package com.perrigogames.life4.model.mapping

import com.perrigogames.life4.data.BaseRankGoal
import com.perrigogames.life4.data.LadderGoalProgress
import com.perrigogames.life4.db.GoalState
import com.perrigogames.life4.enums.GoalStatus
import com.perrigogames.life4.feature.ladder.UILadderDetailItem
import com.perrigogames.life4.feature.ladder.UILadderGoal
import com.perrigogames.life4.feature.ladder.UILadderProgress
import com.perrigogames.life4.longNumberString
import com.perrigogames.life4.model.GoalStateManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LadderGoalMapper : KoinComponent {

    private val goalStateManager: GoalStateManager by inject()

    fun toViewData(
        base: BaseRankGoal,
        goalState: GoalState = goalStateManager.getOrCreateGoalState(base),
        progress: LadderGoalProgress?,
        isMandatory: Boolean,
        isExpanded: Boolean,
    ) = UILadderGoal(
        id = base.id.toLong(),
        goalText = base.goalString(),
        completed = goalState.status == GoalStatus.COMPLETE,
        hidden = goalState.status == GoalStatus.IGNORED,
        canHide = !isMandatory,
        isMandatory = isMandatory,
        progress = progress?.let {
            if (it.max == 0.0) return@let null
            UILadderProgress(
                count = it.progress,
                max = it.max,
                showMax = it.showMax
            )
        },
        detailItems = if (isExpanded && progress != null) {
            progress.results?.map { result ->
                UILadderDetailItem(
                    leftText = result.chart.song.title,
                    difficultyClass = result.chart.difficultyClass,
                    rightText = (result.result?.score ?: 0).toInt().longNumberString()
                )
            } ?: emptyList()
        } else {
            emptyList()
        }
    )
}