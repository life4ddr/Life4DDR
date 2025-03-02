package com.perrigogames.life4.feature.ladder.converter

import com.perrigogames.life4.data.BaseRankGoal
import com.perrigogames.life4.data.LadderGoalProgress
import com.perrigogames.life4.data.StackedRankGoal
import com.perrigogames.life4.data.StackedRankGoalWrapper
import kotlinx.coroutines.flow.Flow

interface GoalProgressConverter<T : BaseRankGoal> {

    fun getGoalProgress(
        goal: T,
    ): Flow<LadderGoalProgress?>
}

interface StackedGoalProgressConverter<M : StackedRankGoal> : GoalProgressConverter<StackedRankGoalWrapper> {

    override fun getGoalProgress(
        goal: StackedRankGoalWrapper,
    ): Flow<LadderGoalProgress?> {
        return getGoalProgress(
            goal = goal.mainGoal as M,
            stackIndex = goal.index,
        )
    }

    fun getGoalProgress(
        goal: M,
        stackIndex: Int,
    ): Flow<LadderGoalProgress?>
}
