package com.perrigogames.life4trials.manager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.crashlytics.android.Crashlytics
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity
import com.perrigogames.life4trials.api.GithubDataAPI
import com.perrigogames.life4trials.api.MajorVersionedRemoteData
import com.perrigogames.life4trials.data.TrialData
import com.perrigogames.life4trials.data.TrialRank
import com.perrigogames.life4trials.data.TrialSession
import com.perrigogames.life4trials.db.TrialSessionDB
import com.perrigogames.life4trials.db.TrialSessionDB_
import com.perrigogames.life4trials.db.TrialSongResultDB
import com.perrigogames.life4trials.event.SavedRankUpdatedEvent
import com.perrigogames.life4trials.event.TrialListReplacedEvent
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.NotificationUtil
import com.perrigogames.life4trials.util.SharedPrefsUtil
import com.perrigogames.life4trials.util.loadRawString
import io.objectbox.kotlin.query

class TrialManager(private val context: Context,
                   private val githubDataAPI: GithubDataAPI): BaseManager() {

    private var trialData = object: MajorVersionedRemoteData<TrialData>(context, R.raw.trials, TRIALS_FILE_NAME, 2) {
        override fun createLocalDataFromText(text: String): TrialData =
            mergeDebugData(DataUtil.gson.fromJson(text, TrialData::class.java)!!)

        override suspend fun getRemoteResponse() = githubDataAPI.getTrials()

        override fun onFetchUpdated(data: TrialData) {
            super.onFetchUpdated(data)
            this.data = mergeDebugData(data)
            Toast.makeText(context, "${data.trials.size} Trials found!", Toast.LENGTH_SHORT).show()
            Life4Application.eventBus.post(TrialListReplacedEvent())
        }

        private fun mergeDebugData(data: TrialData): TrialData = if (BuildConfig.DEBUG) {
            val debugData: TrialData = DataUtil.gson.fromJson(context.loadRawString(R.raw.trials_debug), TrialData::class.java)!!
            val placements = context.life4app.placementManager.placements
            TrialData(data.version, data.majorVersion,data.trials + debugData.trials + placements)
        } else data
    }

    var currentSession: TrialSession? = null

    val trials get() = trialData.data.trials
    val activeTrials get() = trials.filter { !it.isEvent || it.isActiveEvent }
    val hasEventTrial get() = trials.count { it.isActiveEvent } > 0
    val eventTrials get() = trials.filter { it.isEvent }

    init {
        trialData.start()
        validateTrials()
    }

    private fun validateTrials() = trials.forEach { trial ->
        if (trial.songs.count { it.ex != null } == 4) {
            var sum = 0
            trial.songs.forEach { sum += it.ex!! }
            if (sum != trial.total_ex) {
                Crashlytics.logException(Exception("Trial ${trial.name} has improper EX values: total_ex=${trial.total_ex}, sum=$sum"))
            }
        }
    }

    private val sessionBox get() = objectBox.boxFor(TrialSessionDB::class.java)
    private val songBox get() = objectBox.boxFor(TrialSongResultDB::class.java)

    val records: List<TrialSessionDB> get() = sessionBox.all

    fun findTrial(id: String) = trials.firstOrNull { it.id == id }

    fun previousTrial(id: String) = previousTrial(activeTrials.indexOfFirst { it.id == id })

    fun previousTrial(index: Int) = activeTrials.getOrNull(index - 1)

    fun nextTrial(id: String) = nextTrial(activeTrials.indexOfFirst { it.id == id })

    fun nextTrial(index: Int) = activeTrials.getOrNull(index + 1)

    fun saveRecord(session: TrialSession) {
        val sessionDB = TrialSessionDB.from(session)
        songBox.put(session.results.mapIndexed { idx, result ->
            if (result != null) {
                TrialSongResultDB.from(result, idx).also {
                    it.session.target = sessionDB
                }
            } else null
        }.filterNotNull())
        sessionBox.put(sessionDB)
        Life4Application.eventBus.post(SavedRankUpdatedEvent(session.trial))
    }

    fun deleteRecord(id: Long) {
        sessionBox.get(id).songResults.forEach { songBox.remove(it.id) }
        sessionBox.remove(id)
    }

    fun trialRecords(trialId: String): List<TrialSessionDB> {
        sessionBox.query {
            return equal(TrialSessionDB_.trialId, trialId).build().find()
        }
        return emptyList()
    }

    fun clearRecords(c: Context) {
        AlertDialog.Builder(c)
            .setTitle(R.string.are_you_sure)
            .setMessage(R.string.confirm_erase_trial_data)
            .setPositiveButton(R.string.yes) { _, _ ->
                sessionBox.removeAll()
                songBox.removeAll()
                Life4Application.eventBus.post(SavedRankUpdatedEvent())
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    fun bestTrial(trialId: String): TrialSessionDB? = sessionBox.query()
        .equal(TrialSessionDB_.trialId, trialId)
        .equal(TrialSessionDB_.goalObtained, true)
        .sort { o1, o2 -> o2.goalRankId.compareTo(o1.goalRankId)}
        .build()
        .find()
        .firstOrNull()

    fun bestTrials(): List<TrialSessionDB> {
        val results = sessionBox.query()
            .equal(TrialSessionDB_.goalObtained, true)
            .sort { o1, o2 -> o2.goalRankId.compareTo(o1.goalRankId)}
            .build()
            .find()
        return trials.mapNotNull {
            if (it.isEvent) null
            else results.firstOrNull { db -> db.trialId == it.id }
        }
    }

    fun getRankForTrial(trialId: String): TrialRank? {
        return bestTrial(trialId)?.goalRank
    }

    fun startSession(trialId: String, initialGoal: TrialRank?): TrialSession {
        currentSession = TrialSession(findTrial(trialId)!!, initialGoal)
        return currentSession!!
    }

    fun submitResult(context: Context, session: TrialSession = currentSession!!, onFinish: () -> Unit) {
        when {
            session.results.any { it?.passed != true } -> submitRankAndFinish(context, session, false, onFinish)
            session.trial.isEvent -> submitRankAndFinish(context, session, true, onFinish)
            else -> AlertDialog.Builder(context)
                .setTitle(R.string.trial_submit_dialog_title)
                .setMessage(context.getString(R.string.trial_submit_dialog_rank_confirmation, session.goalRank.toString()))
                .setPositiveButton(R.string.yes) { _, _ -> submitRankAndFinish(context, session, true, onFinish) }
                .setNegativeButton(R.string.no) { _, _ -> submitRankAndFinish(context, session, false, onFinish) }
                .show()
        }
    }

    private fun submitRankAndFinish(context: Context, session: TrialSession, passed: Boolean, onFinish: () -> Unit) {
        session.goalObtained = passed
        saveRecord(session)
        if (passed) {
            AlertDialog.Builder(context)
                .setTitle(R.string.trial_submit_dialog_title)
                .setMessage(R.string.trial_submit_dialog_prompt)
                .setCancelable(false)
                .setNegativeButton(R.string.no) { _, _ -> onFinish() }
                .setPositiveButton(R.string.yes) { _, _ ->
                    if (SharedPrefsUtil.getUserFlag(context, SettingsActivity.KEY_SUBMISSION_NOTIFICAION, false)) {
                        NotificationUtil.showUserInfoNotifications(context, session.totalExScore)
                    }
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.url_trial_submission_form))))
                    onFinish()
                }
                .show()
        } else {
            onFinish()
        }
    }

    override fun onApplicationException() {
        Crashlytics.setInt("trials_version", trialData.data.version)
        Crashlytics.setInt("trials_major_version", trialData.data.majorVersion)
        Crashlytics.setInt("trials_engine", trialData.majorVersion)
        Crashlytics.setString("trials", trials.joinToString { it.id })
    }

    companion object {
        const val TRIALS_FILE_NAME = "trials.json"
    }
}