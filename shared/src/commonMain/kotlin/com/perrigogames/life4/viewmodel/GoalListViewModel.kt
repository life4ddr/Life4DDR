package com.perrigogames.life4.viewmodel

import com.perrigogames.life4.PlatformStrings
import com.perrigogames.life4.data.BaseRankGoal
import com.perrigogames.life4.data.RankEntry
import com.perrigogames.life4.data.ladder.UILadderData
import com.perrigogames.life4.data.ladder.UILadderGoal
import com.perrigogames.life4.data.ladder.UILadderGoals
import com.perrigogames.life4.enums.GoalStatus
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.model.LadderDataManager
import com.perrigogames.life4.model.LadderProgressManager
import com.perrigogames.life4.model.UserRankManager
import com.perrigogames.life4.util.ViewState
import com.perrigogames.life4.util.ifNull
import com.perrigogames.life4.util.toViewState
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GoalListViewModel(private val config: GoalListConfig) : ViewModel(), KoinComponent {

    private val ladderDataManager: LadderDataManager by inject()
    private val userRankManager: UserRankManager by inject()
    private val ladderProgressManager: LadderProgressManager by inject()
    private val platformStrings: PlatformStrings by inject()

    private val _state = MutableStateFlow<ViewState<UILadderData, String>>(ViewState.Loading).cMutableStateFlow()
    val state: StateFlow<ViewState<UILadderData, String>> = _state

    private lateinit var entry: RankEntry

    init {
        viewModelScope.launch {
            if (config.targetRank == null) {
                _state.value = ViewState.Error("No higher goals found...")
                return@launch
            }
            ladderDataManager.findRankEntry(config.targetRank)
                ?.also { entry = it }
                .ifNull {
                    _state.value = ViewState.Error("No goals found for ${config.targetRank.name}")
                    return@launch
                }
            _state.value = ViewState.Success(
                UILadderData(
                    goals = UILadderGoals.SingleList(
                        items = entry.goals.map(::toViewData)
                    )
                )
            )
        }
    }

    fun handleAction(action: RankListAction) = when(action) {
        is RankListAction.OnGoal.SetComplete -> {
            val newStatus = if (action.complete) {
                GoalStatus.COMPLETE
            } else {
                GoalStatus.INCOMPLETE
            }
            ladderDataManager.setGoalState(action.id, newStatus)
            modifyLoadedState { data ->
                data.copy(
                    goals = data.goals.replaceGoal(action.id) { uiLadderGoal ->
                        uiLadderGoal.copy(

                        )
                    }
                )
            }
        }
        is RankListAction.OnGoal.SetExpanded -> {

        }
        is RankListAction.OnGoal.SetHidden -> {
            val newStatus = if (action.hidden) {
                GoalStatus.IGNORED
            } else {
                GoalStatus.INCOMPLETE
            }
            ladderDataManager.setGoalState(action.id, newStatus)
            updateGoal(action.id)
        }
    }

    private fun updateGoal(id: Long) {
        val baseGoal = findGoal(id.toInt()).ifNull { return }
        modifyGoal(id) {
            toViewData(baseGoal!!)
        }
    }

    private fun modifyGoal(id: Long, block: (UILadderGoal) -> UILadderGoal) {
        modifyLoadedState { data ->
            data.copy(
                goals = data.goals.replaceGoal(id, block)
            )
        }
    }

    private inline fun modifyLoadedState(block: (UILadderData) -> UILadderData) {
        val loadedState = (_state.value as? ViewState.Success)?.data.ifNull { return }
        _state.value = block(loadedState!!.copy()).toViewState()
    }

    private fun findGoal(id: Int) = entry.allGoals.firstOrNull { it.id == id }

    private fun toViewData(base: BaseRankGoal): UILadderGoal {
        val goalState = ladderDataManager.getOrCreateGoalState(base)
        return UILadderGoal(
            id = base.id.toLong(),
            goalText = base.goalString(platformStrings),
            completed = goalState.status == GoalStatus.COMPLETE,
            hidden = goalState.status == GoalStatus.IGNORED,
            canHide = !base.isMandatory
        )
    }
}

data class GoalListConfig(
    val targetRank: LadderRank?
)

sealed class RankListAction {
    sealed class OnGoal : RankListAction() {
        abstract val id: Long

        data class SetComplete(override val id: Long, val complete: Boolean) : OnGoal()
        data class SetHidden(override val id: Long, val hidden: Boolean) : OnGoal()
        data class SetExpanded(override val id: Long, val expanded: Boolean) : OnGoal()
    }
}
