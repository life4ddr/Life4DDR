package com.perrigogames.life4.model

import co.touchlab.kermit.Logger
import com.perrigogames.life4.GameConstants
import com.perrigogames.life4.data.*
import com.perrigogames.life4.db.ChartResult
import com.perrigogames.life4.db.DetailedChartInfo
import com.perrigogames.life4.db.ResultDatabaseHelper
import com.perrigogames.life4.injectLogger
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.core.component.inject
import kotlin.math.max

@OptIn(ExperimentalSerializationApi::class)
class LadderProgressManager: BaseModel() {

    private val logger: Logger by injectLogger("LadderProgressManager")
    private val ignoreListManager: IgnoreListManager by inject()
    private val songDataManager: SongDataManager by inject()
    private val resultDbHelper: ResultDatabaseHelper by inject()
    private val trialManager: TrialManager by inject()

    private lateinit var resultsList: List<ChartResult>
    private lateinit var matchedResults: Map<DetailedChartInfo, ChartResult?>

    fun mapToResults(charts: List<DetailedChartInfo>): Map<DetailedChartInfo, ChartResult?> {
        return charts.associateWith { matchedResults[it] }
    }

    fun getGoalProgress(goal: BaseRankGoal): LadderGoalProgress? = when (goal) {
        is SongsClearGoal -> {
            if (goal.songCount == 1 && goal.score != null && goal.diffNum != null) {
                var cumHighestScore = 0L
                val topValidScore = matchedResults.filter { (chart, result) ->
                    val maxLevel = if (goal.allowsHigherDiffNum) 999 else goal.diffNum
                    result?.score?.let { score ->
                        if (score < cumHighestScore) {
                            return@filter false
                        } else {
                            cumHighestScore = score
                        }
                    } ?: return@filter false
                    chart.playStyle == goal.playStyle &&
                            (goal.diffNum..maxLevel).contains(chart.difficultyNumber)
                }
                    .entries
                    .maxByOrNull { (_, result) -> result!!.score }
                    ?.value?.score ?: 0

                val currentScore = max(topValidScore, goal.score.toLong())
                LadderGoalProgress(
                    progress = currentScore.toInt(),
                    max = goal.score,
                    showMax = false,
                )
            }
//            val charts = songDataManager.detailedCharts
//                .filter {
//                    goal.playStyle == it.playStyle &&
//                            goal.diffClassSet?.match(it.difficultyClass) ?: true
//                }.filter {
//                    val diffNumMatch = goal.diffNum?.let { diffNum ->
//                        if (goal.allowsHigherDiffNum) {
//                            it.difficultyNumber >= diffNum.toLong()
//                        } else {
//                            it.difficultyNumber == diffNum.toLong()
//                        }
//                    } ?: true
//                    diffNumMatch && when {
//                        goal.diffNum != null -> true
//                        goal.diffClassSet != null -> when {
//                            goal.songs != null -> goal.songs.contains(it.title)
//                            goal.folderType != null -> when (goal.folderType) {
//                                is FolderType.Letter -> it.title.startsWith(goal.folderType.letter)
//                                is FolderType.Version -> it.version == goal.folderType.version
//                            }
//                            goal.folderCount != null -> return null //FIXME
//                            else -> error("Illegal goal configuration: ${goal.id}")
//                        }
//                        else -> error("Illegal goal configuration: ${goal.id}")
//                    }
//                } - ignoreListManager.getCurrentlyLockedSongs()

//            val ignores = ignoreListManager.getCurrentlyLockedSongs() FIXME

//            val chartIds = charts.map { it.skillId }
//            val results = resultsList.filter { chartIds.contains(it.skillId) }
//                .toMutableList()

//            val satisfied = mutableListOf<ChartResultPair>()
//            val unsatisfied = mutableListOf<ChartResultPair>()
//
//            charts.mapNotNull { chart ->
//                val result = results.find { it.matches(chart) } ?: chart.toResult()
//                ChartResultPair(chart = chart, result = result)
//            }
//                .forEach { pair ->
//                    val targetList = if (
//                        (goal.score == null || pair.result.score >= goal.score) && // satisfies score
//                        true && // satisfies average score FIXME deal with this
//                        pair.result.clearType >= goal.clearType // satisfies clear type
//                    ) {
//                        satisfied
//                    } else unsatisfied
//                    targetList.add(pair)
//                }

            logger.v("Goal ${goal.id}: ${goal.diffNum}, ${goal.score}, ${goal.clearType}")
//            LadderGoalProgress(
//                progress = satisfied.size,
//                max = charts.size,
//                showMax = true,
//                unsatisfied.sortedByDescending { it.result.score },
//            )
            LadderGoalProgress(
                progress = 0,
                max = 0,
                showMax = true,
            )
        }
        is TrialGoal -> {
            val trials = trialManager.bestSessions().filter {
                if (goal.restrictDifficulty) {
                    it.goalRank.stableId == goal.rank.stableId
                } else {
                    it.goalRank.stableId >= goal.rank.stableId
                }
            }
            LadderGoalProgress(trials.size, goal.count) // return
        }
        is MFCPointsGoal -> {
            val points = songDataManager
                .matchWithDetailedCharts(resultDbHelper.selectMFCs())
                .sumOf {
                    GameConstants.mfcPointsForDifficulty(it.chart.difficultyNumber.toInt())
                }
            LadderGoalProgress(points, goal.points.toDouble())
        }
        else -> null
    }

    internal fun refresh() {
        resultsList = resultDbHelper.selectAll()

        val tempResults = resultsList.toMutableSet()
        matchedResults = songDataManager.detailedCharts
            .associateWith { chart ->
                tempResults.firstOrNull { result ->
                    chart.skillId == result.skillId &&
                            chart.playStyle == result.playStyle &&
                            chart.difficultyClass == result.difficultyClass
                }.also { tempResults.remove(it) }
            }
    }

    internal fun clearAllResults() {
        resultDbHelper.deleteAll()
        resultsList = emptyList()
    }
}