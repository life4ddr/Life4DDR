package com.perrigogames.life4.feature.ladder.converter

import com.perrigogames.life4.data.LadderGoalProgress
import com.perrigogames.life4.data.MAPointsStackedGoal
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.feature.songresults.ChartResultOrganizer
import com.perrigogames.life4.feature.songresults.FilterState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MAPointGoalProgressConverter : StackedGoalProgressConverter<MAPointsStackedGoal>, KoinComponent {

    private val chartResultOrganizer: ChartResultOrganizer by inject()

    override fun getGoalProgress(
        goal: MAPointsStackedGoal,
        stackIndex: Int,
    ): Flow<LadderGoalProgress?> {
        val config = FilterState(
            selectedPlayStyle = goal.playStyle,
            clearTypeRange = ClearType.SINGLE_DIGIT_PERFECTS.ordinal .. ClearType.MARVELOUS_FULL_COMBO.ordinal,
        )
        val targetPoints = goal.getDoubleValue(stackIndex, MAPointsStackedGoal.KEY_MFC_POINTS)!!
        return chartResultOrganizer.resultsForConfig(goal, config, enableDifficultyTiers = false).map { (match, _) ->
            val mfcPoints = match.sumOf { it.maPointsForDifficulty() }
            LadderGoalProgress(
                progress = mfcPoints,
                max = targetPoints,
                showMax = true,
                results = match
            )
        }
    }
}