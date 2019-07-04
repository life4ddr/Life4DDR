package com.perrigogames.life4trials.manager

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.*
import com.perrigogames.life4trials.db.*
import com.perrigogames.life4trials.event.LadderImportCompletedEvent
import com.perrigogames.life4trials.ui.managerimport.ScoreManagerImportEntryDialog
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.loadRawString
import io.objectbox.kotlin.query
import java.util.*

class LadderManager(val context: Context,
                    val songDataManager: SongDataManager): BaseManager() {

    val ladderData = DataUtil.gson.fromJson(context.loadRawString(R.raw.ranks), LadderRankData::class.java)!!

    private val goalsBox get() = objectBox.boxFor(GoalStatusDB::class.java)
    private val ladderResultBox get() = objectBox.boxFor(LadderResultDB::class.java)

    fun findRank(rank: LadderRank) = ladderData.rankRequirements.firstOrNull { it.rank == rank }

    fun previousEntry(rank: LadderRank) = previousEntry(ladderData.rankRequirements.indexOfFirst { it.rank == rank })

    fun previousEntry(index: Int) = ladderData.rankRequirements.getOrNull(index - 1)

    fun nextEntry(rank: LadderRank) = nextEntry(ladderData.rankRequirements.indexOfFirst { it.rank == rank })

    fun nextEntry(index: Int) = ladderData.rankRequirements.getOrNull(index + 1)

    fun getGoalStatus(goal: BaseRankGoal): GoalStatusDB? {
        goalsBox.query { return equal(GoalStatusDB_.goalId, goal.id.toLong()).build().findFirst() }
        return null
    }

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
                // format = %p:b:B:D:E:C%%y:SP:DP%;%d%;%s0%;%f:mfc:pfc:gfc:fc:life4:clear%;%t%
                try {
                    val chartType = entryParts[0] // ESP
                    val difficultyNumber = entryParts[1].toInt()
                    val score = entryParts[2].toInt()
                    val clear = ClearType.parse(entryParts[3])!!
                    val songName = entryParts.subList(4, entryParts.size).joinToString(";")

                    val playStyle = PlayStyle.parse(chartType)!!
                    val difficultyClass = DifficultyClass.parse(chartType)!!

                    val songDB = songDataManager.getOrCreateSong(songName)
                    val chartDB = songDataManager.updateOrCreateChartForSong(songDB, playStyle, difficultyClass, difficultyNumber)
                    val resultDB = updateOrCreateResultForChart(chartDB, score, clear)

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
        Life4Application.eventBus.post(LadderImportCompletedEvent(success, errors))
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
        return result ?: LadderResultDB().also {
            chart.plays.add(it)
            songDataManager.updateChart(chart)
            ladderResultBox.put(it)
        }
    }
}