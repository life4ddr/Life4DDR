package com.perrigogames.life4.feature.ladder

import co.touchlab.kermit.Logger
import com.perrigogames.life4.data.*
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.ladder.converter.GoalProgressConverter
import com.perrigogames.life4.feature.ladder.converter.MAPointGoalProgressConverter
import com.perrigogames.life4.feature.ladder.converter.SongsClearGoalProgressConverter
import com.perrigogames.life4.feature.ladder.converter.TrialGoalProgressConverter
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.model.BaseModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import kotlin.reflect.KClass

class LadderGoalProgressManager : BaseModel(), KoinComponent {

    private val logger: Logger by injectLogger("LadderGoalProgressManager")

    private val converters: Map<KClass<out BaseRankGoal>, GoalProgressConverter<out BaseRankGoal>> = mapOf(
        MAPointsStackedGoal::class to MAPointGoalProgressConverter(),
        SongsClearGoal::class to SongsClearGoalProgressConverter(),
        TrialStackedGoal::class to TrialGoalProgressConverter(),
    )

    fun getGoalProgress(goal: BaseRankGoal, ladderRank: LadderRank?): Flow<LadderGoalProgress?> {
        println("Goal ${goal.id}: $goal")
        return when (goal) {
            is SongsClearGoal -> (converters[SongsClearGoal::class] as SongsClearGoalProgressConverter)
                .getGoalProgress(goal, ladderRank)
            is StackedRankGoalWrapper -> when (goal.mainGoal) {
                is TrialStackedGoal -> (converters[TrialStackedGoal::class] as TrialGoalProgressConverter)
                    .getGoalProgress(goal, ladderRank)
                is MAPointsStackedGoal -> (converters[MAPointsStackedGoal::class] as MAPointGoalProgressConverter)
                    .getGoalProgress(goal, ladderRank)
                else -> flowOf(null)
            }
            else -> flowOf(null)
        }
    }

    fun getProgressMapFlow(goals: List<BaseRankGoal>, ladderRank: LadderRank?): Flow<Map<BaseRankGoal, LadderGoalProgress?>> {
        val flowMap = goals.associateWith { getGoalProgress(it, ladderRank) }
        return combine(
            flowMap.map { (goal, flow) ->
                flow.map { goal to it }
            }
        ) { pairs -> pairs.toMap() }
    }
}