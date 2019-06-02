package com.perrigogames.life4trials.manager

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.TrialData
import com.perrigogames.life4trials.data.TrialRank
import com.perrigogames.life4trials.data.TrialSession
import com.perrigogames.life4trials.db.SongDB
import com.perrigogames.life4trials.db.TrialSessionDB
import com.perrigogames.life4trials.db.TrialSessionDB_
import com.perrigogames.life4trials.event.SavedRankUpdatedEvent
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.loadRawString
import io.objectbox.kotlin.query

class TrialManager(private val context: Context) {

    private var trialData: TrialData
    val trials get() = trialData.trials

    var currentSession: TrialSession? = null

    init {
        trialData = DataUtil.gson.fromJson(context.loadRawString(R.raw.trials), TrialData::class.java)!!
        if (BuildConfig.DEBUG) {
            val debugData: TrialData = DataUtil.gson.fromJson(context.loadRawString(R.raw.trials_debug), TrialData::class.java)!!
            val placements = context.life4app.placementManager.placements
            trialData = TrialData(trialData.trials + debugData.trials + placements)
        }
    }

    private val objectBox get() = Life4Application.objectBox

    private val sessionBox get() = objectBox.boxFor(TrialSessionDB::class.java)
    private val songBox get() = objectBox.boxFor(SongDB::class.java)

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
                SongDB.from(result, idx).also {
                    it.session.target = sessionDB
                }
            } else null
        }.filterNotNull())
        sessionBox.put(sessionDB)
        Life4Application.eventBus.post(SavedRankUpdatedEvent(session.trial))
    }

    fun deleteRecord(id: Long) {
        sessionBox.get(id).songs.forEach { songBox.remove(it.id) }
        sessionBox.remove(id)
    }

    fun trialRecords(trialId: String): List<TrialSessionDB> {
        sessionBox.query {
            return equal(TrialSessionDB_.trialId, trialId).build().find()
        }
        return emptyList()
    }

    fun clearRecords() {
        AlertDialog.Builder(context)
            .setTitle(R.string.are_you_sure)
            .setMessage(R.string.confirm_erase_data)
            .setPositiveButton(R.string.yes) { _, _ ->
                sessionBox.removeAll()
                songBox.removeAll()
                Life4Application.eventBus.post(SavedRankUpdatedEvent())
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    fun bestTrial(trialId: String): TrialSessionDB? {
        sessionBox.query {
            val results = equal(TrialSessionDB_.trialId, trialId)
                .equal(TrialSessionDB_.goalObtained, true)
                .sort { o1, o2 -> o2.goalRankId.compareTo(o1.goalRankId)}
                .build()
                .find()
            return results.firstOrNull()
        }
        return null
    }

    fun getRankForTrial(trialId: String): TrialRank? {
        return bestTrial(trialId)?.goalRank
    }

    fun startSession(trialId: String, initialGoal: TrialRank): TrialSession {
        currentSession = TrialSession(findTrial(trialId)!!, initialGoal)
        return currentSession!!
    }
}