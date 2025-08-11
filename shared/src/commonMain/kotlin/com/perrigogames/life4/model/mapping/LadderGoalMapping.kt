package com.perrigogames.life4.model.mapping

import com.mohamedrejeb.ksoup.entities.KsoupEntities
import com.perrigogames.life4.data.BaseRankGoal
import com.perrigogames.life4.data.LadderGoalProgress
import com.perrigogames.life4.data.MAPointsGoal
import com.perrigogames.life4.data.MAPointsStackedGoal
import com.perrigogames.life4.data.StackedRankGoalWrapper
import com.perrigogames.life4.enums.GoalStatus
import com.perrigogames.life4.enums.colorRes
import com.perrigogames.life4.feature.ladder.RankListInput
import com.perrigogames.life4.feature.ladder.UILadderDetailItem
import com.perrigogames.life4.feature.ladder.UILadderGoal
import com.perrigogames.life4.feature.ladder.UILadderProgress
import com.perrigogames.life4.feature.songresults.ChartResultPair
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
        val isMFC = base is MAPointsGoal ||
                (base is StackedRankGoalWrapper && base.mainGoal is MAPointsStackedGoal)

        fun ChartResultPair.formatResultItem() : UILadderDetailItem.Entry {
            val rightText = if (isMFC) {
                "L${chart.difficultyNumber} > ${maPoints()}"
            } else {
                (result?.score ?: 0).toInt().longNumberString()
            }
            return if (isMFC) {
                UILadderDetailItem.Entry(
                    leftText = KsoupEntities.decodeHtml(chart.song.title),
                    leftColor = chart.difficultyClass.colorRes,
                    leftWeight = 0.75f,
                    rightText = rightText,
                    rightColor = result!!.clearType.colorRes,
                    rightWeight = 0.25f
                )
            } else {
                UILadderDetailItem.Entry(
                    leftText = KsoupEntities.decodeHtml(chart.song.title),
                    leftColor = chart.difficultyClass.colorRes,
                    rightText = rightText,
                )
            }
        }

        val resultItems = progress?.results?.map { it.formatResultItem() }
            ?: emptyList()
        val resultBottomItems = progress?.resultsBottom?.map { it.formatResultItem() }
            ?: emptyList()

        return UILadderGoal(
            id = base.id.toLong(),
            goalText = base.goalString(),
            completed = isComplete,
            canComplete = allowCompleting && progress == null, // if we can't illustrate progress, it has to be user-driven
            hidden = goalStatus == GoalStatus.IGNORED,
            canHide = !isComplete && // don't allow hiding completed goals
                    (allowHiding || goalStatus == GoalStatus.IGNORED), // must be able to unhide
            showCheckbox = true,
            progress = progress?.toViewData(),
            expandAction = if (progress?.hasResults == true) {
                RankListInput.OnGoal.ToggleExpanded(base.id.toLong())
            } else {
                null
            },
            detailItems = if (isExpanded && progress != null) {
                if (!resultItems.isEmpty() && !resultBottomItems.isEmpty()) {
                    resultItems + listOf(UILadderDetailItem.Spacer) + resultBottomItems
                } else {
                    resultItems + resultBottomItems
                }
            } else {
                emptyList()
            },
            debugText = base.toString().let { text ->
                val mainSplit = text.split('(', ')')
                val title = "${mainSplit[0]} (${base.id})"
                val body = mainSplit[1].split(", ")
                    .filterNot { it.contains("=null") }
                    .filterNot { it.contains("allowsHigherDiffNum=false") }
                    .filterNot { it.contains("id=") }
                    .map { "\t$it" }
                    .joinToString("\n")

                listOf(title, body).joinToString("\n")
            }, // FIXME don't show on release mode
        )
    }
}

fun LadderGoalProgress.toViewData(): UILadderProgress? {
    if (max == 0.0) return null
    return UILadderProgress(
        count = progress,
        max = max,
        showMax = showMax,
        showProgressBar = showProgressBar,
    )
}