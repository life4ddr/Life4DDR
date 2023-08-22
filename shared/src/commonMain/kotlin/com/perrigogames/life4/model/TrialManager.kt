package com.perrigogames.life4.model

import com.perrigogames.life4.*
import com.perrigogames.life4.api.TrialRemoteData
import com.perrigogames.life4.api.base.CompositeData
import com.perrigogames.life4.api.base.LocalDataReader
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.data.TrialData
import com.perrigogames.life4.db.TrialDatabaseHelper
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.TRIALS_FILE_NAME
import com.perrigogames.life4.viewmodel.TrialListState
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import org.koin.core.component.inject
import org.koin.core.qualifier.named

/**
 * Manages data relating to Trials.  This includes:
 * - the Trials themselves
 * - the current Trial in progress (the 'session')
 * - records for Trials the player has previously completed ('records')
 */
class TrialManager: BaseModel(), CompositeData.NewDataListener<TrialData> {

    private val settings: Settings by inject()
    private val notifications: Notifications by inject()
    private val dbHelper: TrialDatabaseHelper by inject()
    private val dataReader: LocalDataReader by inject(named(TRIALS_FILE_NAME))

    private var trialData = TrialRemoteData(dataReader, this)

    override fun onDataVersionChanged(data: TrialData) {
        notifications.showToast("${data.trials.size} Trials found!")
        _trialsFlow.value = trialData.data.trials
    }

    override fun onMajorVersionBlock() {
        // FIXME eventBus.postSticky(DataRequiresAppUpdateEvent())
    }

    val dataVersionString get() = trialData.versionString

    val trials get() = trialsFlow.value
    val hasEventTrial get() = trials.count { it.isActiveEvent } > 0

    private val _trialsFlow: MutableLiveData<List<Trial>> = MutableLiveData(emptyList())
    val trialsFlow: LiveData<List<Trial>> = _trialsFlow

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

    fun previousTrial(id: String) = previousTrial(trials.indexOfFirst { it.id == id })

    fun previousTrial(index: Int) = trials.getOrNull(index - 1)

    fun nextTrial(id: String) = nextTrial(trials.indexOfFirst { it.id == id })

    fun nextTrial(index: Int) = trials.getOrNull(index + 1)

    fun createViewState() = TrialListState(
        trials = trials,
//        sessions = bestSessions(), FIXME
        sessions = emptyList(),
        featureNew = settings.getBoolean(SettingsKeys.KEY_LIST_HIGHLIGHT_NEW, true),
        featureUnplayed = settings.getBoolean(SettingsKeys.KEY_LIST_HIGHLIGHT_UNPLAYED, true),
    )

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
