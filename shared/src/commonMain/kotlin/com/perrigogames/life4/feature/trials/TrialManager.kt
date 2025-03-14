package com.perrigogames.life4.feature.trials

import co.touchlab.kermit.Logger
import com.perrigogames.life4.AppInfo
import com.perrigogames.life4.api.TrialRemoteData
import com.perrigogames.life4.api.base.CompositeData
import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.feature.songlist.SongDataManager
import com.perrigogames.life4.feature.trialrecords.TrialDatabaseHelper
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.model.BaseModel
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.inject

/**
 * Manages data relating to Trials.  This includes:
 * - the Trials themselves
 * - the current Trial in progress (the 'session')
 * - records for Trials the player has previously completed ('records')
 */
class TrialManager: BaseModel() {

    private val appInfo: AppInfo by inject()
    private val settings: Settings by inject()
    private val songDataManager: SongDataManager by inject()
    private val dbHelper: TrialDatabaseHelper by inject()
    private val logger: Logger by injectLogger("TrialManager")

    private var data = TrialRemoteData()

    val dataVersionString: Flow<String> =
        data.versionState.map { it.versionString }

    val trials get() = trialsFlow.value
    val hasEventTrial get() = trials.count { it.isActiveEvent } > 0

    private val _trialsFlow = MutableStateFlow<List<Trial>>(emptyList()).cMutableStateFlow()
    val trialsFlow: StateFlow<List<Trial>> = _trialsFlow

    init {
        validateTrials()

        mainScope.launch {
            data.dataState
                .mapNotNull { (it as? CompositeData.LoadingState.Loaded)?.data?.trials }
                .onEach { trials ->
                    trials.forEach { trial ->
                        trial.songs.forEach { song ->
                            song.chart = songDataManager.getChart(
                                skillId = song.skillId,
                                playStyle = song.playStyle,
                                difficultyClass = song.difficultyClass,
                            ) ?: throw IllegalStateException("Chart not found for ${song.skillId}, ${song.playStyle}, ${song.difficultyClass}")
                        }
                    }
                }
                .collect(_trialsFlow)
        }
        mainScope.launch {
            data.start()
        }
    }

    private fun validateTrials() = trials.forEach { trial ->
        var sum = 0
        trial.songs.forEach { sum += it.ex }
        if (sum != trial.totalEx) {
            if (!appInfo.isDebug) {
                logger.e { "Trial ${trial.name} has improper EX values: total_ex=${trial.totalEx}, sum=$sum" }
            }
        }
    }

    fun findTrial(id: String) = trials.firstOrNull { it.id == id }

    /**
     * Commits the current session to internal storage.  [currentSession] is
     * no longer usable after calling this.
     */
    fun saveSession(session: InProgressTrialSession) {
        mainScope.launch {
            dbHelper.insertSession(session)
        }
    }
}
