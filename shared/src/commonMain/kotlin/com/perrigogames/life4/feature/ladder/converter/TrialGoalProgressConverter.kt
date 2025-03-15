package com.perrigogames.life4.feature.ladder.converter

import com.perrigogames.life4.data.LadderGoalProgress
import com.perrigogames.life4.data.TrialStackedGoal
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.trials.manager.TrialRecordsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TrialGoalProgressConverter : StackedGoalProgressConverter<TrialStackedGoal>, KoinComponent {

    private val trialRecordsManager: TrialRecordsManager by inject()

    override fun getGoalProgress(
        goal: TrialStackedGoal,
        stackIndex: Int,
        ladderRank: LadderRank?,
    ): Flow<LadderGoalProgress?> {
        return trialRecordsManager.bestSessions.map { sessions -> // FIXME playstyle
            val count = sessions.count {
                if (goal.restrictDifficulty) {
                    it.goalRank.stableId == goal.rank.stableId
                } else {
                    it.goalRank.stableId >= goal.rank.stableId
                }
            }
            LadderGoalProgress(
                progress = count,
                max = goal.getIntValue(stackIndex, TrialStackedGoal.KEY_TRIALS_COUNT)!!
            )
        }
    }
}