package com.perrigogames.life4trials.manager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_IMPORT_SKIP_DIRECTIONS
import com.perrigogames.life4trials.api.GithubDataAPI
import com.perrigogames.life4trials.data.*
import com.perrigogames.life4trials.data.DifficultyClass.*
import com.perrigogames.life4trials.db.*
import com.perrigogames.life4trials.event.*
import com.perrigogames.life4trials.ui.managerimport.ScoreManagerImportDirectionsDialog
import com.perrigogames.life4trials.ui.managerimport.ScoreManagerImportEntryDialog
import com.perrigogames.life4trials.util.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.Subscribe
import retrofit2.Response
import java.net.UnknownHostException
import java.util.*

class LadderManager(private val context: Context,
                    private val songDataManager: SongDataManager,
                    private val trialManager: TrialManager,
                    private val githubDataAPI: GithubDataAPI): BaseManager() {

    //
    // Ladder Data
    //
    var ladderData: LadderRankData
        private set
    private var ladderJob: Job? = null

    //
    // ObjectBoxes
    //
    private val goalsBox get() = objectBox.boxFor(GoalStatusDB::class.java)
    private val ladderResultBox get() = objectBox.boxFor(LadderResultDB::class.java)

    //
    // Init
    //
    init {
        Life4Application.eventBus.register(this)
        val dataString = context.readFromFile(RANKS_FILE_NAME) ?: context.loadRawString(R.raw.ranks)
        ladderData = DataUtil.gson.fromJson(dataString, LadderRankData::class.java)!!
        fetchRemoteRanks()
    }

    private fun fetchRemoteRanks() {
        ladderJob?.cancel()
        ladderJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = githubDataAPI.getLadderRanks()
                withContext(Dispatchers.Main) {
                    if (response.check()) {
                        context.saveToFile(TrialManager.TRIALS_FILE_NAME, DataUtil.gson.toJson(response.body()))
                        Toast.makeText(context, R.string.ranks_updated, Toast.LENGTH_SHORT).show()
                        ladderData = response.body()!!
                        Life4Application.eventBus.post(LadderRanksReplacedEvent())
                    }
                    ladderJob = null
                }
            } catch (e: UnknownHostException) {}
        }
    }

    @Subscribe
    fun onMajorVersion(e: MajorUpdateProcessEvent) {
        if (e.version == MajorUpdate.SONG_DB) {
            if (!ladderResultBox.isEmpty) {
                ladderResultBox.removeAll()
                AlertDialog.Builder(context)
                    .setTitle(R.string.database_upgraded)
                    .setMessage(R.string.database_replaced_reimport)
                    .setPositiveButton(R.string.okay) { d, _ -> d.dismiss() }
                    .setCancelable(true)
                    .create()
                    .show()
            }
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
    private val ladderResultQuery = ladderResultBox.query()
        .`in`(LadderResultDB_.chartId, LongArray(0)).parameterAlias("ids")
        .build()
    private val mfcQuery = ladderResultBox.query()
        .equal(LadderResultDB_.clearType, ClearType.MARVELOUS_FULL_COMBO.stableId)
        .apply {
            link(LadderResultDB_.chart)
                .`in`(ChartDB_.difficultyClass, longArrayOf(DIFFICULT.stableId, EXPERT.stableId, CHALLENGE.stableId))
        }
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
    fun findRankEntry(rank: LadderRank?) = ladderData.rankRequirements.firstOrNull { it.rank == rank }

    fun previousEntry(rank: LadderRank?) = previousEntry(ladderData.rankRequirements.indexOfFirst { it.rank == rank })

    fun previousEntry(index: Int) = ladderData.rankRequirements.getOrNull(index - 1)

    fun nextEntry(rank: LadderRank?) = nextEntry(ladderData.rankRequirements.indexOfFirst { it.rank == rank })

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
    fun getGoalProgress(goal: BaseRankGoal, playStyle: PlayStyle): LadderGoalProgress? = when (goal) {
        is DifficultyClearGoal -> {
            val charts = songDataManager.getChartsByDifficulty(goal.difficultyNumbers, playStyle)
            val filtered = charts.filterNot {
                        songDataManager.selectedIgnoreChartIds.contains(it.id) ||
                        songDataManager.selectedIgnoreSongIds.contains(it.song.targetId) ||
                        goal.songExceptions?.contains(it.song.target.title) == true }
            val filteredIds = filtered.map { it.id }.toLongArray()
            val results = ladderResultQuery.setParameters("ids", filteredIds).find()
            if (results.isEmpty()) {
                null // return
            } else {
                val resultIds = results.map { it.chart.target.id }.toSortedSet()
                val notFound = getOrCreateResultsForCharts(filtered.filterNot { resultIds.contains(it.id) })
                results.addAll(notFound)
                goal.getGoalProgress(filtered.size, results) // return
            }
        }
        is TrialGoal -> {
            val trials = trialManager.bestTrials().filter { it.goalRankId >= goal.rank.stableId }
            LadderGoalProgress(trials.size, goal.count) // return
        }
        is MFCPointsGoal -> {
            goal.getGoalProgress(goal.points, mfcQuery.find())
        }
        is SongSetClearGoal -> when {
            goal.songs != null -> {
                val songs = goal.songs.mapNotNull { songDataManager.getSongByName(it) }
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

    val shouldShowImportTutorial get() = !SharedPrefsUtil.getUserFlag(context, KEY_IMPORT_SKIP_DIRECTIONS, false)

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
                (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).let {
                    it.primaryClip = ClipData.newPlainText("LIFE4 Data", context.getString(R.string.import_data_format))
                }
                showImportEntryDialog(activity)
            }
        }).show(activity.supportFragmentManager, ScoreManagerImportDirectionsDialog.TAG)
    }

    private fun showImportEntryDialog(activity: FragmentActivity) {
        val launchIntent = activity.packageManager.getLaunchIntentForPackage("jp.linanfine.dsma")
        if (launchIntent != null) {
            activity.startActivity(launchIntent)//null pointer check in case package name was not found
        } else {
            Toast.makeText(activity, context.getString(R.string.no_ddra_manager), Toast.LENGTH_SHORT).show()
        }

        ScoreManagerImportEntryDialog(object : ScoreManagerImportEntryDialog.Listener {
            override fun onDialogCancelled() = Unit
            override fun onHelpPressed() = showImportDirectionsDialog(activity)
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
                            entryParts[3] == "E" -> clear = when {
                                clears > 0 -> ClearType.CLEAR
                                else -> ClearType.FAIL
                            }
                        }
                    }

                    val songName = entryParts.subList(entryParts.size - 1, entryParts.size).joinToString(";")

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
        Life4Application.eventBus.post(SongResultsImportCompletedEvent(success, errors))
        if (success > 0) {
            Life4Application.eventBus.post(SongResultsUpdatedEvent())
        }
    }

    fun clearGoalStates(c: Context) {
        AlertDialog.Builder(c)
            .setTitle(R.string.are_you_sure)
            .setMessage(R.string.confirm_erase_trial_data)
            .setPositiveButton(R.string.yes) { _, _ ->
                goalsBox.removeAll()
                ladderResultBox.removeAll()
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
                ladderResultBox.removeAll()
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
                ladderResultBox.put(result)
            }
        }
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

    private fun Response<LadderRankData>.check(): Boolean = when {
        !isSuccessful -> {
            Toast.makeText(context, errorBody()!!.string(), Toast.LENGTH_SHORT).show()
            false
        }
        body()!!.version <= ladderData.version -> false
        else -> true
    }

    companion object {
        const val RANKS_FILE_NAME = "ranks.json"
    }
}