package com.perrigogames.life4.feature.laddergoals

import co.touchlab.kermit.Logger
import com.perrigogames.life4.data.RankEntry
import com.perrigogames.life4.enums.GoalStatus
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.profile.UserRankManager
import com.perrigogames.life4.feature.songresults.SongResultsManager
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.model.GoalStateManager
import com.perrigogames.life4.model.LadderDataManager
import com.perrigogames.life4.model.mapping.LadderGoalMapper
import com.perrigogames.life4.util.ViewState
import com.perrigogames.life4.util.ifNull
import com.perrigogames.life4.util.toViewState
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GoalListViewModel(private val config: GoalListConfig) : ViewModel(), KoinComponent {
    private val ladderDataManager: LadderDataManager by inject()
    private val goalStateManager: GoalStateManager by inject()
    private val songResultsManager: SongResultsManager by inject()
    private val ladderGoalMapper: LadderGoalMapper by inject()
    private val userRankManager: UserRankManager by inject()
    private val logger: Logger by injectLogger("GoalListViewModel")

    private val targetRankFlow =
        config.targetRank
            ?.let { flowOf(it) }
            ?: userRankManager.targetRank

    private val requirementsFlow =
        targetRankFlow
            .flatMapLatest { targetRank ->
                ladderDataManager.requirementsForRank(targetRank)
            }
            .onEach { logger.v { "requirementsFlow -> $it" } }

    private val _state = MutableStateFlow<ViewState<UILadderData, String>>(ViewState.Loading).cMutableStateFlow()
    val state: StateFlow<ViewState<UILadderData, String>> = _state

    private var entry: RankEntry? = null

    init {
        viewModelScope.launch {
            combine(
                targetRankFlow,
                requirementsFlow,
            ) { targetRank, requirements ->
                entry = requirements
                when {
                    targetRank == null -> ViewState.Error("No higher goals found...")
                    requirements == null -> ViewState.Error("No goals found for ${targetRank.name}")
                    else ->
                        ViewState.Success(
                            UILadderData(
                                goals =
                                    UILadderGoals.SingleList(
                                        items = requirements.goals.map(ladderGoalMapper::toViewData),
                                    ),
                            ),
                        )
                }
            }.collect { _state.value = it }
        }
    }

    fun handleAction(action: RankListAction) =
        when (action) {
            is RankListAction.OnGoal -> {
                val goal = entry?.allGoals?.firstOrNull { it.id.toLong() == action.id }.ifNull { return }
                val state = goalStateManager.getOrCreateGoalState(action.id)

                when (action) {
                    is RankListAction.OnGoal.ToggleComplete -> {
                        val newStatus =
                            if (state.status == GoalStatus.COMPLETE) {
                                GoalStatus.INCOMPLETE
                            } else {
                                GoalStatus.COMPLETE
                            }
                        goalStateManager.setGoalState(action.id, newStatus)
                        updateGoal(action.id)
                    }
                    is RankListAction.OnGoal.ToggleExpanded -> {
                    }
                    is RankListAction.OnGoal.ToggleHidden -> {
                        val newStatus =
                            if (state.status == GoalStatus.IGNORED) {
                                GoalStatus.INCOMPLETE
                            } else {
                                GoalStatus.IGNORED
                            }
                        goalStateManager.setGoalState(action.id, newStatus)
                        updateGoal(action.id)
                    }
                }
            }
        }

    private fun updateGoal(id: Long) {
        val baseGoal = findGoal(id.toInt()).ifNull { return }
        modifyGoal(id) {
            ladderGoalMapper.toViewData(baseGoal!!)
        }
    }

    private fun modifyGoal(
        id: Long,
        block: (UILadderGoal) -> UILadderGoal,
    ) {
        modifyLoadedState { data ->
            data.copy(
                goals = data.goals.replaceGoal(id, block),
            )
        }
    }

    private inline fun modifyLoadedState(block: (UILadderData) -> UILadderData) {
        val loadedState = (_state.value as? ViewState.Success)?.data.ifNull { return }
        _state.value = block(loadedState!!.copy()).toViewState()
    }

    private fun findGoal(id: Int) = entry?.allGoals?.firstOrNull { it.id == id }
}

data class GoalListConfig(
    val targetRank: LadderRank? = null,
)

sealed class RankListAction {
    sealed class OnGoal : RankListAction() {
        abstract val id: Long

        data class ToggleComplete(override val id: Long) : OnGoal()

        data class ToggleHidden(override val id: Long) : OnGoal()

        data class ToggleExpanded(override val id: Long) : OnGoal()
    }
}
