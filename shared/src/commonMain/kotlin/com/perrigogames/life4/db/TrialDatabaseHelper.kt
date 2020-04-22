package com.perrigogames.life4.db

import com.perrigogames.life4.Life4Db
import com.perrigogames.life4.data.StableIdColumnAdapter
import com.perrigogames.life4.enums.GoalStatus
import com.soywiz.klock.DateTime
import com.soywiz.klock.ISO8601
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrialDatabaseHelper(private val sqlDriver: SqlDriver) {
    private val dbRef = Life4Db(sqlDriver, GoalState.Adapter(StableIdColumnAdapter(GoalStatus.values())))

    internal fun dbClear() {
        sqlDriver.close()
    }

    fun allRecords(): Query<TrialSession> = dbRef.trialQueries.selectAll()

    suspend fun insertSession(session: com.perrigogames.life4.data.TrialSession, datetime: DateTime? = null) = withContext(Dispatchers.Default) {
        dbRef.transaction {
            dbRef.trialQueries.insertSession(null,
                session.trial.id,
                (datetime ?: DateTime.now()).format(ISO8601.DATETIME_COMPLETE),
                session.goalRank!!.stableId,
                session.goalObtained)
            session.results.forEachIndexed { idx, result ->
                dbRef.trialQueries.insertSong(null,
                    0L, //FIXME
                    idx.toLong(),
                    result!!.score!!.toLong(),
                    result.exScore!!.toLong(),
                    result.misses!!.toLong(),
                    result.goods!!.toLong(),
                    result.greats!!.toLong(),
                    result.perfects!!.toLong(),
                    result.passed)
            }
        }
    }

    suspend fun deleteSession(id: Long) = withContext(Dispatchers.Default) {
        dbRef.trialQueries.deleteSession(id)
    }

    suspend fun deleteAll() = withContext(Dispatchers.Default) {
        dbRef.trialQueries.deleteAll()
    }

//    fun bestTrial(trialId: String): TrialSessionDB? = sessionBox.query()
//        .equal(TrialSessionDB_.trialId, trialId)
//        .equal(TrialSessionDB_.goalObtained, true)
//        .sort { o1, o2 -> o2.goalRankId.compareTo(o1.goalRankId)}
//        .build()
//        .find()
//        .firstOrNull()
//
//    fun bestTrials(): List<TrialSessionDB> = sessionBox.query()
//        .equal(TrialSessionDB_.goalObtained, true)
//        .sort { o1, o2 -> o2.goalRankId.compareTo(o1.goalRankId)}
//        .build()
//        .find()
//
//    fun getRankForTrial(trialId: String): TrialRank? = bestTrial(trialId)?.goalRank
}
