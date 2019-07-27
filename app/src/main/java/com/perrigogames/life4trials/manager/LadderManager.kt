package com.perrigogames.life4trials.manager

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity
import com.perrigogames.life4trials.data.*
import com.perrigogames.life4trials.db.*
import com.perrigogames.life4trials.event.LadderImportCompletedEvent
import com.perrigogames.life4trials.event.LadderRankUpdatedEvent
import com.perrigogames.life4trials.ui.managerimport.ScoreManagerImportEntryDialog
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.SharedPrefsUtil
import com.perrigogames.life4trials.util.loadRawString
import java.util.*

class LadderManager(val context: Context,
                    val songDataManager: SongDataManager): BaseManager() {

    //
    // Ladder Data
    //
    val ladderData = DataUtil.gson.fromJson(context.loadRawString(R.raw.ranks), LadderRankData::class.java)!!

    //
    // ObjectBoxes
    //
    private val goalsBox get() = objectBox.boxFor(GoalStatusDB::class.java)
    private val ladderResultBox get() = objectBox.boxFor(LadderResultDB::class.java)

    //
    // Queries
    //
    private val goalStatusQuery = goalsBox.query()
        .equal(GoalStatusDB_.goalId, 0).parameterAlias("id")
        .build()
    private val goalMultiStatusQuery = goalsBox.query()
        .`in`(GoalStatusDB_.goalId, LongArray(0)).parameterAlias("ids")
        .build()
    private val ladderResultQuery = ladderResultBox.query()
        .`in`(LadderResultDB_.chartId, LongArray(0)).parameterAlias("ids")
        .build()

    //
    // Local User Rank
    //
    fun getUserRank(): LadderRank? =
        LadderRank.parse(SharedPrefsUtil.getUserString(context, SettingsActivity.KEY_INFO_RANK)?.toLongOrNull())

    fun setUserRank(rank: LadderRank?) {
        SharedPrefsUtil.setUserString(context, SettingsActivity.KEY_INFO_RANK, rank?.stableId.toString())
        Life4Application.eventBus.post(LadderRankUpdatedEvent())
    }

    //
    // Rank Navigation
    //
    fun findRankEntry(rank: LadderRank) = ladderData.rankRequirements.firstOrNull { it.rank == rank }

    fun previousEntry(rank: LadderRank) = previousEntry(ladderData.rankRequirements.indexOfFirst { it.rank == rank })

    fun previousEntry(index: Int) = ladderData.rankRequirements.getOrNull(index - 1)

    fun nextEntry(rank: LadderRank?) =
        if (rank != null) nextEntry(ladderData.rankRequirements.indexOfFirst { it.rank == rank })
        else ladderData.rankRequirements.getOrNull(0)

    fun nextEntry(index: Int) = ladderData.rankRequirements.getOrNull(index + 1)

    //
    // Goal State
    //
    fun getGoalStatus(goal: BaseRankGoal): GoalStatusDB? =
        goalStatusQuery.setParameter("id", goal.id.toLong()).findFirst()

    fun getGoalStatuses(goals: List<BaseRankGoal>): List<GoalStatusDB> =
        goalMultiStatusQuery.setParameters("ids", goals.map { it.id.toLong() }.toLongArray()).find()

    fun getOrCreateGoalStatus(goal: BaseRankGoal): GoalStatusDB {
        var goalDB = getGoalStatus(goal)
        if (goalDB == null) {
            goalDB = GoalStatusDB(goal.id.toLong())
            goalsBox.put(goalDB)
        }
        return goalDB
    }

    fun setGoalState(goal: BaseRankGoal, status: GoalStatus) {
        val statusDB = getGoalStatus(goal)
        if (statusDB == null) {
            goalsBox.put(GoalStatusDB(goal.id.toLong(), status))
        } else {
            setGoalState(statusDB, status)
        }
    }

    fun setGoalState(goalDB: GoalStatusDB, status: GoalStatus) {
        goalDB.date = Date()
        goalDB.status = status
        goalsBox.put(goalDB)
    }

    //
    // Imported Score Data
    //
    fun getGoalProgress(goal: BaseRankGoal): LadderGoalProgress? = when (goal) {
        is DifficultyClearGoal -> {
            val charts = songDataManager.getChartsByDifficulty(goal.difficultyNumbers)
            val filtered = charts.filterNot { songDataManager.selectedIgnoreChartIds!!.contains(it.id) ||
                        songDataManager.selectedIgnoreSongIds!!.contains(it.song.targetId)}
            val results = ladderResultQuery.setParameters("ids", filtered.map { it.id }.toLongArray()).find()
            goal.getGoalProgress(results)
        }
        else -> null
    }

    fun showImportFlow(activity: FragmentActivity) {
        ScoreManagerImportEntryDialog(object: ScoreManagerImportEntryDialog.Listener {

            override fun onDialogCancelled() = Unit

            override fun onDataSubmitted(data: String) = importManagerData(data)

        }).show(activity.supportFragmentManager, ScoreManagerImportEntryDialog.TAG)
    }

    fun importManagerData(dataString: String) {
        var success = 0
        var errors = 0
        dataString.lines().forEach { entry ->
            val entryParts = entry.trim().split(';')
            if (entryParts.size >= 4) {
                // format = %p:b:B:D:E:C%%y:SP:DP%;%d%;%s0%;%l%;%f:mfc:pfc:gfc:fc:life4:clear%;%e%;%a%;%t%
                try {
                    val chartType = entryParts[0] // ESP
                    val difficultyNumber = entryParts[1].toInt()
                    val score = entryParts[2].toInt()
                    // need 5 and 6 first
                    val clears = entryParts[5].toIntOrNull() ?: 0
                    val plays = entryParts[6].toIntOrNull() ?: 0

                    var clear = ClearType.parse(entryParts[4])!!
                    if (clear == ClearType.CLEAR) {
                        when {
                            entryParts[3] == "-" -> clear = ClearType.NO_PLAY
                            entryParts[3] == "E" -> clear = if (clears > 0) ClearType.CLEAR else ClearType.FAIL
                        }
                    }

                    val songName = entryParts.subList(7, entryParts.size).joinToString(";")

                    val playStyle = PlayStyle.parse(chartType)!!
                    val difficultyClass = DifficultyClass.parse(chartType)!!

                    val songDB = songDataManager.getOrCreateSong(songName)
                    val chartDB = songDataManager.updateOrCreateChartForSong(songDB, playStyle, difficultyClass, difficultyNumber)
                    val resultDB = updateOrCreateResultForChart(chartDB, score, clear)

                    if (BuildConfig.DEBUG && resultDB.clearType == ClearType.NO_PLAY) {
                        Log.v("import", "${songDB.title} - ${chartDB.difficultyClass} (${chartDB.difficultyNumber})")
                    }
                    success++
                } catch (e: Exception) {
                    errors++
                    Log.e("Exception", e.toString())
                }
            } else if (entry.isNotEmpty()) {
                errors++
            }
        }
        Toast.makeText(context, context.getString(R.string.import_finished, success, errors), Toast.LENGTH_SHORT).show()
        songDataManager.invalidateIgnoredIds()
        Life4Application.eventBus.post(LadderImportCompletedEvent(success, errors))
    }

    fun clearGoalData() {
        AlertDialog.Builder(context)
            .setTitle(R.string.are_you_sure)
            .setMessage(R.string.confirm_erase_rank_data)
            .setPositiveButton(R.string.yes) { _, _ ->
                goalsBox.removeAll()
                ladderResultBox.removeAll()
                Life4Application.eventBus.post(LadderRankUpdatedEvent())
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun updateOrCreateResultForChart(chart: ChartDB, score: Int, clear: ClearType): LadderResultDB {
        val result = chart.plays.firstOrNull()
        result?.let {
            if (it.score != score || it.clearType != clear) {
                it.score = score
                it.clearType = clear
                ladderResultBox.put(it)
            }
        }
        return result ?: LadderResultDB(score, clear).also {
            chart.plays.add(it)
            songDataManager.updateChart(chart)
            ladderResultBox.put(it)
        }
    }
}