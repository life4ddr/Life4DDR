package com.perrigogames.life4.feature.ladder.converter

import com.perrigogames.life4.GameConstants
import com.perrigogames.life4.data.LadderGoalProgress
import com.perrigogames.life4.data.MFCPointsStackedGoal
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.feature.songresults.ChartResultOrganizer
import com.perrigogames.life4.feature.songresults.FilterState
import com.perrigogames.life4.util.toInclusiveRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MFCPointGoalProgressConverter : StackedGoalProgressConverter<MFCPointsStackedGoal>, KoinComponent {

    private val chartResultOrganizer: ChartResultOrganizer by inject()

    override fun getGoalProgress(
        goal: MFCPointsStackedGoal,
        stackIndex: Int,
    ): Flow<LadderGoalProgress?> {
        return chartResultOrganizer.resultsForConfig(
            FilterState(
                selectedPlayStyle = goal.playStyle,
                clearTypeRange = ClearType.MARVELOUS_FULL_COMBO.ordinal.toInclusiveRange()
            )
        ).map { results ->
            val mfcPoints = results
                .resultsDone
                .sumOf { GameConstants.mfcPointsForDifficulty(it.chart.difficultyNumber) }
            LadderGoalProgress(
                progress = mfcPoints,
                max = goal.getIntValue(stackIndex, MFCPointsStackedGoal.KEY_MFC_POINTS)!!.toDouble()
            )
        }
    }
}