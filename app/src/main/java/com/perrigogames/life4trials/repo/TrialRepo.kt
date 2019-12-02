package com.perrigogames.life4trials.repo

import com.perrigogames.life4trials.data.TrialRank
import com.perrigogames.life4trials.data.TrialSession
import com.perrigogames.life4trials.db.TrialSessionDB
import com.perrigogames.life4trials.db.TrialSessionDB_
import com.perrigogames.life4trials.db.TrialSongResultDB
import com.perrigogames.life4trials.manager.BaseManager
import io.objectbox.kotlin.query

class TrialRepo: BaseManager() {

    private val sessionBox get() = objectBox.boxFor(TrialSessionDB::class.java)
    private val songBox get() = objectBox.boxFor(TrialSongResultDB::class.java)

    val allRecords: List<TrialSessionDB> get() = sessionBox.all

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
    }

    fun deleteRecord(id: Long) {
        sessionBox.get(id).songResults.forEach { songBox.remove(it.id) }
        sessionBox.remove(id)
    }

    fun clearRecords() {
        sessionBox.removeAll()
        songBox.removeAll()
    }

    fun trialRecords(trialId: String): List<TrialSessionDB> {
        sessionBox.query {
            return equal(TrialSessionDB_.trialId, trialId).build().find()
        }
        return emptyList()
    }

    fun bestTrial(trialId: String): TrialSessionDB? = sessionBox.query()
        .equal(TrialSessionDB_.trialId, trialId)
        .equal(TrialSessionDB_.goalObtained, true)
        .sort { o1, o2 -> o2.goalRankId.compareTo(o1.goalRankId)}
        .build()
        .find()
        .firstOrNull()

    fun bestTrials(): List<TrialSessionDB> = sessionBox.query()
        .equal(TrialSessionDB_.goalObtained, true)
        .sort { o1, o2 -> o2.goalRankId.compareTo(o1.goalRankId)}
        .build()
        .find()

    fun getRankForTrial(trialId: String): TrialRank? = bestTrial(trialId)?.goalRank
}