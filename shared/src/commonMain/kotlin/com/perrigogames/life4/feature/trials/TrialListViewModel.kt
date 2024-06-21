package com.perrigogames.life4.feature.trials

import com.perrigogames.life4.MR
import com.perrigogames.life4.SettingsKeys.KEY_TRIAL_LIST_HIGHLIGHT_NEW
import com.perrigogames.life4.SettingsKeys.KEY_TRIAL_LIST_HIGHLIGHT_UNPLAYED
import com.perrigogames.life4.SettingsKeys.KEY_TRIAL_LIST_SHOW_EX
import com.perrigogames.life4.SettingsKeys.KEY_TRIAL_LIST_TINT_COMPLETED
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.data.TrialState
import com.perrigogames.life4.db.SelectBestSessions
import com.perrigogames.life4.enums.TrialJacketCorner
import com.perrigogames.life4.feature.settings.UserRankSettings
import com.perrigogames.life4.feature.trialrecords.TrialRecordsManager
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TrialListViewModel : ViewModel(), KoinComponent {

    private val trialManager: TrialManager by inject()
    private val userRankSettings: UserRankSettings by inject()
    private val trialRecordsManager: TrialRecordsManager by inject()
    private val settings: Settings by inject()

    private val _state = MutableStateFlow(UITrialList()).cMutableStateFlow()
    val state: CStateFlow<UITrialList> = _state.cStateFlow()

    init {
        val tintCompleted = settings.getBoolean(KEY_TRIAL_LIST_TINT_COMPLETED, true)
        val showEx = settings.getBoolean(KEY_TRIAL_LIST_SHOW_EX, true)
        val highlightNew = settings.getBoolean(KEY_TRIAL_LIST_HIGHLIGHT_NEW, true)
        val highlightUnplayed = settings.getBoolean(KEY_TRIAL_LIST_HIGHLIGHT_UNPLAYED, true)

        viewModelScope.launch {
            combine(
                trialManager.trialsFlow,
                trialRecordsManager.bestSessions,
                userRankSettings.rank,
            ) { trials, sessions, rank ->
                _state.value = UITrialList(
                    placementBanner = if (rank == null) {
                        UIPlacementBanner()
                    } else {
                        null
                    },
                    trials = createDisplayTrials(
                        trials = trials,
                        sessions = sessions,
                        featureNew = highlightNew,
                        featureUnplayed = highlightUnplayed,
                    ),
                )
            }.collect()
        }
    }

    private fun matchTrials(trials: List<Trial>, sessions: List<SelectBestSessions>) = trials.associateWith { trial ->
        sessions.firstOrNull { it.trialId == trial.id }
    }

    private fun createDisplayTrials(
        trials: List<Trial>,
        sessions: List<SelectBestSessions>,
        featureNew: Boolean,
        featureUnplayed: Boolean,
    ): List<UITrialList.Item> {
        val matchedTrials = matchTrials(trials, sessions)

        val retired = mutableListOf<UITrialList.Item.Trial>()
        val event = mutableListOf<UITrialList.Item.Trial>()
        val new = mutableListOf<UITrialList.Item.Trial>()
        val unplayed = mutableListOf<UITrialList.Item.Trial>()
        val active = mutableListOf<UITrialList.Item.Trial>()

        trials.map { it.toUIJacket(bestSession = matchedTrials[it]) }
            .forEach { item ->
                when {
                    item.trial.state == TrialState.RETIRED -> retired
                    item.cornerType == TrialJacketCorner.EVENT -> event
                    featureNew && item.cornerType == TrialJacketCorner.NEW -> new
                    featureUnplayed && item.session == null -> unplayed
                    else -> active
                }.add(UITrialList.Item.Trial(item))
            }

        return mutableListOf<UITrialList.Item>(
            UITrialList.Item.Header(MR.strings.active_trials.desc())
        ).apply {
            addAll(event)
            addAll(new)
            addAll(unplayed)
            addAll(active)
            add(UITrialList.Item.Header(MR.strings.retired_trials.desc()))
            addAll(retired)
        }
    }
}