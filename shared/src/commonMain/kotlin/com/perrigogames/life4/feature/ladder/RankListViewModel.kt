package com.perrigogames.life4.feature.ladder

import com.perrigogames.life4.MR
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.LadderRankClass
import com.perrigogames.life4.feature.firstrun.FirstRunSettingsManager
import com.perrigogames.life4.feature.firstrun.InitState
import com.perrigogames.life4.feature.settings.UserInfoSettings
import com.perrigogames.life4.model.LadderDataManager
import com.perrigogames.life4.model.mapping.LadderGoalMapper
import dev.icerock.moko.mvvm.flow.*
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RankListViewModel(
    isFirstRun: Boolean = false
) : ViewModel(), KoinComponent {

    private val firstRunSettingsManager: FirstRunSettingsManager by inject()
    private val ladderGoalProgressManager: LadderGoalProgressManager by inject()
    private val userInfoSettings: UserInfoSettings by inject()
    private val ladderDataManager: LadderDataManager by inject()
    private val ladderGoalMapper: LadderGoalMapper by inject()

    private val selectedRankClass = MutableStateFlow<LadderRankClass?>(null)
    private val selectedRank = MutableStateFlow<LadderRank?>(null)
    private val selectedRankGoals = selectedRank
        .flatMapLatest { ladderDataManager.requirementsForRank(it) }
    private var startingRank: LadderRank? = null
    private var rankClassChanged: Boolean = !isFirstRun

    private val _state = MutableStateFlow(UIRankList()).cMutableStateFlow()
    val state: CStateFlow<UIRankList> = _state.cStateFlow()

    private val _actions = MutableSharedFlow<Action>()
    val actions: CFlow<Action> = _actions.cFlow()

    init {
        viewModelScope.launch {
            startingRank = userInfoSettings.userRank.value
            selectedRankClass.value = startingRank?.group
            selectedRank.value = startingRank

            combine(
                selectedRankClass,
                selectedRankGoals,
                selectedRankGoals.filterNotNull().flatMapLatest {
                    ladderGoalProgressManager.getProgressMapFlow(it.allGoals)
                }
            ) { rankClass, goalEntry, progress ->
                val rank = selectedRank.value // base for selectedRankGoals
                UIRankList(
                    titleText = when {
                        isFirstRun -> MR.strings.select_a_starting_rank
                        else -> MR.strings.select_a_new_rank
                    },
                    showBackButton = !isFirstRun,
                    rankClasses = listOf(UILadderRankClass.NO_RANK) + LadderRankClass.entries.map {
                        UILadderRankClass(it, selected = it == rankClass)
                    },
                    selectedRankClass = rankClass,
                    showRankSelector = rankClassChanged,
                    isRankSelectorCompressed = goalEntry != null,
                    ranks = rankClass?.let {
                        LadderRank.entries.filter { it.group == rankClass }
                            .sortedBy { it.stableId }
                            .map { UILadderRank(it, selected = it == rank) }
                    } ?: emptyList(),
                    noRankInfo = when {
                        isFirstRun -> UINoRank.FIRST_RUN
                        else -> UINoRank.DEFAULT
                    },
                    footer = when {
                        isFirstRun && rank != null -> UIFooterData.firstRunRankSubmit(rank)
                        isFirstRun -> UIFooterData.FIRST_RUN_CANCEL
                        startingRank == rank -> null
                        rank != null -> UIFooterData.changeSubmit(rank)
                        else -> null
                    },
                    ladderData = goalEntry?.allGoals?.let { goals ->
                        UILadderData(
                            items = goals.map { goal ->
                                ladderGoalMapper.toViewData(
                                    base = goal,
                                    isMandatory = goalEntry.mandatoryGoalIds.contains(goal.id),
                                    progress = progress[goal]
                                )
                            },
                            allowCompleting = false,
                            allowHiding = false
                        )
                    }
                )
            }.collect(_state)
        }
    }

    fun onInputAction(input: Input) = viewModelScope.launch {
        when (input) {
            is Input.RankClassTapped -> {
                rankClassChanged = true
                selectedRank.value = null
                selectedRankClass.value = input.rankClass
            }
            is Input.RankTapped -> {
                selectedRank.value = input.rank
            }
            is Input.RankSelected -> {
                firstRunSettingsManager.setInitState(InitState.DONE)
                userInfoSettings.setRank(input.rank)
                _actions.emit(Action.NavigateToMainScreen)
            }
            Input.MoveToPlacements -> {
                firstRunSettingsManager.setInitState(InitState.PLACEMENTS)
                _actions.emit(Action.NavigateToPlacements)
            }
            Input.RankRejected -> {
                firstRunSettingsManager.setInitState(InitState.DONE)
                userInfoSettings.setRank(null)
                _actions.emit(Action.NavigateToMainScreen)
            }
        }
    }

    fun moveToPlacements() {
        firstRunSettingsManager.setInitState(InitState.PLACEMENTS)
    }

    fun saveRank(ladderRank: LadderRank?) {
        firstRunSettingsManager.setInitState(InitState.DONE)
        userInfoSettings.setRank(ladderRank)
    }

    sealed class Input {
        data class RankClassTapped(val rankClass: LadderRankClass?) : Input()
        data class RankTapped(val rank: LadderRank) : Input()
        data class RankSelected(val rank: LadderRank) : Input()
        data object MoveToPlacements : Input()
        data object RankRejected : Input()
    }

    sealed class Action {
        data object NavigateToPlacements : Action()
        data object NavigateToMainScreen : Action()
    }
}
