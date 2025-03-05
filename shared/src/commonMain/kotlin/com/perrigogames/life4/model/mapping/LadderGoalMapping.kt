package com.perrigogames.life4.model.mapping

import com.mohamedrejeb.ksoup.entities.KsoupEntities
import com.perrigogames.life4.data.BaseRankGoal
import com.perrigogames.life4.data.LadderGoalProgress
import com.perrigogames.life4.data.MAPointsStackedGoal
import com.perrigogames.life4.data.StackedRankGoalWrapper
import com.perrigogames.life4.enums.GoalStatus
import com.perrigogames.life4.enums.colorRes
import com.perrigogames.life4.feature.ladder.RankListInput
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
        goalStatus: GoalStatus = goalStateManager.getOrCreateGoalState(base).status,
        progress: LadderGoalProgress?,
        isExpanded: Boolean,
        allowCompleting: Boolean,
        allowHiding: Boolean,
    ): UILadderGoal {
        val isComplete = goalStatus == GoalStatus.COMPLETE || progress?.isComplete == true
        return UILadderGoal(
            id = base.id.toLong(),
            goalText = base.goalString(),
            completed = isComplete,
            canComplete = allowCompleting && progress == null, // if we can't illustrate progress, it has to be user-driven
            hidden = goalStatus == GoalStatus.IGNORED,
            canHide = !isComplete && // don't allow hiding completed goals
                    (allowHiding || goalStatus == GoalStatus.IGNORED), // must be able to unhide
            showCheckbox = true,
            progress = progress?.let {
                if (it.max == 0.0) return@let null
                UILadderProgress(
                    count = it.progress,
                    max = it.max,
                    showMax = it.showMax
                )
            },
            expandAction = if (progress?.results?.isNotEmpty() == true) {
                RankListInput.OnGoal.ToggleExpanded(base.id.toLong())
            } else {
                null
            },
            detailItems = if (isExpanded && progress != null) {
                progress.results?.map { result ->
                    val isMFC = base is StackedRankGoalWrapper && base.mainGoal is MAPointsStackedGoal
                    val rightText = if (isMFC) {
                        "L%d > %.3f".format(result.chart.difficultyNumber, result.maPointsForDifficulty())
                    } else {
                        (result.result?.score ?: 0).toInt().longNumberString()
                    }
                    if (isMFC) {
                        UILadderDetailItem(
                            leftText = KsoupEntities.decodeHtml(result.chart.song.title),
                            leftColor = result.chart.difficultyClass.colorRes,
                            leftWeight = 0.75f,
                            rightText = rightText,
                            rightColor = result.result!!.clearType.colorRes,
                            rightWeight = 0.25f
                        )
                    } else {
                        UILadderDetailItem(
                            leftText = KsoupEntities.decodeHtml(result.chart.song.title),
                            leftColor = result.chart.difficultyClass.colorRes,
                            rightText = rightText,
                        )
                    }
                } ?: emptyList()
            } else {
                emptyList()
            }
        )
    }
}