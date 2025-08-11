package com.perrigogames.life4.feature.ladder.converter

import co.touchlab.kermit.Logger
import com.perrigogames.life4.GameConstants
import com.perrigogames.life4.data.LadderGoalProgress
import com.perrigogames.life4.data.SongsClearGoal
import com.perrigogames.life4.data.SongsClearStackedGoal
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.songresults.*
import com.perrigogames.life4.feature.songresults.FilterState.Companion.DEFAULT_DIFFICULTY_NUMBER_RANGE
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.model.safeScore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.roundToLong

class SongsClearGoalProgressConverter : GoalProgressConverter<SongsClearGoal>, KoinComponent {

    private val chartResultOrganizer: ChartResultOrganizer by inject()
    private val logger: Logger by injectLogger("SongsClearGoalProgressConverter")

    override fun getGoalProgress(
        goal: SongsClearGoal,
        ladderRank: LadderRank?,
    ): Flow<LadderGoalProgress?> {
        val config = FilterState(
            chartFilter = ChartFilterState(
                selectedPlayStyle = goal.playStyle,
                difficultyClassSelection = goal.diffClassSet?.set ?: DifficultyClass.entries,
                difficultyNumberRange = goal.diffNumRange ?: DEFAULT_DIFFICULTY_NUMBER_RANGE,
                ignoreFilterType = when {
                    goal.songCount != null -> IgnoreFilterType.ALL // working up, allow all songs
                    ladderRank == null -> IgnoreFilterType.BASIC
                    ladderRank < LadderRank.AMETHYST1 -> IgnoreFilterType.BASIC // FIXME use the JSON value
                    else -> IgnoreFilterType.EXPANDED
                }
            ),
            resultFilter = ResultFilterState(
                clearTypeRange = goal.clearType.ordinal .. ClearType.entries.size,
                scoreRange = ((goal.score ?: 0) .. GameConstants.MAX_SCORE),
                filterIgnored = true
            )
        )
        return chartResultOrganizer.resultsForConfig(goal, config, enableDifficultyTiers = false)
            .map { (match, partMatch, noMatch) ->
                if (goal.diffNum != null) {
                    when {
                        goal.score != null -> when (goal.songCount) { // score-based goal
                            null -> allSongsEasyProgress(goal, match, partMatch, noMatch)
                            else -> countSongsScoreProgress(goal, match, partMatch, noMatch)
                        }
                        goal.averageScore != null -> allSongsAverageScoreProgress(goal, match, partMatch, noMatch)
                        else -> when { // clear type
                            goal.songCount != null -> countSongsClearProgress(goal, match, noMatch)
                            else -> allSongsEasyProgress(goal, match, partMatch, noMatch)
                        }
                    }
                } else {
                    logger.v("Goal ${goal.id}: ${goal.diffNum}, ${goal.score}, ${goal.clearType}")
                    LadderGoalProgress(
                        progress = 0,
                        max = 0,
                        showMax = true,
                    )
                }
            }
    }

    private fun countSongsScoreProgress(
        goal: SongsClearGoal,
        match: List<ChartResultPair>,
        partMatch: List<ChartResultPair>,
        noMatch: List<ChartResultPair>,
    ): LadderGoalProgress {
        var completed = 0
        var topScore = 0L
        val sortedMatches = match.groupByDifficultyNumber()

        fun List<ChartResultPair>.recordTopScore() {
            firstOrNull()?.result?.let {
                if (it.safeScore > topScore) {
                    topScore = it.safeScore
                }
            }
        }

        goal.forEachDiffNum { diff ->
            val diffMatch = sortedMatches[diff] ?: emptyList()
            diffMatch.recordTopScore()
            completed += diffMatch.size
        }

        partMatch.recordTopScore()
        noMatch.recordTopScore()

        return if (goal.songCount == 1) {
            LadderGoalProgress(
                progress = topScore.toInt(),
                max = goal.score!!,
                showMax = false,
                results = match,
            )
        } else {
            LadderGoalProgress(
                progress = completed,
                max = goal.songCount!!,
                showMax = true,
                results = match,
            )
        }
    }

    private fun countSongsClearProgress(
        goal: SongsClearGoal,
        match: List<ChartResultPair>,
        noMatch: List<ChartResultPair>,
    ): LadderGoalProgress {
        var completed = 0
        val sortedMatches = match.groupByDifficultyNumber()
        goal.forEachDiffNum { diff ->
            (sortedMatches[diff] ?: emptyList()).forEach { result ->
                if ((result.result?.clearType ?: ClearType.NO_PLAY) >= goal.clearType) {
                    completed++
                }
            }
        }

        return LadderGoalProgress(
            progress = completed,
            max = goal.songCount!!,
            showMax = true,
            results = match,
        )
    }

    private fun allSongsEasyProgress(
        goal: SongsClearGoal,
        match: List<ChartResultPair>,
        partMatch: List<ChartResultPair>,
        noMatch: List<ChartResultPair>,
    ): LadderGoalProgress {
        val freeExceptions = if (goal.exceptionScore != null) {
            0
        } else {
            (goal.exceptions ?: 0)
        }
        return LadderGoalProgress(
            progress = match.size,
            max = (match.size + noMatch.size) - freeExceptions,
            showMax = true,
            results = if (partMatch.isNotEmpty()) partMatch else noMatch,
            resultsBottom = if (partMatch.isNotEmpty()) noMatch else null
        )
    }

    private fun allSongsAverageScoreProgress(
        goal: SongsClearGoal,
        match: List<ChartResultPair>,
        partMatch: List<ChartResultPair>,
        noMatch: List<ChartResultPair>
    ): LadderGoalProgress {
        val averageScore = (match + partMatch + noMatch).averageScore()
        return LadderGoalProgress(
            progress = averageScore.toInt(),
            max = goal.averageScore!!,
            showMax = false,
            results = noMatch,
        )
    }

    /**
     * Groups the songs by their truncated score in tens of thousands. For example, a result
     * with a score of 957,370 would appear under the key 95.  The returned list is sorted in
     * descending order of key, and each group's list is sorted in descending order of score.
     */
    private fun List<ChartResultPair>.byScoreInTenThousands(): List<Pair<Int, List<ChartResultPair>>> =
        groupBy { (it.result.safeScore / 10_000).toInt() }
            .mapValues { (_, group) -> group.sortedByDescending { it.result.safeScore } }
            .toList()
            .sortedByDescending { it.first }

    /**
     * Calculates the average score of all songs in this group
     */
    private fun List<ChartResultPair>.averageScore(): Long = if (isNotEmpty()) {
        (sumOf { it.result.safeScore } / size.toDouble())
            .roundToLong()
    } else 0
}

class SongsClearStackedGoalProgressConverter : StackedGoalProgressConverter<SongsClearStackedGoal> {

    override fun getGoalProgress(
        goal: SongsClearStackedGoal,
        stackIndex: Int,
        ladderRank: LadderRank?,
    ): Flow<LadderGoalProgress?> {
        return flowOf(null)
    }
}
