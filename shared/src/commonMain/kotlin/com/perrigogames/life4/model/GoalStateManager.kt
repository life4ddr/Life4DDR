package com.perrigogames.life4.model

import com.perrigogames.life4.LadderDialogs
import com.perrigogames.life4.data.BaseRankGoal
import com.perrigogames.life4.db.GoalDatabaseHelper
import com.perrigogames.life4.db.GoalState
import com.perrigogames.life4.enums.GoalStatus
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.component.inject

class GoalStateManager: BaseModel() {

    private val goalDBHelper: GoalDatabaseHelper by inject()
    private val ladderDialogs: LadderDialogs by inject()

    fun getGoalState(id: Long): GoalState? = goalDBHelper.stateForId(id)
    fun getGoalState(goal: BaseRankGoal): GoalState? = getGoalState(goal.id.toLong())

    fun getOrCreateGoalState(id: Long): GoalState = getGoalState(id)
        ?: GoalState(id, GoalStatus.INCOMPLETE, Clock.System.now().toString())
    fun getOrCreateGoalState(goal: BaseRankGoal): GoalState = getOrCreateGoalState(goal.id.toLong())

    fun getGoalStateList(goals: List<BaseRankGoal>): List<GoalState> =
        goalDBHelper.statesForIdList(goals.map { it.id.toLong() }).executeAsList()

    fun setGoalState(id: Long, status: GoalStatus) {
        goalDBHelper.insertGoalState(id, status)
    }

    fun clearGoalStates() {
        ladderDialogs.onClearGoalStates {
            mainScope.launch {
                goalDBHelper.deleteAll()
            }
            // FIXME ladderProgressManager.clearAllResults()
            // FIXME eventBus.post(LadderRankUpdatedEvent())
        }
    }
}