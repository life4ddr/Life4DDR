package com.perrigogames.life4.model

import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.db.SelectBestSessions
import com.perrigogames.life4.db.TrialDatabaseHelper
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TrialRecordsManager: BaseModel(), KoinComponent {
//    private val settings: Settings by inject()
    private val dbHelper: TrialDatabaseHelper by inject()
//    private val life4Api: Life4API by inject()

//    fun getRecordsFromNetwork() {
//        fun isRecordListStale(now: Long): Boolean {
//            val lastDownloadTimeMS = settings.getLong(TrialManager.RECORD_FETCH_TIMESTAMP_KEY, 0)
//            val oneDayMS = 24 * 60 * 60 * 1000
//            return (lastDownloadTimeMS + oneDayMS < now)
//        }
//
//        val currentTimeMS = currentTimeMillis()
//        if (isRecordListStale(currentTimeMS)) {
//            ktorScope.launch {
//                try {
//                    val result = life4Api.getRecords()
//                    insertRecords(result.records)
//                    settings.putLong(TrialManager.RECORD_FETCH_TIMESTAMP_KEY, currentTimeMS)
//                } catch (e:Exception){
//                    //FIXME listen to success or failure
//                }
//            }
//        }
//    }

    private fun insertRecords(records: List<InProgressTrialSession>) {
        mainScope.launch {
            records.forEach {
                dbHelper.insertSession(it)
            }
        }
    }

    fun bestSessions(): List<SelectBestSessions> {
        val results = dbHelper.bestSessions()
//        return trials.mapNotNull {
//            if (it.isEvent) null
//            else results.firstOrNull { db -> db.trialId == it.id }
//        } FIXME
        return emptyList()
    }

    fun deleteSession(sessionId: Long) {
        mainScope.launch {
            dbHelper.deleteSession(sessionId)
        }
    }

    fun clearSessions() {
        mainScope.launch {
            dbHelper.deleteAll()
        }
    }

    fun getSongsForSession(sessionId: Long) = dbHelper.songsForSession(sessionId)
}