package com.perrigogames.life4trials.manager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.crashlytics.android.Crashlytics
import com.perrigogames.life4.SavedRankUpdatedEvent
import com.perrigogames.life4.SettingsKeys.KEY_SUBMISSION_NOTIFICAION
import com.perrigogames.life4.TrialListReplacedEvent
import com.perrigogames.life4.api.FetchListener
import com.perrigogames.life4.api.TrialRemoteData
import com.perrigogames.life4.data.TrialData
import com.perrigogames.life4.data.TrialRank
import com.perrigogames.life4.data.TrialSession
import com.perrigogames.life4.db.TrialDatabaseHelper
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.TRIALS_FILE_NAME
import com.perrigogames.life4.model.BaseModel
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.api.AndroidDataReader
import com.perrigogames.life4trials.db.TrialSessionDB
import com.perrigogames.life4trials.repo.TrialRepo
import com.perrigogames.life4trials.util.NotificationUtil
import org.greenrobot.eventbus.EventBus
import org.koin.core.inject

class TrialManager: BaseModel() {

    private val context: Context by inject()
    private val repo: TrialRepo by inject()
    private val settingsManager: SettingsManager by inject()
    private val eventBus: EventBus by inject()
    private val dbHelper: TrialDatabaseHelper by inject()

    private var trialData = TrialRemoteData(AndroidDataReader(R.raw.trials, TRIALS_FILE_NAME), object: FetchListener<TrialData> {
        override fun onFetchUpdated(data: TrialData) {
            Toast.makeText(context, "${data.trials.size} Trials found!", Toast.LENGTH_SHORT).show()
            eventBus.post(TrialListReplacedEvent())
        }
    })

    var currentSession: TrialSession? = null

    val trials get() = trialData.data.trials
    val activeTrials get() = trials.filter { !it.isEvent || it.isActiveEvent }
    val hasEventTrial get() = trials.count { it.isActiveEvent } > 0
    val eventTrials get() = trials.filter { it.isEvent }

    init {
        trialData.start()
        validateTrials()
    }

    val allRecords get() = repo.allRecords

    private fun validateTrials() = trials.forEach { trial ->
        var sum = 0
        trial.songs.forEach { sum += it.ex }
        if (sum != trial.total_ex) {
            if (!BuildConfig.DEBUG) {
                Crashlytics.logException(Exception("Trial ${trial.name} has improper EX values: total_ex=${trial.total_ex}, sum=$sum"))
            }
        }
    }

    fun findTrial(id: String) = trials.firstOrNull { it.id == id }

    fun previousTrial(id: String) = previousTrial(activeTrials.indexOfFirst { it.id == id })

    fun previousTrial(index: Int) = activeTrials.getOrNull(index - 1)

    fun nextTrial(id: String) = nextTrial(activeTrials.indexOfFirst { it.id == id })

    fun nextTrial(index: Int) = activeTrials.getOrNull(index + 1)

    fun saveRecord(session: TrialSession) {
        repo.saveRecord(session)
        eventBus.post(SavedRankUpdatedEvent(session.trial))
    }

    fun deleteRecord(id: Long) = repo.deleteRecord(id)

    fun getRankForTrial(trialId: String) = repo.getRankForTrial(trialId)

    fun clearRecords(c: Context) {
        AlertDialog.Builder(c)
            .setTitle(R.string.are_you_sure)
            .setMessage(R.string.confirm_erase_trial_data)
            .setPositiveButton(R.string.yes) { _, _ ->
                repo.clearRecords()
                eventBus.post(SavedRankUpdatedEvent())
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    fun bestTrial(trialId: String) = repo.bestTrial(trialId)

    fun bestTrials(): List<TrialSessionDB> {
        val results = repo.bestTrials()
        return trials.mapNotNull {
            if (it.isEvent) null
            else results.firstOrNull { db -> db.trialId == it.id }
        }
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
                    if (settingsManager.getUserFlag(KEY_SUBMISSION_NOTIFICAION, false)) {
                        NotificationUtil.showUserInfoNotifications(context, session.currentTotalExScore)
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
        if (!BuildConfig.DEBUG) {
            Crashlytics.setInt("trials_version", trialData.data.version)
            Crashlytics.setInt("trials_major_version", trialData.data.majorVersion)
            Crashlytics.setInt("trials_engine", trialData.majorVersion)
            Crashlytics.setString("trials", trials.joinToString { it.id })
        }
    }
}
