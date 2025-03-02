package com.perrigogames.life4.feature.ladder.converter

import co.touchlab.kermit.Logger
import com.perrigogames.life4.GameConstants
import com.perrigogames.life4.data.LadderGoalProgress
import com.perrigogames.life4.data.SongsClearGoal
import com.perrigogames.life4.data.SongsClearStackedGoal
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.feature.songresults.ChartFilterState
import com.perrigogames.life4.feature.songresults.ChartResultOrganizer
import com.perrigogames.life4.feature.songresults.FilterState
import com.perrigogames.life4.feature.songresults.FilterState.Companion.DEFAULT_DIFFICULTY_NUMBER_RANGE
import com.perrigogames.life4.feature.songresults.ResultFilterState
import com.perrigogames.life4.injectLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SongsClearGoalProgressConverter : GoalProgressConverter<SongsClearGoal>, KoinComponent {

    private val chartResultOrganizer: ChartResultOrganizer by inject()
    private val logger: Logger by injectLogger("SongsClearGoalProgressConverter")

    override fun getGoalProgress(
        goal: SongsClearGoal
    ): Flow<LadderGoalProgress?> {
        val config = FilterState(
            chartFilter = ChartFilterState(
                selectedPlayStyle = goal.playStyle,
                difficultyClassSelection = goal.diffClassSet?.set ?: DifficultyClass.entries,
                difficultyNumberRange = goal.diffNumRange ?: DEFAULT_DIFFICULTY_NUMBER_RANGE,
            ),
            resultFilter = ResultFilterState(
                clearTypeRange = goal.clearType.ordinal .. ClearType.entries.size,
                scoreRange = ((goal.score ?: 0) .. GameConstants.MAX_SCORE),
                filterIgnored = true
            )
        )
        return chartResultOrganizer.resultsForConfig(config)
            .map { (match, noMatch) ->
                LadderGoalProgress(
                    progress = match.size,
                    max = match.size + noMatch.size,
                    showMax = true,
                    results = noMatch
                )
            }

//        if (goal.diffNum != null) {
//            return when {
//                goal.score != null -> when (goal.songCount) { // score-based goal
//                    null -> allSongsScoreProgress(goal)
//                    else -> countSongsScoreProgress(goal)
//                }
//                goal.averageScore != null -> allSongsAverageScoreProgress(goal)
//                else -> when { // clear type
//                    goal.songCount != null -> countSongsClearProgress(goal)
//                    else -> allSongsClearProgress(goal)
//                }
//            }
//        }
//
//        logger.v("Goal ${goal.id}: ${goal.diffNum}, ${goal.score}, ${goal.clearType}")
//        return LadderGoalProgress(
//            progress = 0,
//            max = 0,
//            showMax = true,
//        )
    }

//    private fun countSongsScoreProgress(goal: SongsClearGoal): LadderGoalProgress {
//        var completed = 0
//        var topValidScore = 0L
//        goal.forEachDiffNum { diff ->
//            val results = chartResultOrganizer.resultsForConfig(
//                FilterState(
//                    selectedPlayStyle = goal.playStyle,
//                    difficultyNumberRange = diff..diff,
//                    scoreRange = (goal.score!! .. GameConstants.MAX_SCORE),
//                )
//            ).value // FIXME
//            val validResults = results.scoresInRange(bottom = goal.score)
//            if (validResults.firstOrNull()?.result.safeScore > topValidScore) {
//                topValidScore = validResults.first().result.safeScore
//            }
//
//            completed += validResults.size
//            if (completed >= goal.songCount!!) {
//                return@forEachDiffNum
//            }
//        }
//
//        return if (goal.songCount == 1) {
//            val currentScore = min(topValidScore, goal.score!!.toLong())
//            LadderGoalProgress(
//                progress = currentScore.toInt(),
//                max = goal.score,
//                showMax = false,
//            )
//        } else {
//            LadderGoalProgress(
//                progress = min(completed, goal.songCount!!),
//                max = goal.songCount,
//                showMax = true,
//            )
//        }
//    }
//
//    private fun countSongsClearProgress(goal: SongsClearGoal): LadderGoalProgress {
//        var completed = 0
//        goal.forEachDiffNum { diff ->
//            val results = chartResultOrganizer.resultsForConfig(
//                FilterState(
//                    selectedPlayStyle = goal.playStyle,
//                    difficultyNumberRange = diff..diff,
//                    resultFilter = ResultFilterState(
//                        scoreRangeBottomValue = 1,
//                        filterIgnored = false,
//                    )
//                )
//            ).value // FIXME
//            results.byScoreInTenThousands().forEach { (_, results) ->
//                results.forEach { (_, result) ->
//                    if (result!!.clearType >= goal.clearType) {
//                        completed++
//                    }
//                    if (completed >= goal.songCount!!) {
//                        return@forEachDiffNum
//                    }
//                }
//            }
//        }
//
//        return LadderGoalProgress(
//            progress = completed,
//            max = goal.songCount!!,
//            showMax = true,
//        )
//    }
//
//    private fun allSongsScoreProgress(goal: SongsClearGoal): LadderGoalProgress {
//        val results = chartResultOrganizer.resultsForConfig(
//            FilterState(
//                selectedPlayStyle = goal.playStyle,
//                difficultyNumberRange = goal.diffNum!!.toInclusiveRange(),
//                filterIgnored = true,
//            )
//        ).value // FIXME
//
//        val belowRequirement = results.scoresInRange(top = goal.score!! - 1)
//        val missingSongs = max(0, belowRequirement.size - (goal.exceptions ?: 0))
//        return LadderGoalProgress(
//            progress = results.size - missingSongs,
//            max = results.size,
//            showMax = true,
//            results = belowRequirement,
//        )
//    }
//
//    private fun allSongsAverageScoreProgress(goal: SongsClearGoal): LadderGoalProgress {
//        val results = chartResultOrganizer.resultsForConfig(
//            FilterState(
//                selectedPlayStyle = goal.playStyle,
//                difficultyNumberRange = goal.diffNum!!.toInclusiveRange(),
//                resultFilter = ResultFilterState(
//                    filterIgnored = true,
//                ),
//            )
//        ).value // FIXME
//
//        val belowRequirement = results.scoresInRange(top = goal.averageScore!! - 1)
//        return LadderGoalProgress(
//            progress = min(results.averageScore().toInt(), goal.averageScore),
//            max = goal.averageScore,
//            showMax = false,
//            results = belowRequirement,
//        )
//    }
//
//    private fun allSongsClearProgress(goal: SongsClearGoal): LadderGoalProgress {
//        val results = chartResultOrganizer.resultsForConfig(
//            FilterState(
//                selectedPlayStyle = goal.playStyle,
//                difficultyNumberRange = goal.diffNum!!.toInclusiveRange(),
//                filterIgnored = true,
//            )
//        ).value // FIXME
//
//        val belowRequirement = (0 until goal.clearType.ordinal)
//            .flatMap { results.byClearType()[ClearType.entries[it]] ?: emptyList() }
//        val missingSongs = belowRequirement.size - (goal.exceptions ?: 0)
//        return LadderGoalProgress(
//            progress = results.size - missingSongs,
//            max = results.size,
//            showMax = true,
//            results = belowRequirement,
//        )
//    }
//
//    /**
//     * Groups the songs by their truncated score in tens of thousands. For example, a result
//     * with a score of 957,370 would appear under the key 95.  The returned list is sorted in
//     * descending order of key, and each group's list is sorted in descending order of score.
//     */
//    private fun List<ChartResultPair>.byScoreInTenThousands(): List<Pair<Int, List<ChartResultPair>>> =
//        groupBy { (it.result.safeScore / 10_000).toInt() }
//            .mapValues { (_, group) -> group.sortedByDescending { it.result.safeScore } }
//            .toList()
//            .sortedByDescending { it.first }
//
//    private fun List<ChartResultPair>.scoresInRange(
//        bottom: Int? = null,
//        top: Int? = null,
//    ): List<ChartResultPair> {
//        when {
//            (bottom ?: 0) < 0 -> error("Bottom of range exceeds minimum (0)")
//            (top ?: MAX_SCORE) > MAX_SCORE -> error("Top of range exceeds maximum ($MAX_SCORE)")
//        }
//        val bottomIdx = ((bottom ?: 0) / 10_000)
//        val topIdx = ((top ?: MAX_SCORE) / 10_000)
//        val centerSongs = (bottomIdx + 1 until topIdx)
//            .mapNotNull { scoresForGroup(it) }
//            .flatten()
//        val bottomSongs =
//            if (bottom != null) scoresForGroup(bottomIdx).filter { it.result.safeScore >= bottom }
//            else scoresForGroup(bottomIdx)
//        val topSongs =
//            if (top == null) scoresForGroup(topIdx) // MFCs, group 100
//            else scoresForGroup(topIdx).filter { it.result.safeScore <= top }
//        return topSongs + centerSongs + bottomSongs
//    }
//
//    private fun List<ChartResultPair>.scoresForGroup(group: Int): List<ChartResultPair> {
//        val idx = byScoreInTenThousands().binarySearch { group - it.first }
//        return if (idx >= 0) {
//            byScoreInTenThousands()[idx].second
//        } else emptyList()
//    }
//
//    /**
//     * Groups the songs by their clear type.
//     */
//    private fun List<ChartResultPair>.byClearType(): Map<ClearType, List<ChartResultPair>> =
//        groupBy { it.result.safeClear }
//
//    /**
//     * Calculates the average score of all songs in this group
//     */
//    private fun List<ChartResultPair>.averageScore() = if (isNotEmpty()) {
//        (sumOf { it.result.safeScore } / size.toDouble())
//            .roundToLong()
//    } else 0
}

class SongsClearStackedGoalProgressConverter : StackedGoalProgressConverter<SongsClearStackedGoal> {

    override fun getGoalProgress(
        goal: SongsClearStackedGoal,
        stackIndex: Int
    ): Flow<LadderGoalProgress?> {
        return flowOf(null)
    }
}
