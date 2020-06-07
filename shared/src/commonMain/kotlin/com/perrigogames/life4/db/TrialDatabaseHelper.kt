package com.perrigogames.life4.db

import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.data.TrialRank
import com.soywiz.klock.DateTime
import com.soywiz.klock.ISO8601
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrialDatabaseHelper(sqlDriver: SqlDriver): DatabaseHelper(sqlDriver) {

    private val queries get() = dbRef.trialQueries

    fun allRecords(): Query<TrialSession> = queries.selectAll()

    suspend fun insertSession(session: InProgressTrialSession, datetime: DateTime? = null) = withContext(Dispatchers.Default) {
        dbRef.transaction {
            queries.insertSession(null,
                session.trial.id,
                (datetime ?: DateTime.now()).format(ISO8601.DATETIME_COMPLETE),
                session.goalRank!!,
                session.goalObtained)
            val sId = queries.lastInsertRowId().executeAsOne()
            session.results.forEachIndexed { idx, result ->
                queries.insertSong(null, sId,
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
        queries.deleteSession(id)
    }

    suspend fun deleteAll() = withContext(Dispatchers.Default) {
        queries.deleteAll()
    }

    fun sessionsForTrial(trialId: String) = queries.selectByTrialId(trialId).executeAsList()

    fun bestSession(trialId: String) = queries.selectBestSession(trialId).executeAsOneOrNull()

    fun bestSessions() = queries.selectBestSessions().executeAsList()

    fun songsForSession(sessionId: Long) = queries.selectSessionSongs(sessionId).executeAsList()
}
