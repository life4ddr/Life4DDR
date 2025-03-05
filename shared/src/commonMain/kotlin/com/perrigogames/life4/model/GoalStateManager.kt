package com.perrigogames.life4.model

import com.perrigogames.life4.LadderDialogs
import com.perrigogames.life4.data.BaseRankGoal
import com.perrigogames.life4.db.GoalDatabaseHelper
import com.perrigogames.life4.db.GoalState
import com.perrigogames.life4.enums.GoalStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.component.inject

class GoalStateManager: BaseModel() {

    private val goalDBHelper: GoalDatabaseHelper by inject()
    private val ladderDialogs: LadderDialogs by inject()

    private val _updated = MutableSharedFlow<Unit>(replay = 1)
    val updated = _updated.asSharedFlow()

    init {
        mainScope.launch {
            _updated.emit(Unit)
        }
    }

    private fun getGoalState(id: Long): GoalState? = goalDBHelper.stateForId(id)

    fun getOrCreateGoalState(id: Long): GoalState = getGoalState(id)
        ?: GoalState(id, GoalStatus.INCOMPLETE, Clock.System.now().toString())

    fun getOrCreateGoalState(goal: BaseRankGoal): GoalState = getOrCreateGoalState(goal.id.toLong())

    fun getGoalStateList(goals: List<BaseRankGoal>): List<GoalState> =
        goalDBHelper.statesForIdList(goals.map { it.id.toLong() }).executeAsList()

    fun setGoalState(id: Long, status: GoalStatus) {
        mainScope.launch {
            goalDBHelper.insertGoalState(id, status)
            _updated.emit(Unit)
        }
    }

    fun clearGoalStates() {
        ladderDialogs.onClearGoalStates {
            mainScope.launch {
                goalDBHelper.deleteAll()
                _updated.emit(Unit)
            }
        }
    }
}