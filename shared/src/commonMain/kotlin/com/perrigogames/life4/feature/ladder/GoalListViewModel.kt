package com.perrigogames.life4.feature.ladder

import co.touchlab.kermit.Logger
import com.perrigogames.life4.MR
import com.perrigogames.life4.data.BaseRankGoal
import com.perrigogames.life4.data.RankEntry
import com.perrigogames.life4.data.SongsClearGoal
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
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
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

    private val targetRankFlow: Flow<LadderRank?> = config.targetRank
        ?.let { flowOf(it) }
        ?: userRankManager.targetRank

    private val requirementsFlow: Flow<RankEntry?> = targetRankFlow
        .flatMapLatest { targetRank ->
            ladderDataManager.requirementsForRank(targetRank)
        }
        .onEach { logger.v { "requirementsFlow -> $it" } }

    private val _state = MutableStateFlow<ViewState<UILadderData, String>>(ViewState.Loading).cMutableStateFlow()
    val state: CStateFlow<ViewState<UILadderData, String>> = _state.cStateFlow()

    private var entry: RankEntry? = null

    init {
        viewModelScope.launch {
            combine(
                targetRankFlow,
                requirementsFlow,
                goalStateManager.updated,
            ) { targetRank, requirements, _ ->
                entry = requirements
                when {
                    targetRank == null -> ViewState.Error("No higher goals found...")
                    requirements == null -> ViewState.Error("No goals found for ${targetRank.name}")
                    targetRank >= LadderRank.PLATINUM1 -> ViewState.Success(
                        UILadderData(
                            goals = generateDifficultyCategories(requirements)
                        )
                    )
                    else -> ViewState.Success(
                        UILadderData(
                            goals = generateCommonCategories(requirements)
                        )
                    )
                }
            }.collect { _state.value = it }
        }
    }

    private fun generateCommonCategories(requirements: RankEntry) : UILadderGoals.CategorizedList {
        val goalStates = goalStateManager.getGoalStateList(requirements.goals)
        val finishedGoalCount = goalStates.count { it.status == GoalStatus.COMPLETE }
        val neededGoals = requirements.requirementsOpt ?: 0
        return UILadderGoals.CategorizedList(
            categories = listOf(
                UILadderGoals.CategorizedList.Category(
                    title = MR.strings.goals.desc(),
                    goalText = StringDesc.ResourceFormatted(
                        MR.strings.goal_progress_format,
                        finishedGoalCount,
                        neededGoals
                    ),
                ) to requirements.goals.map { ladderGoalMapper.toViewData(it, isMandatory = false) },
                UILadderGoals.CategorizedList.Category(
                    title = MR.strings.mandatory_goals.desc()
                ) to requirements.mandatoryGoals.map { ladderGoalMapper.toViewData(it, isMandatory = true) }
            )
                .filterNot { it.second.isEmpty() }
        )
    }

    private fun generateDifficultyCategories(requirements: RankEntry) : UILadderGoals.CategorizedList {
        val songsClearGoals = requirements.allGoals.filterIsInstance<SongsClearGoal>()
        val remainingGoals = requirements.allGoals.filterNot { it is SongsClearGoal }
        val categories = (songsClearGoals.groupBy { it.diffNum }
            .toList()
            as List<Pair<Int?, List<BaseRankGoal>>>)
            .sortedBy { it.first ?: Int.MAX_VALUE }
            .toMutableList()
        val otherIndex = categories.indexOfFirst { it.first == null }
        if (categories.any { it.first == null }) {
            val otherGoals = categories.removeAt(otherIndex).second
            categories.add(otherIndex, null to (otherGoals + remainingGoals))
        } else {
            categories.add(null to remainingGoals)
        }

        return UILadderGoals.CategorizedList(
            categories = categories.map { (level, goals) ->
                val title = level?.let { StringDesc.ResourceFormatted(MR.strings.level_header, it) }
                    ?: MR.strings.other_goals.desc()
                UILadderGoals.CategorizedList.Category(title) to goals.map {
                    ladderGoalMapper.toViewData(it, isMandatory = requirements.mandatoryGoalIds.contains(it.id))
                }
            }
        )
    }

    fun handleAction(action: RankListAction) = when(action) {
        is RankListAction.OnGoal -> {
            val goal = entry?.allGoals?.firstOrNull { it.id.toLong() == action.id }.ifNull { return }
            val state = goalStateManager.getOrCreateGoalState(action.id)

            when (action) {
                is RankListAction.OnGoal.ToggleComplete -> {
                    val newStatus = if (state.status == GoalStatus.COMPLETE) {
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
                    val newStatus = if (state.status == GoalStatus.IGNORED) {
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
            ladderGoalMapper.toViewData(baseGoal!!, isMandatory = it.isMandatory)
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

    private fun findGoal(id: Int) = entry?.allGoals?.firstOrNull { it.id == id }
}

data class GoalListConfig(
    val targetRank: LadderRank? = null
)

sealed class RankListAction {
    sealed class OnGoal : RankListAction() {
        abstract val id: Long

        data class ToggleComplete(override val id: Long) : OnGoal()
        data class ToggleHidden(override val id: Long) : OnGoal()
        data class ToggleExpanded(override val id: Long) : OnGoal()
    }
}

// Taken from old LadderGoalsViewModel
//            val entry = ladderDataManager.findRankEntry(config.targetRank)
//            if (config.targetRank == null || entry == null) {
//                // TODO some kind of endgame/error handling text
//                return@launch
//            }
//
//            _stateFlow.value = _stateFlow.value.copy(
//                goals = UILadderGoals.SingleList(
//                    entry.allGoals.map {  goal ->
//                        val goalState = goalStateManager.getGoalState(goal)
//                        UILadderGoal(
//                            id = goal.id.toLong(),
//                            goalText = goal.goalString(platformStrings),
//                            completed = goalState?.status == GoalStatus.COMPLETE,
//                            hidden = goalState?.status == GoalStatus.IGNORED,
//                            canHide = false, // FIXME
//                            progress = null, // FIXME
//                            detailItems = emptyList() // FIXME
//                        )
//                    }
//                )
//            )