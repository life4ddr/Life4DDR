package com.perrigogames.life4.feature.laddergoals

import com.perrigogames.life4.PlatformStrings
import com.perrigogames.life4.enums.GoalStatus
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.songresults.SongResultsManager
import com.perrigogames.life4.model.GoalStateManager
import com.perrigogames.life4.model.LadderDataManager
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LadderGoalsViewModel(private val config: LadderGoalsConfig) : ViewModel(), KoinComponent {

    private val ladderDataManager: LadderDataManager by inject()
    private val songResultsManager: SongResultsManager by inject()
    private val goalStateManager: GoalStateManager by inject()
    private val platformStrings: PlatformStrings by inject()

    private val _stateFlow = MutableStateFlow(
        UILadderData(UILadderGoals.SingleList(emptyList()))
    )
    val stateFlow: StateFlow<UILadderData> = _stateFlow

    init {
        viewModelScope.launch {
            val entry = ladderDataManager.findRankEntry(config.targetRank)
            if (config.targetRank == null || entry == null) {
                // TODO some kind of endgame/error handling text
                return@launch
            }

            _stateFlow.value = _stateFlow.value.copy(
                goals = UILadderGoals.SingleList(
                    entry.allGoals.map {  goal ->
                        val goalState = goalStateManager.getGoalState(goal)
                        UILadderGoal(
                            id = goal.id.toLong(),
                            goalText = goal.goalString(platformStrings),
                            completed = goalState?.status == GoalStatus.COMPLETE,
                            hidden = goalState?.status == GoalStatus.IGNORED,
                            canHide = false, // FIXME
                            progress = null, // FIXME
                            detailItems = emptyList() // FIXME
                        )
                    }
                )
            )
        }
    }
}

/**
 * @param targetRank the rank the user is attempting to reach, indicating the goals to show
 *  null == player is the top rank
 */
data class LadderGoalsConfig(
    val targetRank: LadderRank? = null,
)
