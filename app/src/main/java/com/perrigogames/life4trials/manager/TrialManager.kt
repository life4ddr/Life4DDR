package com.perrigogames.life4trials.manager

import android.content.Context
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
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.loadRawString
import io.objectbox.kotlin.query

class TrialManager(private val context: Context) {

    private var trialData: TrialData
    val trials get() = trialData.trials

    init {
        trialData = DataUtil.gson.fromJson(context.loadRawString(R.raw.trials), TrialData::class.java)!!
        if (BuildConfig.DEBUG) {
            val debugData: TrialData = DataUtil.gson.fromJson(context.loadRawString(R.raw.trials_debug), TrialData::class.java)!!
            trialData = TrialData(trialData.trials + debugData.trials)
        }
    }

    private val objectBox get() = Life4Application.objectBox

    private val sessionBox get() = objectBox.boxFor(TrialSessionDB::class.java)
    private val songBox get() = objectBox.boxFor(SongDB::class.java)

    val records: List<TrialSessionDB> get() = sessionBox.all

    fun findTrial(id: String) = trials.firstOrNull { it.id == id }

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
        sessionBox.removeAll()
        songBox.removeAll()
        Life4Application.eventBus.post(SavedRankUpdatedEvent())
    }

    fun getRankForTrial(trialId: String): TrialRank? {
        sessionBox.query {
            val results = equal(TrialSessionDB_.trialId, trialId)
                .equal(TrialSessionDB_.goalObtained, true)
                .sort { o1, o2 -> o2.goalRankId.compareTo(o1.goalRankId)}
                .build()
                .find()
            return results.firstOrNull()?.goalRank
        }
        return null
    }
}