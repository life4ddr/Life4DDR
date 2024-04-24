package com.perrigogames.life4.feature.songresults

import co.touchlab.kermit.Logger
import com.perrigogames.life4.GameConstants
import com.perrigogames.life4.data.BaseRankGoal
import com.perrigogames.life4.data.LadderGoalProgress
import com.perrigogames.life4.data.MFCPointsStackedGoal
import com.perrigogames.life4.data.SongsClearGoal
import com.perrigogames.life4.data.StackedRankGoalWrapper
import com.perrigogames.life4.data.TrialStackedGoal
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.feature.songlist.IgnoreListManager
import com.perrigogames.life4.feature.songlist.SongDataManager
import com.perrigogames.life4.feature.trialrecords.TrialRecordsManager
import com.perrigogames.life4.feature.trials.TrialManager
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.model.BaseModel
import com.perrigogames.life4.model.ChartResultOrganizer
import com.perrigogames.life4.model.safeScore
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.core.component.inject
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalSerializationApi::class)
class SongResultsManager: BaseModel() {

    private val logger: Logger by injectLogger("SongResultsManager")
    private val ignoreListManager: IgnoreListManager by inject()
    private val songDataManager: SongDataManager by inject()
    private val resultDbHelper: ResultDatabaseHelper by inject()
    private val trialManager: TrialManager by inject()
    private val trialRecordsManager: TrialRecordsManager by inject()

    private val chartResultOrganizer = ChartResultOrganizer()

    fun refresh() {
        chartResultOrganizer.refresh()
    }

    fun getGoalProgress(goal: BaseRankGoal): LadderGoalProgress? {
        println("Goal ${goal.id}: $goal")
        when (goal) {
            is SongsClearGoal -> {
                if (goal.diffNum != null) {
                    return when {
                        goal.score != null -> when (goal.songCount) { // score-based goal
                            null -> allSongsScoreProgress(goal)
                            else -> countSongsScoreProgress(goal)
                        }
                        goal.averageScore != null -> allSongsAverageScoreProgress(goal)
                        else -> when { // clear type
                            goal.songCount != null -> countSongsClearProgress(goal)
                            else -> allSongsClearProgress(goal)
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
//                    val trials = trialRecordsManager.bestSessions().filter {
//                        if (goal.mainGoal.restrictDifficulty) {
//                            it.goalRank.stableId == goal.mainGoal.rank.stableId
//                        } else {
//                            it.goalRank.stableId >= goal.mainGoal.rank.stableId
//                        }
//                    }
                    return LadderGoalProgress(
                        progress = 0, // FIXME
                        max = goal.getIntValue(TrialStackedGoal.KEY_TRIALS_COUNT)!!
                    )
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

    private fun countSongsScoreProgress(goal: SongsClearGoal): LadderGoalProgress {
        var completed = 0
        var topValidScore = 0L
        goal.forEachDiffNum { diff ->
            val results = chartResultOrganizer.getResults(
                playStyle = goal.playStyle,
                diffNum = diff,
                populated = true,
                filterIgnored = false,
            )
            val validResults = results.scoresInRange(bottom = goal.score)
            if (validResults.firstOrNull()?.result.safeScore > topValidScore) {
                topValidScore = validResults.first().result.safeScore
            }

            completed += validResults.size
            if (completed >= goal.songCount!!) {
                return@forEachDiffNum
            }
        }

        return if (goal.songCount == 1) {
            val currentScore = min(topValidScore, goal.score!!.toLong())
            LadderGoalProgress(
                progress = currentScore.toInt(),
                max = goal.score,
                showMax = false,
            )
        } else {
            LadderGoalProgress(
                progress = min(completed, goal.songCount!!),
                max = goal.songCount,
                showMax = true,
            )
        }
    }

    private fun countSongsClearProgress(goal: SongsClearGoal): LadderGoalProgress {
        var completed = 0
        goal.forEachDiffNum { diff ->
            val results = chartResultOrganizer.getResults(
                playStyle = goal.playStyle,
                diffNum = diff,
                populated = true,
                filterIgnored = false,
            )
            results.byScoreInTenThousands.forEach { (_, results) ->
                results.forEach { (_, result) ->
                    if (result!!.clearType >= goal.clearType) {
                        completed++
                    }
                    if (completed >= goal.songCount!!) {
                        return@forEachDiffNum
                    }
                }
            }
        }

        return LadderGoalProgress(
            progress = completed,
            max = goal.songCount!!,
            showMax = true,
        )
    }

    private fun allSongsScoreProgress(goal: SongsClearGoal): LadderGoalProgress {
        val results = chartResultOrganizer.getResults(
            playStyle = goal.playStyle,
            diffNum = goal.diffNum!!,
            populated = false,
            filterIgnored = true,
        )

        val belowRequirement = results.scoresInRange(top = goal.score!! - 1)
        val missingSongs = max(0, belowRequirement.size - (goal.exceptions ?: 0))
        return LadderGoalProgress(
            progress = results.results.size - missingSongs,
            max = results.results.size,
            showMax = true,
            results = belowRequirement,
        )
    }

    private fun allSongsAverageScoreProgress(goal: SongsClearGoal): LadderGoalProgress {
        val results = chartResultOrganizer.getResults(
            playStyle = goal.playStyle,
            diffNum = goal.diffNum!!,
            populated = false,
            filterIgnored = true,
        )

        val belowRequirement = results.scoresInRange(top = goal.averageScore!! - 1)
        return LadderGoalProgress(
            progress = min(results.averageScore.toInt(), goal.averageScore),
            max = goal.averageScore,
            showMax = false,
            results = belowRequirement,
        )
    }

    private fun allSongsClearProgress(goal: SongsClearGoal): LadderGoalProgress {
        val results = chartResultOrganizer.getResults(
            playStyle = goal.playStyle,
            diffNum = goal.diffNum!!,
            populated = false,
            filterIgnored = true,
        )

        val belowRequirement = (0 until goal.clearType.ordinal)
            .flatMap { results.byClearType[ClearType.entries[it]] ?: emptyList() }
        val missingSongs = belowRequirement.size - (goal.exceptions ?: 0)
        return LadderGoalProgress(
            progress = results.results.size - missingSongs,
            max = results.results.size,
            showMax = true,
            results = belowRequirement,
        )
    }

    internal fun clearAllResults() {
        resultDbHelper.deleteAll()
        chartResultOrganizer.refresh()
    }
}