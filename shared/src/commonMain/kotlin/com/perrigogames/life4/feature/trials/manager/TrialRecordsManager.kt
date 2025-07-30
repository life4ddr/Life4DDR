package com.perrigogames.life4.feature.trials.manager

import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.data.SongResult
import com.perrigogames.life4.db.SelectBestSessions
import com.perrigogames.life4.db.TrialSong
import com.perrigogames.life4.feature.trials.data.Trial
import com.perrigogames.life4.feature.trials.data.TrialDatabaseHelper
import com.perrigogames.life4.feature.trials.enums.TrialRank
import com.perrigogames.life4.model.BaseModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface TrialRecordsManager {
    val bestSessions: StateFlow<List<SelectBestSessions>>

    fun saveSession(record: InProgressTrialSession, targetRank: TrialRank)

    fun saveSessions(records: List<Pair<InProgressTrialSession, TrialRank>>)

    fun saveFakeSession(
        trial: Trial,
        targetRank: TrialRank,
        exScore: Int,
    )

    fun deleteSession(sessionId: Long)

    fun deleteSessions(trialId: String)

    fun clearSessions()

    fun getSongsForSession(sessionId: Long): List<TrialSong>
}

class DefaultTrialRecordsManager: BaseModel(), TrialRecordsManager {

    private val dbHelper: TrialDatabaseHelper by inject()

    private val _refresh = MutableSharedFlow<Unit>()
    private val _bestSessions : MutableStateFlow<List<SelectBestSessions>> = MutableStateFlow(emptyList())
    override val bestSessions: StateFlow<List<SelectBestSessions>> get() = _bestSessions.asStateFlow()

    init {
        mainScope.launch {
            _refresh.map { dbHelper.bestSessions() }
                .collect(_bestSessions)
        }
        refreshSessions()
    }

    private fun refreshSessions() {
        mainScope.launch {
            _refresh.emit(Unit)
        }
    }

    override fun saveSession(
        record: InProgressTrialSession,
        targetRank: TrialRank
    ) {
        mainScope.launch {
            dbHelper.insertSession(record, targetRank)
            refreshSessions()
        }
    }

    override fun saveSessions(records: List<Pair<InProgressTrialSession, TrialRank>>) {
        mainScope.launch {
            records.forEach { (session, targetRank) ->
                dbHelper.insertSession(session, targetRank)
            }
            refreshSessions()
        }
    }

    override fun saveFakeSession(
        trial: Trial,
        targetRank: TrialRank,
        exScore: Int,
    ) {
        val songCount = trial.songs.size
        val exRatio = exScore / trial.totalEx.toDouble()
        var remainingEx = exScore

        saveSession(
            record = InProgressTrialSession(
                trial = trial,
                results = trial.songs.mapIndexed { idx, song ->
                    val ex = if (idx == songCount - 1) {
                        remainingEx
                    } else {
                        (song.ex * exRatio).toInt()
                    }
                    remainingEx -= ex
                    SongResult(
                        song = song,
                        exScore = ex
                    )
                }.toTypedArray()
            ).also { it.goalObtained = true },
            targetRank = targetRank
        )
    }

    override fun deleteSession(sessionId: Long) {
        mainScope.launch {
            dbHelper.deleteSession(sessionId)
            refreshSessions()
        }
    }

    override fun deleteSessions(trialId: String) {
        mainScope.launch {
            dbHelper.deleteSessions(trialId)
            refreshSessions()
        }
    }

    override fun clearSessions() {
        mainScope.launch {
            dbHelper.deleteAll()
            refreshSessions()
        }
    }

    override fun getSongsForSession(sessionId: Long) = dbHelper.songsForSession(sessionId)
}