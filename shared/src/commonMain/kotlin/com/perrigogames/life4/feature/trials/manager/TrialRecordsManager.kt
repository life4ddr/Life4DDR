package com.perrigogames.life4.feature.trials.manager

import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.db.SelectBestSessions
import com.perrigogames.life4.feature.trials.data.TrialDatabaseHelper
import com.perrigogames.life4.model.BaseModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TrialRecordsManager: BaseModel(), KoinComponent {

    private val dbHelper: TrialDatabaseHelper by inject()

    private val _refresh = MutableSharedFlow<Unit>()
    private val _bestSessions : MutableStateFlow<List<SelectBestSessions>> = MutableStateFlow(emptyList())
    val bestSessions: StateFlow<List<SelectBestSessions>> get() = _bestSessions.asStateFlow()

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

    fun saveSession(record: InProgressTrialSession) {
        mainScope.launch {
            dbHelper.insertSession(record)
            refreshSessions()
        }
    }

    fun saveSessions(records: List<InProgressTrialSession>) {
        mainScope.launch {
            records.forEach {
                dbHelper.insertSession(it)
            }
            refreshSessions()
        }
    }

    fun deleteSession(sessionId: Long) {
        mainScope.launch {
            dbHelper.deleteSession(sessionId)
            refreshSessions()
        }
    }

    fun clearSessions() {
        mainScope.launch {
            dbHelper.deleteAll()
            refreshSessions()
        }
    }

    fun getSongsForSession(sessionId: Long) = dbHelper.songsForSession(sessionId)
}