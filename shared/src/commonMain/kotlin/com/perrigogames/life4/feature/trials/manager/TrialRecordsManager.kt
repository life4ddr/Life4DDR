package com.perrigogames.life4.feature.trials.manager

import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.db.SelectBestSessions
import com.perrigogames.life4.feature.trials.data.TrialDatabaseHelper
import com.perrigogames.life4.model.BaseModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TrialRecordsManager: BaseModel(), KoinComponent {

    private val dbHelper: TrialDatabaseHelper by inject()

    private val _bestSessions : MutableStateFlow<List<SelectBestSessions>> = MutableStateFlow(emptyList())
    val bestSessions: StateFlow<List<SelectBestSessions>> get() = _bestSessions.asStateFlow()

    init {
        mainScope.launch {
            fetchBestSessions()
        }
    }

    private fun fetchBestSessions() {
        val sessions = dbHelper.bestSessions()
        _bestSessions.value = sessions
    }

    fun saveSession(record: InProgressTrialSession) {
        mainScope.launch {
            dbHelper.insertSession(record)
            fetchBestSessions()
        }
    }

    fun saveSessions(records: List<InProgressTrialSession>) {
        mainScope.launch {
            records.forEach {
                dbHelper.insertSession(it)
            }
            fetchBestSessions()
        }
    }

    fun deleteSession(sessionId: Long) {
        mainScope.launch {
            dbHelper.deleteSession(sessionId)
            fetchBestSessions()
        }
    }

    fun clearSessions() {
        mainScope.launch {
            dbHelper.deleteAll()
            fetchBestSessions()
        }
    }

    fun getSongsForSession(sessionId: Long) = dbHelper.songsForSession(sessionId)
}