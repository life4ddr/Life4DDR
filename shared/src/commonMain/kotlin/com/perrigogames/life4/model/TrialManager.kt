package com.perrigogames.life4.model

import com.perrigogames.life4.*
import com.perrigogames.life4.api.TrialRemoteData
import com.perrigogames.life4.api.base.CompositeData
import com.perrigogames.life4.api.base.LocalDataReader
import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.data.TrialData
import com.perrigogames.life4.db.SelectBestSessions
import com.perrigogames.life4.db.TrialDatabaseHelper
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.TRIALS_FILE_NAME
import com.perrigogames.life4.ktor.Life4API
import com.russhwolf.settings.Settings
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.core.component.inject
import org.koin.core.qualifier.named

/**
 * Manages data relating to Trials.  This includes:
 * - the Trials themselves
 * - the current Trial in progress (the 'session')
 * - records for Trials the player has previously completed ('records')
 */
@OptIn(ExperimentalSerializationApi::class)
class TrialManager: BaseModel() {

    private val settings: Settings by inject()
    private val eventBus: EventBusNotifier by inject()
    private val notifications: Notifications by inject()
    private val dbHelper: TrialDatabaseHelper by inject()
    private val life4Api: Life4API by inject()
    private val dataReader: LocalDataReader by inject(named(TRIALS_FILE_NAME))

    private var trialData = TrialRemoteData(dataReader, object: CompositeData.NewDataListener<TrialData> {
        override fun onDataVersionChanged(data: TrialData) {
            notifications.showToast("${data.trials.size} Trials found!")
            eventBus.post(TrialListReplacedEvent())
        }

        override fun onMajorVersionBlock() = eventBus.postSticky(DataRequiresAppUpdateEvent())
    })
    val dataVersionString get() = trialData.versionString

    val trials get() = trialData.data.trials
    val activeTrials get() = trials.filter { !it.isEvent || it.isActiveEvent }
    val hasEventTrial get() = trials.count { it.isActiveEvent } > 0
    val eventTrials get() = trials.filter { it.isEvent }

    init {
        trialData.start()
        validateTrials()
    }

    val allRecords get() = dbHelper.allRecords().executeAsList()

    private fun validateTrials() = trials.forEach { trial ->
        var sum = 0
        trial.songs.forEach { sum += it.ex }
        if (sum != trial.totalEx) {
            if (!isDebug) {
                logException(Exception("Trial ${trial.name} has improper EX values: total_ex=${trial.totalEx}, sum=$sum"))
            }
        }
    }

    fun findTrial(id: String) = trials.firstOrNull { it.id == id }

    fun previousTrial(id: String) = previousTrial(activeTrials.indexOfFirst { it.id == id })

    fun previousTrial(index: Int) = activeTrials.getOrNull(index - 1)

    fun nextTrial(id: String) = nextTrial(activeTrials.indexOfFirst { it.id == id })

    fun nextTrial(index: Int) = activeTrials.getOrNull(index + 1)

    fun getRankForTrial(trialId: String) = dbHelper.bestSession(trialId)?.goalRank

    fun bestSession(trialId: String) = dbHelper.bestSession(trialId)

    fun bestSessions(): List<SelectBestSessions> {
        val results = dbHelper.bestSessions()
        return trials.mapNotNull {
            if (it.isEvent) null
            else results.firstOrNull { db -> db.trialId == it.id }
        }
    }

    fun deleteSession(sessionId: Long) {
        mainScope.launch {
            dbHelper.deleteSession(sessionId)
            eventBus.post(SavedRankUpdatedEvent())
        }
    }

    fun clearSessions() {
        mainScope.launch {
            dbHelper.deleteAll()
            eventBus.post(SavedRankUpdatedEvent())
        }
    }

    fun getSongsForSession(sessionId: Long) = dbHelper.songsForSession(sessionId)

    fun getRecordsFromNetwork() {
        fun isRecordListStale(now: Long): Boolean {
            val lastDownloadTimeMS = settings.getLong(RECORD_FETCH_TIMESTAMP_KEY, 0)
            val oneDayMS = 24 * 60 * 60 * 1000
            return (lastDownloadTimeMS + oneDayMS < now)
        }

        val currentTimeMS = currentTimeMillis()
        if (isRecordListStale(currentTimeMS)) {
            ktorScope.launch {
                try {
                    val result = life4Api.getRecords()
                    insertRecords(result.records)
                    settings.putLong(RECORD_FETCH_TIMESTAMP_KEY, currentTimeMS)
                } catch (e:Exception){
                    //FIXME listen to success or failure
                }
            }
        }
    }

    private fun insertRecords(records: List<InProgressTrialSession>) {
        mainScope.launch {
            records.forEach {
                dbHelper.insertSession(it)
            }
        }
    }

    override fun onApplicationException() {
        if (!isDebug) {
            setCrashInt("trials_version", trialData.data.version)
            setCrashInt("trials_major_version", trialData.data.majorVersion)
            setCrashInt("trials_engine", trialData.data.majorVersion)
            setCrashString("trials", trials.joinToString { it.id })
        }
    }

    companion object {
        internal val RECORD_FETCH_TIMESTAMP_KEY = "TRIAL_FETCH_TIMESTAMP_KEY"
    }
}
