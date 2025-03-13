package com.perrigogames.life4.feature.trialrecords

import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.db.DatabaseHelper
import com.perrigogames.life4.db.TrialSession
import com.perrigogames.life4.db.TrialSong
import com.perrigogames.life4.enums.TrialRank
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class TrialDatabaseHelper(sqlDriver: SqlDriver): DatabaseHelper(sqlDriver) {

    private val queries get() = dbRef.trialQueries

    fun allRecords(): Query<TrialSession> = queries.selectAllSessions()

    fun allSongs(): Query<TrialSong> = queries.selectAllSongs()

    suspend fun insertSession(
        session: InProgressTrialSession,
        datetime: Instant? = null
    ) = withContext(Dispatchers.Default) {
        queries.insertSession(null,
            session.trial.id,
            (datetime ?: Clock.System.now()).toString(),
            session.targetRank!!,
            session.goalObtained)
        val sId = queries.lastInsertRowId().executeAsOne()
        dbRef.transaction {
            session.results.forEachIndexed { idx, result ->
                queries.insertSong(null, sId,
                    idx.toLong(),
                    result!!.score!!.toLong(),
                    result.exScore!!.toLong(),
                    result.misses?.toLong(),
                    result.goods?.toLong(),
                    result.greats?.toLong(),
                    result.perfects?.toLong(),
                    result.passed)
            }
        }
    }

    suspend fun deleteSession(id: Long) = withContext(Dispatchers.Default) {
        queries.deleteSession(id)
    }

    suspend fun deleteAll() = withContext(Dispatchers.Default) {
        queries.deleteAllSongs()
        queries.deleteAllSessions()
    }

    fun sessionsForTrial(trialId: String) = queries.selectSessionByTrialId(trialId).executeAsList()

    fun bestSession(trialId: String) = queries.selectBestSession(trialId).executeAsOneOrNull()

    fun bestSessions() = queries.selectBestSessions().executeAsList()

    fun songsForSession(sessionId: Long) = queries.selectSessionSongs(sessionId).executeAsList()

    fun createRecordExportStrings(): List<String> = allRecords().executeAsList().map {
        (listOf(it.trialId, it.date, it.goalRank.stableId.toString(), it.goalObtained.toString()) +
                songsForSession(it.id).joinToString("\t") {
                        song -> "${song.score}\t${song.exScore}\t${song.misses}\t${song.goods}\t${song.greats}\t${song.perfects}\t${song.passed}"
                }).joinToString("\t")
    }

    fun importRecordExportStrings(input: List<String>) {
        val songs = allSongs().executeAsList()
        val records = allRecords().executeAsList()
            .associateWith { session -> songs.filter { session.id == it.sessionId } }
        dbRef.transaction {
            input.forEach { line ->
                val segs = line.split('\t').toMutableList()
                val trialId = segs.removeAt(0)
                val date = segs.removeAt(0)
                val goalRank = TrialRank.parse(segs.removeAt(0).toLong())!!
                val goalObtained = segs.removeAt(0).toBoolean()

                val newSongs = mutableListOf<TrialInputSong>()
                while(segs.isNotEmpty()) {
                    newSongs.add(
                        TrialInputSong(
                        segs.removeAt(0).toLong(),
                        segs.removeAt(0).toLong(),
                        segs.removeAt(0).toLong(),
                        segs.removeAt(0).toLong(),
                        segs.removeAt(0).toLong(),
                        segs.removeAt(0).toLong(),
                        segs.removeAt(0).toBoolean())
                    )
                }
                val matches = records.any { record ->
                    record.key.trialId == trialId && record.value.map { it.exScore } == newSongs.map { it.exScore }
                }
                if (!matches) {
                    queries.insertSession(null, trialId, date, goalRank, goalObtained)
                    val sId = queries.lastInsertRowId().executeAsOne()
                    var idx = 0L
                    newSongs.forEach { queries.insertSong(null, sId, idx++, it.score, it.exScore, it.misses, it.goods, it.greats, it.perfects, it.passed) }
                }
            }
        }
    }
}

class TrialInputSong(
    val score: Long,
    val exScore: Long,
    val misses: Long,
    val goods: Long,
    val greats: Long,
    val perfects: Long,
    val passed: Boolean,
)