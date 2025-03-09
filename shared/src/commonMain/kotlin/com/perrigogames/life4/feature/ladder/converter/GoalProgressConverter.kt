package com.perrigogames.life4.feature.ladder.converter

import com.perrigogames.life4.data.BaseRankGoal
import com.perrigogames.life4.data.LadderGoalProgress
import com.perrigogames.life4.data.StackedRankGoal
import com.perrigogames.life4.data.StackedRankGoalWrapper
import com.perrigogames.life4.enums.LadderRank
import kotlinx.coroutines.flow.Flow

interface GoalProgressConverter<T : BaseRankGoal> {

    fun getGoalProgress(
        goal: T,
        ladderRank: LadderRank?,
    ): Flow<LadderGoalProgress?>
}

interface StackedGoalProgressConverter<M : StackedRankGoal> : GoalProgressConverter<StackedRankGoalWrapper> {

    override fun getGoalProgress(
        goal: StackedRankGoalWrapper,
        ladderRank: LadderRank?,
    ): Flow<LadderGoalProgress?> {
        return getGoalProgress(
            goal = goal.mainGoal as M,
            stackIndex = goal.index,
            ladderRank = ladderRank,
        )
    }

    fun getGoalProgress(
        goal: M,
        stackIndex: Int,
        ladderRank: LadderRank?,
    ): Flow<LadderGoalProgress?>
}
