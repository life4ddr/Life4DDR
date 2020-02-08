package com.perrigogames.life4trials.manager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_IMPORT_SKIP_DIRECTIONS
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_INFO_RANK
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_INFO_TARGET_RANK
import com.perrigogames.life4trials.api.GithubDataAPI
import com.perrigogames.life4trials.api.MajorVersionedRemoteData
import com.perrigogames.life4trials.data.*
import com.perrigogames.life4trials.db.*
import com.perrigogames.life4trials.event.LadderRankUpdatedEvent
import com.perrigogames.life4trials.event.LadderRanksReplacedEvent
import com.perrigogames.life4trials.event.SongResultsImportCompletedEvent
import com.perrigogames.life4trials.event.SongResultsUpdatedEvent
import com.perrigogames.life4trials.repo.LadderResultRepo
import com.perrigogames.life4trials.repo.SongRepo
import com.perrigogames.life4trials.ui.managerimport.ScoreManagerImportDirectionsDialog
import com.perrigogames.life4trials.ui.managerimport.ScoreManagerImportEntryDialog
import com.perrigogames.life4trials.ui.managerimport.ScoreManagerImportProcessingDialog
import com.perrigogames.life4trials.util.DataUtil
import kotlinx.coroutines.*
import java.util.*

class LadderManager(private val context: Context,
                    private val songRepo: SongRepo,
                    private val ladderResults: LadderResultRepo,
                    private val ignoreListManager: IgnoreListManager,
                    private val songDataManager: SongDataManager,
                    private val trialManager: TrialManager,
                    private val githubDataAPI: GithubDataAPI,
                    private val settingsManager: SettingsManager): BaseManager() {

    //
    // Ladder Data
    //
    private val ladderDataRemote = object: MajorVersionedRemoteData<LadderRankData>(context, R.raw.ranks_v2, RANKS_FILE_NAME, 1) {
        override suspend fun getRemoteResponse() = githubDataAPI.getLadderRanks()

        override fun createLocalDataFromText(text: String): LadderRankData {
            val out = DataUtil.gson.fromJson(text, LadderRankData::class.java)!!
            out.gameVersions.values.flatMap { it.rankRequirements }.forEach {  entry ->
                entry.goals = entry.goalIds.map { id ->
                    out.goals.firstOrNull { it.id == id } ?:
                    error("ID not found: $id")
                }
            }
            return out
        }

        override fun onFetchUpdated(data: LadderRankData) {
            super.onFetchUpdated(data)
            Toast.makeText(context, R.string.ranks_updated, Toast.LENGTH_SHORT).show()
            Life4Application.eventBus.post(LadderRanksReplacedEvent())
        }
    }
    val ladderData: LadderRankData get() = ladderDataRemote.data
    val currentRequirements: LadderVersion
        get() = ignoreListManager.selectedIgnoreList!!.baseVersion.let { version ->
            ladderData.gameVersions[version] ?: error("Rank requirements not found for version $version")
        }

    //
    // ObjectBoxes
    //
    private val goalsBox get() = objectBox.boxFor(GoalStatusDB::class.java)

    //
    // Init
    //
    init {
        ladderDataRemote.start()
    }

    fun onDatabaseMajorUpdate(context: Context) {
        if (!ladderResults.isEmpty) {
            ladderResults.clearRepo()
            Handler().postDelayed({
                AlertDialog.Builder(context)
                    .setTitle(R.string.database_upgraded)
                    .setMessage(R.string.database_replaced_reimport)
                    .setPositiveButton(R.string.okay) { d, _ -> d.dismiss() }
                    .setCancelable(true)
                    .create()
                    .show()
            }, 10)
        }
    }

    //
    // Queries
    //
    private val goalStatusQuery = goalsBox.query()
        .equal(GoalStatusDB_.goalId, 0).parameterAlias("id")
        .build()
    private val goalMultiStatusQuery = goalsBox.query()
        .`in`(GoalStatusDB_.goalId, LongArray(0)).parameterAlias("ids")
        .build()

    //
    // Local User Rank
    //
    fun getUserRank(): LadderRank? =
        LadderRank.parse(settingsManager.getUserString(KEY_INFO_RANK)?.toLongOrNull())

    fun getUserGoalRank(): LadderRank? =
        settingsManager.getUserString(KEY_INFO_TARGET_RANK)?.toLongOrNull()?.let { LadderRank.parse(it) }
            ?: getUserRank()?.let { return LadderRank.values().getOrNull(it.ordinal + 1) }
            ?: LadderRank.WOOD1

    fun setUserRank(rank: LadderRank?) {
        settingsManager.setUserString(KEY_INFO_RANK, rank?.stableId.toString())
        settingsManager.setUserString(KEY_INFO_TARGET_RANK, "")
        Life4Application.eventBus.post(LadderRankUpdatedEvent())
    }

    fun setUserTargetRank(rank: LadderRank?) {
        settingsManager.setUserString(KEY_INFO_TARGET_RANK, rank?.stableId.toString())
        Life4Application.eventBus.post(LadderRankUpdatedEvent())
    }

    //
    // Rank Navigation
    //
    fun findRankEntry(rank: LadderRank?) = currentRequirements.rankRequirements.firstOrNull { it.rank == rank }

    fun previousEntry(rank: LadderRank?) = previousEntry(currentRequirements.rankRequirements.indexOfFirst { it.rank == rank })

    fun previousEntry(index: Int) = currentRequirements.rankRequirements.getOrNull(index - 1)

    fun nextEntry(rank: LadderRank?) = nextEntry(currentRequirements.rankRequirements.indexOfFirst { it.rank == rank })

    fun nextEntry(index: Int) = currentRequirements.rankRequirements.getOrNull(index + 1)

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
    private var importJob: Job? = null

    fun getGoalProgress(goal: BaseRankGoal, playStyle: PlayStyle): LadderGoalProgress? = when (goal) {
        is DifficultyClearGoal -> {
            val charts = if (goal.count == null) {
                songDataManager.getFilteredChartsByDifficulty(goal.difficultyNumbers, playStyle).filterNot {
                    ignoreListManager.selectedIgnoreChartIds.contains(it.id) ||
                    ignoreListManager.selectedIgnoreSongIds.contains(it.song.targetId) ||
                    goal.songExceptions?.contains(it.song.target.title) == true }
            } else songDataManager.getChartsByDifficulty(goal.difficultyNumbers, playStyle)
            val chartIds = charts.map { it.id }.toLongArray()
            val results = ladderResults.getResultsById(chartIds).toMutableList()
            if (results.isEmpty()) {
                null // return
            } else {
                val resultIds = results.map { it.chart.target.id }.toSortedSet()
                val notFound = getOrCreateResultsForCharts(charts.filterNot { resultIds.contains(it.id) })
                results.addAll(notFound)
                goal.getGoalProgress(charts.size, results) // return
            }
        }
        is TrialGoal -> {
            val trials = trialManager.bestTrials().filter {
                if (goal.restrictDifficulty) {
                    it.goalRankId == goal.rank.stableId.toInt()
                } else {
                    it.goalRankId >= goal.rank.stableId
                }
            }
            LadderGoalProgress(trials.size, goal.count) // return
        }
        is MFCPointsGoal -> {
            goal.getGoalProgress(goal.points, ladderResults.getMFCs())
        }
        is SongSetClearGoal -> when {
            goal.songs != null -> {
                val songs = goal.songs.mapNotNull { songRepo.getSongByName(it) }
                val charts = goal.difficulties.map { diff ->
                    songs.mapNotNull { song -> song.charts.firstOrNull {
                        it.difficultyClass == diff && it.playStyle == playStyle
                    } }
                }.flatten()
                if (goal.score != null) { // clear chart with target score
                    if (charts.size == 1) { // single chart, show the score
                        val currentScore = charts[0].plays.maxBy { it.score }?.score
                        currentScore?.let { curr -> LadderGoalProgress(curr, goal.score, showMax = false) }
                    } else { // multiple charts, show songs satisfied
                        val doneCount = charts.count { chart ->
                            (chart.plays.maxBy { it.score }?.score ?: 0) > goal.score
                        }
                        LadderGoalProgress(doneCount, charts.size)
                    }
                } else { // simply clear chart
                    val doneCount = charts.count {
                        it.plays.maxBy { play -> play.clearType.stableId }?.clearType?.passing ?: false
                    }
                    LadderGoalProgress(doneCount, charts.size)
                }
            }
            else -> null
        }
        else -> null
    }

    private val shouldShowImportTutorial get() = !settingsManager.getUserFlag(KEY_IMPORT_SKIP_DIRECTIONS, false)

    fun showImportFlow(activity: FragmentActivity) {
        if (shouldShowImportTutorial) {
            showImportDirectionsDialog(activity)
        } else {
            showImportEntryDialog(activity)
        }
    }

    private fun showImportDirectionsDialog(activity: FragmentActivity) {
        ScoreManagerImportDirectionsDialog(object: ScoreManagerImportDirectionsDialog.Listener {
            override fun onDialogCancelled() = Unit
            override fun onCopyAndContinue() {
                Toast.makeText(activity, context.getString(R.string.copied), Toast.LENGTH_SHORT).show()
                (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip =
                    ClipData.newPlainText("LIFE4 Data", context.getString(R.string.import_data_format))
                showImportEntryDialog(activity)
            }
        }).show(activity.supportFragmentManager, ScoreManagerImportDirectionsDialog.TAG)
    }

    private fun showImportEntryDialog(activity: FragmentActivity) {
        val intent = activity.packageManager.getLaunchIntentForPackage("jp.linanfine.dsma")
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)//null pointer check in case package name was not found
        } else {
            Toast.makeText(activity, context.getString(R.string.no_ddra_manager), Toast.LENGTH_SHORT).show()
        }

        ScoreManagerImportEntryDialog(object : ScoreManagerImportEntryDialog.Listener {
            override fun onDialogCancelled() = Unit
            override fun onHelpPressed() = showImportDirectionsDialog(activity)
            override fun onDataSubmitted(data: String) = showImportProcessingDialog(activity, data)
        }).show(activity.supportFragmentManager, ScoreManagerImportEntryDialog.TAG)
    }

    private fun showImportProcessingDialog(activity: FragmentActivity, dataString: String) {
        val dialog = ScoreManagerImportProcessingDialog(object : ScoreManagerImportProcessingDialog.Listener {
            override fun onDialogLoaded(managerListener: ManagerImportListener) = importManagerData(dataString, managerListener)
            override fun onDialogCancelled() = cancelImportJob()
        })
        dialog.show(activity.supportFragmentManager, ScoreManagerImportEntryDialog.TAG)
    }

    fun importManagerData(dataString: String, listener: ManagerImportListener? = null) {
        var success = 0
        var errors = 0
        importJob = CoroutineScope(Dispatchers.IO).launch {
            val lines = dataString.lines()
            lines.forEach { entry ->
                val entryParts = entry.trim().split(';')
                if (entryParts.size >= 4) {
                    // format = %p:b:B:D:E:C%%y:SP:DP%;%d%;%s0%;%l%;%f:mfc:pfc:gfc:fc:life4:clear%;%e%;%a%;%t%
                    try {
                        val chartType = entryParts[0] // ESP
                        val difficultyNumber = entryParts[1].toInt()
                        val score = entryParts[2].toInt()
                        // need 5 and 6 first
                        val clears = entryParts[5].toIntOrNull() ?: 0

                        var clear = ClearType.parse(entryParts[4])!!
                        if (clear == ClearType.CLEAR) {
                            when {
                                entryParts[3] == "-" -> clear = ClearType.NO_PLAY
                                entryParts[3] == "E" -> clear = when {
                                    clears > 0 -> ClearType.CLEAR
                                    else -> ClearType.FAIL
                                }
                            }
                        }

                        val songName = entryParts.subList(entryParts.size - 1, entryParts.size).joinToString(";")

                        val playStyle = PlayStyle.parse(chartType)!!
                        val difficultyClass = DifficultyClass.parse(chartType)!!

                        val songDB = songRepo.getSongByName(songName) ?: throw SongNotFoundException(songName)
                        val chartDB = songDataManager.updateOrCreateChartForSong(songDB, playStyle, difficultyClass, difficultyNumber)
                        val resultDB = updateOrCreateResultForChart(chartDB, score, clear)

                        if (BuildConfig.DEBUG && resultDB.clearType == ClearType.NO_PLAY) {
                            Log.v("import", "${songDB.title} - ${chartDB.difficultyClass} (${chartDB.difficultyNumber})")
                        }
                        success++
                        if (success % 2 == 0) {
                            withContext(Dispatchers.Main) { listener?.onCountUpdated(success + errors, lines.size - 1) }
                        }
                    } catch (e: Exception) {
                        errors++
                        Log.e("Exception", e.message ?: "")
                        withContext(Dispatchers.Main) { listener?.onError(errors, "${entry}\n${e.message}") }
                    }
                } else if (entry.isNotEmpty()) {
                    errors++
                }
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(context, context.getString(R.string.import_finished, success, errors), Toast.LENGTH_SHORT).show()
                ignoreListManager.invalidateIgnoredIds()
                Life4Application.eventBus.post(SongResultsImportCompletedEvent())
                if (success > 0) {
                    Life4Application.eventBus.post(SongResultsUpdatedEvent())
                }
                importJob = null
                listener?.onCompleted()
            }
        }
    }

    fun cancelImportJob() {
        importJob?.cancel()
        importJob = null
    }

    fun clearGoalStates(c: Context) {
        AlertDialog.Builder(c)
            .setTitle(R.string.are_you_sure)
            .setMessage(R.string.confirm_erase_trial_data)
            .setPositiveButton(R.string.yes) { _, _ ->
                goalsBox.removeAll()
                ladderResults.clearRepo()
                Life4Application.eventBus.post(LadderRankUpdatedEvent())
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    fun clearSongResults(c: Context) {
        AlertDialog.Builder(c)
            .setTitle(R.string.are_you_sure)
            .setMessage(R.string.confirm_erase_result_data)
            .setPositiveButton(R.string.yes) { _, _ ->
                ladderResults.clearRepo()
                Life4Application.eventBus.post(SongResultsUpdatedEvent())
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    fun refreshSongDatabase(c: Context) {
        AlertDialog.Builder(c)
            .setTitle(R.string.are_you_sure)
            .setMessage(R.string.confirm_refresh_song_db)
            .setPositiveButton(R.string.yes) { _, _ ->
                ladderResults.clearRepo()
                songDataManager.initializeSongDatabase()
                Life4Application.eventBus.post(SongResultsUpdatedEvent())
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun getOrCreateResultsForCharts(charts: List<ChartDB>): List<LadderResultDB> {
        return charts.map { chart ->
            chart.plays.firstOrNull() ?: LadderResultDB().also { result ->
                chart.plays.add(result)
                songDataManager.updateChart(chart)
                ladderResults.addResult(result)
            }
        }
    }

    private fun updateOrCreateResultForChart(chart: ChartDB, score: Int, clear: ClearType): LadderResultDB {
        val result = chart.plays.firstOrNull()
        result?.let {
            if (it.score != score || it.clearType != clear) {
                it.score = score
                it.clearType = clear
                ladderResults.addResult(it)
            }
        }
        return result ?: LadderResultDB(score, clear).also {
            chart.plays.add(it)
            songDataManager.updateChart(chart)
            ladderResults.addResult(it)
        }
    }

    /**
     * Listener class for the manager import process
     */
    interface ManagerImportListener {
        fun onCountUpdated(current: Int, total: Int)
        fun onError(totalCount: Int, message: String)
        fun onCompleted()
    }

    companion object {
        const val RANKS_FILE_NAME = "ranks_v2.json"
    }
}