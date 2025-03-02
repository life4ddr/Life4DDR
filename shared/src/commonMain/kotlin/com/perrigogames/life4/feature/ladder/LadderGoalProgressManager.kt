package com.perrigogames.life4.feature.ladder

import co.touchlab.kermit.Logger
import com.perrigogames.life4.data.*
import com.perrigogames.life4.feature.ladder.converter.GoalProgressConverter
import com.perrigogames.life4.feature.ladder.converter.MFCPointGoalProgressConverter
import com.perrigogames.life4.feature.ladder.converter.SongsClearGoalProgressConverter
import com.perrigogames.life4.feature.ladder.converter.TrialGoalProgressConverter
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.model.BaseModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.component.KoinComponent
import kotlin.reflect.KClass

class LadderGoalProgressManager : BaseModel(), KoinComponent {

    private val logger: Logger by injectLogger("LadderGoalProgressManager")

    private val converters: Map<KClass<out BaseRankGoal>, GoalProgressConverter<out BaseRankGoal>> = mapOf(
        MFCPointsStackedGoal::class to MFCPointGoalProgressConverter(),
        TrialStackedGoal::class to TrialGoalProgressConverter(),
    )

    fun getGoalProgress(goal: BaseRankGoal): Flow<LadderGoalProgress?> {
        println("Goal ${goal.id}: $goal")
        return when (goal) {
            is SongsClearGoal -> (converters[SongsClearGoal::class] as SongsClearGoalProgressConverter)
                .getGoalProgress(goal)
            is StackedRankGoalWrapper -> when (goal.mainGoal) {
                is TrialStackedGoal -> (converters[TrialStackedGoal::class] as TrialGoalProgressConverter)
                    .getGoalProgress(goal)
                is MFCPointsStackedGoal -> (converters[MFCPointsStackedGoal::class] as MFCPointGoalProgressConverter)
                    .getGoalProgress(goal)
                else -> flowOf(null)
            }
            else -> flowOf(null)
        }
    }
}