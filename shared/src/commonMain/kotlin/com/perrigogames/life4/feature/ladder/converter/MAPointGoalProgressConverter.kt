package com.perrigogames.life4.feature.ladder.converter

import com.perrigogames.life4.GameConstants
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
            scoreRange = 999_910 .. GameConstants.MAX_SCORE,
        )
        val targetPoints = goal.getDoubleValue(stackIndex, MAPointsStackedGoal.KEY_MFC_POINTS)!!
        return chartResultOrganizer.resultsForConfig(config).map { (match, _) ->
            val mfcPoints = match.sumOf { maPointsForDifficulty(it.chart.difficultyNumber, it.result!!.clearType) }
            LadderGoalProgress(
                progress = mfcPoints,
                max = targetPoints,
                showMax = true,
                results = match
            )
        }
    }

    private fun maPointsForDifficulty(difficulty: Int, clearType: ClearType): Double {
        val points = when (difficulty) { // first = MFC, 2nd = SDP
            1 -> 0.1 to 0.01
            2, 3 -> 0.25 to 0.025
            4, 5, 6 -> 0.5 to 0.05
            7, 8, 9 -> 1.0 to 0.1
            10 -> 1.5 to 0.15
            11 -> 2.0 to 0.2
            12 -> 4.0 to 0.4
            13 -> 6.0 to 0.6
            14 -> 8.0 to 0.8
            15 -> 15.0 to 1.5
            16, 17, 18, 19, 20 -> 25.0 to 2.5
            else -> 0.0 to 0.0
        }
        return if (clearType == ClearType.MARVELOUS_FULL_COMBO) {
            points.first
        } else {
            points.second
        }
    }
}