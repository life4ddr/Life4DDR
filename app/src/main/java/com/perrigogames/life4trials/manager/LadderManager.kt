package com.perrigogames.life4trials.manager

import android.util.Log
import com.perrigogames.life4.*
import com.perrigogames.life4.SettingsKeys.KEY_INFO_RANK
import com.perrigogames.life4.SettingsKeys.KEY_INFO_TARGET_RANK
import com.perrigogames.life4.api.FetchListener
import com.perrigogames.life4.api.LadderRemoteData
import com.perrigogames.life4.api.LocalDataReader
import com.perrigogames.life4.data.BaseRankGoal
import com.perrigogames.life4.data.LadderRank
import com.perrigogames.life4.data.LadderRankData
import com.perrigogames.life4.data.LadderVersion
import com.perrigogames.life4.db.GoalDatabaseHelper
import com.perrigogames.life4.db.GoalState
import com.perrigogames.life4.db.nowString
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.GoalStatus
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.RANKS_FILE_NAME
import com.perrigogames.life4.model.BaseModel
import com.perrigogames.life4.model.EventBusNotifier
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.db.*
import com.perrigogames.life4trials.repo.LadderResultRepo
import com.perrigogames.life4trials.repo.SongRepo
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.*
import org.koin.core.inject
import org.koin.core.qualifier.named

class LadderManager: BaseModel() {

    private val songRepo: SongRepo by inject()
    private val ladderResults: LadderResultRepo by inject()
    private val ignoreListManager: IgnoreListManager by inject()
    private val songDataManager: SongDataManager by inject()
    private val settings: Settings by inject()
    private val eventBus: EventBusNotifier by inject()
    private val goalDBHelper: GoalDatabaseHelper by inject()
    private val ladderDialogs: LadderDialogs by inject()
    private val dataReader: LocalDataReader by inject(named(RANKS_FILE_NAME))

    //
    // Ladder Data
    //
    private val ladderDataRemote = LadderRemoteData(dataReader, object: FetchListener<LadderRankData> {
        override fun onFetchUpdated(data: LadderRankData) {
            ladderDialogs.showLadderUpdateToast()
            eventBus.post(LadderRanksReplacedEvent())
        }
    })
    val ladderData: LadderRankData get() = ladderDataRemote.data
    val currentRequirements: LadderVersion
        get() = ignoreListManager.selectedIgnoreList!!.baseVersion.let { version ->
            ladderData.gameVersions[version] ?: error("Rank requirements not found for version $version")
        }

    init {
        ladderDataRemote.start()
    }

    //
    // Local User Rank
    //
    fun getUserRank(): LadderRank? =
        LadderRank.parse(settings.getStringOrNull(KEY_INFO_RANK)?.toLongOrNull())

    fun getUserGoalRank(): LadderRank? =
        settings.getStringOrNull(KEY_INFO_TARGET_RANK)?.toLongOrNull()?.let { LadderRank.parse(it) }
            ?: getUserRank()?.let { return LadderRank.values().getOrNull(it.ordinal + 1) }
            ?: LadderRank.WOOD1

    fun setUserRank(rank: LadderRank?) {
        settings[KEY_INFO_RANK] = rank?.stableId.toString()
        settings[KEY_INFO_TARGET_RANK] = ""
        eventBus.post(LadderRankUpdatedEvent())
    }

    fun setUserTargetRank(rank: LadderRank?) {
        settings[KEY_INFO_TARGET_RANK] = rank?.stableId.toString()
        eventBus.post(LadderRankUpdatedEvent())
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
    fun getGoalState(goal: BaseRankGoal): GoalState? =
        goalDBHelper.stateForId(goal.id.toLong())

    fun getOrCreateGoalState(goal: BaseRankGoal): GoalState =
        getGoalState(goal) ?: GoalState.Impl(goal.id.toLong(), GoalStatus.INCOMPLETE, nowString)

    fun getGoalStateList(goals: List<BaseRankGoal>): List<GoalState> =
        goalDBHelper.statesForIdList(goals.map { it.id.toLong() }).executeAsList()

    fun setGoalState(id: Long, status: GoalStatus) {
        mainScope.launch {
            goalDBHelper.updateGoalState(id, status)
        }
    }

    //
    // Imported Score Data
    //
    private var importJob: Job? = null

    //FIXME progress
//    fun getGoalProgress(goal: BaseRankGoal, playStyle: PlayStyle): LadderGoalProgress? = when (goal) {
//        is DifficultyClearGoal -> {
//            val charts = if (goal.count == null) {
//                songDataManager.getFilteredChartsByDifficulty(goal.difficultyNumbers, playStyle).filterNot {
//                    ignoreListManager.selectedIgnoreChartIds.contains(it.id) ||
//                    ignoreListManager.selectedIgnoreSongIds.contains(it.song.targetId) ||
//                    goal.songExceptions?.contains(it.song.target.title) == true }
//            } else songDataManager.getChartsByDifficulty(goal.difficultyNumbers, playStyle)
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
//        is TrialGoal -> {
//            val trials = trialManager.bestTrials().filter {
//                if (goal.restrictDifficulty) {
//                    it.goalRankId == goal.rank.stableId.toInt()
//                } else {
//                    it.goalRankId >= goal.rank.stableId
//                }
//            }
//            LadderGoalProgress(trials.size, goal.count) // return
//        }
//        is MFCPointsGoal -> {
//            goal.getGoalProgress(goal.points, ladderResults.getMFCs())
//        }
//        is SongSetClearGoal -> when {
//            goal.songs != null -> {
//                val songs = goal.songs.mapNotNull { songRepo.getSongByName(it) }
//                val charts = goal.difficulties.map { diff ->
//                    songs.mapNotNull { song -> song.charts.firstOrNull {
//                        it.difficultyClass == diff && it.playStyle == playStyle
//                    } }
//                }.flatten()
//                if (goal.score != null) { // clear chart with target score
//                    if (charts.size == 1) { // single chart, show the score
//                        val currentScore = charts[0].plays.maxBy { it.score }?.score
//                        currentScore?.let { curr ->
//                            LadderGoalProgress(
//                                curr,
//                                goal.score,
//                                showMax = false
//                            )
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
//        else -> null
//    }

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
                ladderDialogs.showImportFinishedToast()
                ignoreListManager.invalidateIgnoredIds()
                eventBus.post(SongResultsImportCompletedEvent())
                if (success > 0) {
                    eventBus.post(SongResultsUpdatedEvent())
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

    fun clearGoalStates() {
        ladderDialogs.onClearGoalStates {
            mainScope.launch {
                goalDBHelper.deleteAll()
            }
            ladderResults.clearRepo()
            eventBus.post(LadderRankUpdatedEvent())
        }
    }

    fun clearSongResults() {
        ladderDialogs.onClearSongResults {
            ladderResults.clearRepo()
            eventBus.post(SongResultsUpdatedEvent())
        }
    }

    fun refreshSongDatabase() {
        ladderDialogs.onRefreshSongDatabase {
            ladderResults.clearRepo()
            songDataManager.initializeSongDatabase()
            eventBus.post(SongResultsUpdatedEvent())
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
}
