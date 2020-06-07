package com.perrigogames.life4.model

import com.perrigogames.life4.GameConstants
import com.perrigogames.life4.data.*
import com.perrigogames.life4.db.ResultDatabaseHelper
import com.perrigogames.life4.db.SongDatabaseHelper
import org.koin.core.inject

class LadderProgressManager: BaseModel() {

    private val ignoreListManager: IgnoreListManager by inject()
    private val songDataManager: SongDataManager by inject()
    private val dbHelper: SongDatabaseHelper by inject()
    private val trialManager: TrialManager by inject()
    private val ladderResults: ResultDatabaseHelper by inject()

    fun getGoalProgress(goal: BaseRankGoal): LadderGoalProgress? = when (goal) {
//        is DifficultyClearGoal -> {
//            val charts = if (goal.count == null) {
//                songDataManager.getFilteredChartsByDifficulty(goal.difficultyNumbers, goal.playStyle).filterNot {
//                    ignoreListManager.selectedIgnoreChartIds.contains(it.id) ||
//                            ignoreListManager.selectedIgnoreSongIds.contains(it.song.targetId) ||
//                            goal.songExceptions?.contains(it.song.target.title) == true }
//            } else songDataManager.getChartsByDifficulty(goal.difficultyNumbers, goal.playStyle)
//            val chartIds = charts.map { it.id }.toLongArray()
//            val results = ladderResults.getResultsById(chartIds).toMutableList()
//            if (results.isEmpty()) {
//                null // return
//            } else {
//                val resultIds = results.map { it.chart.target.id }.toSortedSet()
//                val notFound = getOrCreateResultsForCharts(charts.filterNot { resultIds.contains(it.id) })
//                results.addAll(notFound)
//                goal.getGoalProgress(charts.size, results) // return
//            }
//        }
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
            LadderGoalProgress(ladderResults.selectMFCs().sumBy {
                GameConstants.mfcPointsForDifficulty(it.difficultyNumber.toInt())
            }, goal.points)
        }
//        is SongSetClearGoal -> when {
//            goal.songs != null -> {
//                val songs = dbHelper.selectSongsAndChartsByTitle(goal.songs)
//                val charts = songs.map { entry -> entry.value.filter {
//                    goal.difficulties.contains(it.difficultyClass) && goal.playStyle == it.playStyle
//                } }
//                if (goal.score != null) { // clear chart with target score
//                    if (charts.size == 1) { // single chart, show the score
//                        val currentScore = charts[0].plays.maxBy { it.score }?.score
//                        currentScore?.let { curr ->
//                            LadderGoalProgress(curr, goal.score, showMax = false)
//                        }
//                    } else { // multiple charts, show songs satisfied
//                        val doneCount = charts.count { chart ->
//                            (chart.plays.maxBy { it.score }?.score ?: 0) > goal.score
//                        }
//                        LadderGoalProgress(doneCount, charts.size)
//                    }
//                } else { // simply clear chart
//                    val doneCount = charts.count {
//                        it.plays.maxBy { play -> play.clearType.stableId }?.clearType?.passing ?: false
//                    }
//                    LadderGoalProgress(doneCount, charts.size)
//                }
//            }
//            else -> null
//        }
        else -> null
    }
}