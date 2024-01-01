package com.perrigogames.life4.viewmodel

import com.perrigogames.life4.SettingsKeys.KEY_LIST_HIGHLIGHT_NEW
import com.perrigogames.life4.SettingsKeys.KEY_LIST_HIGHLIGHT_UNPLAYED
import com.perrigogames.life4.SettingsKeys.KEY_LIST_SHOW_EX
import com.perrigogames.life4.SettingsKeys.KEY_LIST_SHOW_EX_REMAINING
import com.perrigogames.life4.SettingsKeys.KEY_LIST_TINT_COMPLETED
import com.perrigogames.life4.feature.trials.TrialManager
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TrialListViewModel : ViewModel(), KoinComponent {

    private val trialManager: TrialManager by inject()
    private val settings: Settings by inject()

    private val _state = MutableStateFlow<TrialListState?>(null).cMutableStateFlow()
    private val _trials = _state.map { it?.displayTrials ?: emptyList() }
        .stateIn(viewModelScope, started = SharingStarted.Lazily, emptyList())
    val trials: StateFlow<List<TrialListState.Item>> = _trials

    init {
        val tintCompleted = settings.getBoolean(KEY_LIST_TINT_COMPLETED, true)
        val showEx = settings.getBoolean(KEY_LIST_SHOW_EX, true)
        val showExRemaining = settings.getBoolean(KEY_LIST_SHOW_EX_REMAINING, true)
        val highlightNew = settings.getBoolean(KEY_LIST_HIGHLIGHT_NEW, true)
        val highlightUnplayed = settings.getBoolean(KEY_LIST_HIGHLIGHT_UNPLAYED, true)

        trialManager.trialsFlow.addObserver { trials ->
            _state.value = TrialListState(
                trials = trials,
                sessions = emptyList(),
            )
        }
    }
}