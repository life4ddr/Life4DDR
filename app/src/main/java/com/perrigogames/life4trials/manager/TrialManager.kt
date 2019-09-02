package com.perrigogames.life4trials.manager

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.crashlytics.android.Crashlytics
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.api.GithubDataAPI
import com.perrigogames.life4trials.api.LocalRemoteData
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
import com.perrigogames.life4trials.util.loadRawString
import io.objectbox.kotlin.query

class TrialManager(private val context: Context,
                   private val githubDataAPI: GithubDataAPI): BaseManager() {

    private var trialData = object: LocalRemoteData<TrialData>(context, R.raw.trials_debug, TRIALS_FILE_NAME) {
        override fun createLocalDataFromText(text: String): TrialData {
            var out = DataUtil.gson.fromJson(text, TrialData::class.java)!!
            if (BuildConfig.DEBUG) {
                val debugData: TrialData = DataUtil.gson.fromJson(context.loadRawString(R.raw.trials_debug), TrialData::class.java)!!
                val placements = context.life4app.placementManager.placements
                out = TrialData(out.version, out.trials + debugData.trials + placements)
            }
            return out
        }

        override suspend fun getRemoteResponse() = githubDataAPI.getTrials()

        override fun getDataVersion(data: TrialData) = data.version

        override fun onFetchUpdated(data: TrialData) {
            super.onFetchUpdated(data)
            Toast.makeText(context, "${data.trials.size} Trials found!", Toast.LENGTH_SHORT).show()
            Life4Application.eventBus.post(TrialListReplacedEvent())
        }
    }

    var currentSession: TrialSession? = null

    val trials get() = trialData.data.trials

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

    fun previousTrial(id: String) = previousTrial(trials.indexOfFirst { it.id == id })

    fun previousTrial(index: Int) = trials.getOrNull(index - 1)

    fun nextTrial(id: String) = nextTrial(trials.indexOfFirst { it.id == id })

    fun nextTrial(index: Int) = trials.getOrNull(index + 1)

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
            results.firstOrNull { db -> db.trialId == it.id }
        }
    }

    fun getRankForTrial(trialId: String): TrialRank? {
        return bestTrial(trialId)?.goalRank
    }

    fun startSession(trialId: String, initialGoal: TrialRank): TrialSession {
        currentSession = TrialSession(findTrial(trialId)!!, initialGoal)
        return currentSession!!
    }

    companion object {
        const val TRIALS_FILE_NAME = "trials.json"
    }
}