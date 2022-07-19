package com.perrigogames.life4.model

import co.touchlab.kermit.Logger
import com.perrigogames.life4.GameConstants
import com.perrigogames.life4.data.*
import com.perrigogames.life4.db.ChartResult
import com.perrigogames.life4.db.DetailedChartInfo
import com.perrigogames.life4.db.ResultDatabaseHelper
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.isDebug
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.core.component.inject
import kotlin.math.min

@OptIn(ExperimentalSerializationApi::class)
class LadderProgressManager: BaseModel() {

    private val logger: Logger by injectLogger("LadderProgressManager")
    private val ignoreListManager: IgnoreListManager by inject()
    private val songDataManager: SongDataManager by inject()
    private val resultDbHelper: ResultDatabaseHelper by inject()
    private val trialManager: TrialManager by inject()

    private lateinit var matchedResults: Map<PlayStyle, Map<Int, Map<DetailedChartInfo, ChartResult?>>>
    private lateinit var populatedResults: Map<PlayStyle, Map<Int, Map<DetailedChartInfo, ChartResult>>>

    fun mapToResults(charts: List<DetailedChartInfo>): Map<DetailedChartInfo, ChartResult?> {
        return charts.associateWith {
            getResultsMap(it.playStyle, it.difficultyNumber.toInt(), onlyPopulated = false)[it]
        }
    }

    fun getGoalProgress(goal: BaseRankGoal): LadderGoalProgress? {
        when (goal) {
            is SongsClearGoal -> {
                if (goal.diffNum != null) {
                    if (goal.score != null) { // score-based goal
                        if (goal.songCount == 1) { // single song above score
                            var topValidScore = 0L
                            goal.forEachDiffNum { diff ->
                                getResults(goal.playStyle, diff, onlyPopulated = true)
                                    .forEach {result ->
                                        if (result!!.score > topValidScore) {
                                            topValidScore = result.score
                                        }
                                        if (topValidScore >= goal.score) {
                                            return LadderGoalProgress(
                                                progress = goal.score,
                                                max = goal.score,
                                                showMax = false,
                                            )
                                        }
                                    }
                            }

                            val currentScore = min(topValidScore, goal.score.toLong())
                            return LadderGoalProgress(
                                progress = currentScore.toInt(),
                                max = goal.score,
                                showMax = false,
                            )
                        } else if (goal.songCount == null) { // all songs over score
                            val eligibleResults = goal.diffNumRange!!.flatMap { diffNum ->
                                getResults(goal.playStyle, diffNum, onlyPopulated = false)
                            }
                            val underScores = eligibleResults
                                .filter { (it?.score ?: 0) < goal.score }
                                .sortedBy { it?.score ?: 0 }
                                .toMutableList()

                            for (i in 1..goal.safeExceptions) {
                                if (underScores.isEmpty()) {
                                    break
                                }
                                underScores.removeAt(0)
                            }
                            return LadderGoalProgress(
                                progress = underScores.count(),
                                max = eligibleResults.count() - goal.safeExceptions,
                                showMax = false,
                            )
                        }
                    } else if (goal.averageScore != null) { // average score type

                    } else { // clear type
                        if (goal.songCount != null) { // number of songs with clear
                            var remainingSongs = goal.songCount
                            goal.forEachDiffNum { diff ->
                                getResults(goal.playStyle, diff, onlyPopulated = true)
                                    .forEach { result ->
                                        if (result!!.clearType >= goal.clearType) {
                                            remainingSongs -= 1
                                        }
                                        if (remainingSongs == 0) {
                                            return LadderGoalProgress(
                                                progress = goal.songCount,
                                                max = goal.songCount,
                                                showMax = true,
                                            )
                                        }
                                    }
                            }
                            return LadderGoalProgress(
                                progress = goal.songCount - remainingSongs,
                                max = goal.songCount,
                                showMax = true,
                            )
                        } else { // all songs with clear

                        }
                    }
                }

                logger.v("Goal ${goal.id}: ${goal.diffNum}, ${goal.score}, ${goal.clearType}")
                return LadderGoalProgress(
                    progress = 0,
                    max = 0,
                    showMax = true,
                )
            }
            is StackedRankGoalWrapper -> when (goal.mainGoal) {
                is TrialStackedGoal -> {
                    val trials = trialManager.bestSessions().filter {
                        if (goal.mainGoal.restrictDifficulty) {
                            it.goalRank.stableId == goal.mainGoal.rank.stableId
                        } else {
                            it.goalRank.stableId >= goal.mainGoal.rank.stableId
                        }
                    }
                    return LadderGoalProgress(trials.size, goal.getIntValue(TrialStackedGoal.KEY_TRIALS_COUNT)!!)
                }
                is MFCPointsStackedGoal -> {
                    val points = songDataManager
                        .matchWithDetailedCharts(resultDbHelper.selectMFCs())
                        .sumOf {
                            GameConstants.mfcPointsForDifficulty(it.chart.difficultyNumber.toInt())
                        }
                    return LadderGoalProgress(
                        progress = points,
                        max = goal.getIntValue(MFCPointsStackedGoal.KEY_MFC_POINTS)!!.toDouble()
                    )
                }
                else -> return null
            }
            else -> return null
        }
    }

    private fun getResultsMap(
        playStyle: PlayStyle,
        diffNum: Int,
        onlyPopulated: Boolean
    ): Map<DetailedChartInfo, ChartResult?> {
        val target = if (onlyPopulated) populatedResults else matchedResults
        return target[playStyle]!![diffNum]!!
    }

    private fun getResults(
        playStyle: PlayStyle,
        diffNum: Int,
        onlyPopulated: Boolean
    ) : Collection<ChartResult?> = getResultsMap(playStyle, diffNum, onlyPopulated).values

    internal fun refresh() {
        val tempResults = resultDbHelper.selectAll().toMutableSet()
        matchedResults = songDataManager.detailedCharts
            .groupBy { it.playStyle }
            .mapValues { (_, charts) ->
                charts.groupBy { it.difficultyNumber.toInt() }
                    .mapValues { (_, charts2) ->
                        charts2.associateWith { chart ->
                            tempResults.firstOrNull { result ->
                                chart.id == result.chartId
                            }.also { tempResults.remove(it) }
                        }
                    }
            }

        @Suppress("UNCHECKED_CAST")
        populatedResults = matchedResults.mapValues { (_, chartMap) ->
            chartMap.mapValues { (_, chartMap2) ->
                chartMap2.filterValues { it != null } as Map<DetailedChartInfo, ChartResult>
            }
        }

        if (isDebug) {
            populatedResults.forEach { (_, a) ->
                a.forEach { (_, b) ->
                    b.forEach { (chart, result) ->
                        logger.v("Result: ${chart.toStringExt()} / ${result.toStringExt()}")
                    }
                }
            }
        }
    }

    internal fun clearAllResults() {
        resultDbHelper.deleteAll()
        matchedResults = emptyMap()
        populatedResults = emptyMap()
    }
}