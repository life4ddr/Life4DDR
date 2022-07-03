package com.perrigogames.life4.model

import co.touchlab.kermit.Logger
import com.perrigogames.life4.GameConstants
import com.perrigogames.life4.data.*
import com.perrigogames.life4.data.SongsClearGoal.FolderType
import com.perrigogames.life4.db.ChartResultPair
import com.perrigogames.life4.db.ResultDatabaseHelper
import com.perrigogames.life4.db.matches
import com.perrigogames.life4.db.toResult
import com.perrigogames.life4.injectLogger
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.core.component.inject

@OptIn(ExperimentalSerializationApi::class)
class LadderProgressManager: BaseModel() {

    private val logger: Logger by injectLogger("LadderProgressManager")
    private val ignoreListManager: IgnoreListManager by inject()
    private val songDataManager: SongDataManager by inject()
    private val resultDbHelper: ResultDatabaseHelper by inject()
    private val trialManager: TrialManager by inject()
    private val ladderResults: ResultDatabaseHelper by inject()

    fun getGoalProgress(goal: BaseRankGoal): LadderGoalProgress? = when (goal) {
        is SongsClearGoal -> {
            val charts = songDataManager.detailedCharts.filter {
                val diffNumMatch = goal.diffNum?.let { diffNum ->
                    if (goal.allowsHigherDiffNum) {
                        it.difficultyNumber >= diffNum.toLong()
                    } else {
                        it.difficultyNumber == diffNum.toLong()
                    }
                } ?: true
                val diffClassMatch = goal.diffClassSet?.match(it.difficultyClass) ?: true
                val playStyleMatch = goal.playStyle == it.playStyle
                diffNumMatch && diffClassMatch && playStyleMatch && when {
                    goal.diffNum != null -> true
                    goal.diffClassSet != null && goal.songs != null -> goal.songs.contains(it.title)
                    goal.diffClassSet != null && goal.folderType != null -> when (goal.folderType) {
                        is FolderType.Letter -> it.title.startsWith(goal.folderType.letter)
                        is FolderType.Version -> it.version == goal.folderType.version
                    }
                    else -> error("Illegal goal configuration: ${goal.id}")
                }
            } - ignoreListManager.getCurrentlyLockedSongs()

            val ignores = ignoreListManager.getCurrentlyLockedSongs()

            val results = resultDbHelper.selectCharts(
                charts.map { it.skillId }
            ).toMutableList()

            val satisfied = mutableListOf<ChartResultPair>()
            val unsatisfied = mutableListOf<ChartResultPair>()

            charts.mapNotNull { chart ->
                val result = results.find { it.matches(chart) } ?: chart.toResult()
                ChartResultPair(chart = chart, result = result)
            }
                .sortedByDescending { it.result.score }
                .forEach { pair ->
                    val satisfiesScore = goal.score == null || pair.result.score >= goal.score
                    val satisfiesAverageScore = true // FIXME deal with this
                    val satisfiesClearType = pair.result.clearType >= goal.clearType //WHAT?
                    if (satisfiesScore && satisfiesAverageScore && satisfiesClearType) {
                        satisfied.add(pair)
                    } else {
                        unsatisfied.add(pair)
                    }
                }

            logger.v("Goal ${goal.id}: ${goal.diffNum}, ${goal.score}, ${goal.clearType}")
            LadderGoalProgress(
                progress = satisfied.size,
                max = charts.size,
                showMax = true,
                unsatisfied,
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
                .matchWithDetailedCharts(ladderResults.selectMFCs())
                .sumOf {
                    GameConstants.mfcPointsForDifficulty(it.chart.difficultyNumber.toInt())
                }
            LadderGoalProgress(points, goal.points.toDouble())
        }
        else -> null
    }
}