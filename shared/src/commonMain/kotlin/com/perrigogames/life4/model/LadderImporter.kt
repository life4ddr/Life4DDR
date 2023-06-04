package com.perrigogames.life4.model

import co.touchlab.kermit.Logger
import com.perrigogames.life4.LadderDialogs
import com.perrigogames.life4.db.ResultDatabaseHelper
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.DifficultyClass.BEGINNER
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.enums.PlayStyle.DOUBLE
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.isDebug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject

/**
 * Model class to facilitate importing ladder data from an external source. Data is provided as a List<String> with each
 * line corresponding to either a single chart or a full song, depending on operation mode.
 *
 * This importer can function in two modes: SA and Legacy mode.
 *
 * In SA mode, input lines are interpreted as Score Attack import lines, where each line holds scores for an entire song.
 * In Legacy mode, input lines are interpreted using the old copy-paste style data lines, where each line was only a single
 *  chart, which led to a lot of duplication.
 */
class LadderImporter(
    private var dataLines: List<String>,
    private val opMode: OpMode = OpMode.AUTO,
): BaseModel() {

    private val saLogger: Logger by injectLogger("SAImport")
    private val legacyLogger: Logger by injectLogger("Import")
    private val songDataManager: SongDataManager by inject()
    private val resultDbHelper: ResultDatabaseHelper by inject()
    private val ladderProgressManager: LadderProgressManager by inject()
    private val ladderDialogs: LadderDialogs by inject()

    private var success = 0
    private var errors = 0
    private var importJob: Job? = null
    private var listener: Listener? = null

    fun start(listener: Listener? = null) {
        success = 0
        errors = 0
        dataLines = dataLines.filterNot { it.isEmpty() }
        this.listener = listener
        importJob = MainScope(Dispatchers.Unconfined).launch {
            dataLines.forEach { when (opMode) {
                OpMode.AUTO -> autoParseManagerLine(it)
                OpMode.LEGACY -> parseLegacyManagerLine(it)
                OpMode.SA -> parseManagerLine(it)
            } }
            withContext(Dispatchers.Main) {
                ladderDialogs.showImportFinishedToast()
//                ignoreListManager.invalidateIgnoredIds() FIXME
                // FIXME eventBus.post(SongResultsImportCompletedEvent())
                if (success > 0) {
                    // FIXME eventBus.post(SongResultsUpdatedEvent())
                }
                importJob = null
                ladderProgressManager.refresh()
                listener?.onCompleted()
            }
        }
    }

    private suspend fun autoParseManagerLine(entry: String) {
        val chunk = entry.substring(0, 6)
        when {
            chunk.count { it == ';' } >= 1 -> parseLegacyManagerLine(entry)
            chunk.count { it == '\t' } >= 1 -> parseManagerLine(entry)
            else -> signalError("Unable to determine input mode")
        }
    }

    private suspend fun parseManagerLine(entry: String) {
        val entryParts = entry.trim().split('\t').toMutableList()
        if (entryParts.size == 39) {
            /*val id =*/ entryParts.removeAt(0)
            val entries = PlayStyle.values().flatMap { playStyle ->
                DifficultyClass.values().mapNotNull { difficulty ->
                    if (playStyle == DOUBLE && difficulty == BEGINNER) { null }
                    else {
                        val grade = entryParts.removeAt(0)
                        val score = entryParts.removeAt(0).toLong()
                        val fullCombo = entryParts.removeAt(0)
                        /*val combo =*/ entryParts.removeAt(0).toInt()
                        if (grade == "NoPlay") {
                            null
                        } else {
                            SASongEntry(null, score, ClearType.parseSA(grade, fullCombo), playStyle, difficulty)
                        }
                    }
                }
            }

            val skillId = entryParts.removeAt(0)
            val songName = entryParts.removeAt(0).replace('+', ' ')

            val charts = songDataManager.songs.firstOrNull { it.skillId == skillId }
                ?.let { song ->
                    songDataManager.chartsGroupedBySong[song]
                }
            if (charts != null) {
                entries.forEach {
                    it.chartId = charts.first { chart ->
                        chart.playStyle == it.playStyle &&
                                chart.difficultyClass == it.difficultyClass
                    }.id
                }
                val hasSong = songDataManager.songs.firstOrNull { it.skillId == skillId } != null
                if (isDebug) {
                    saLogger.v("$songName ($skillId) - ${entries.size} found, ${entries.joinToString { it.score.toString() }}, dbExist=$hasSong")
                }
                if (hasSong) {
                    resultDbHelper.insertSAResults(entries)
                    success++
                    signalUpdate()
                } else {
                    signalError("Song \"$songName\" ($skillId) not found in the database")
                }
            } else {
                signalError("Song \"$songName\" ($skillId) not found in the database")
            }
        } else if (entry.isNotEmpty()) {
            signalError("Entry is too short or too long")
        }
    }

    private suspend fun parseLegacyManagerLine(entry: String) {
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

                val detailedChart = songDataManager.detailedCharts.firstOrNull {
                    it.title == songName && it.playStyle == playStyle && it.difficultyClass == difficultyClass
                } ?: throw ChartNotFoundException(songName, playStyle, difficultyClass, difficultyNumber)
                resultDbHelper.insertResult(detailedChart, clear, score)

                if (isDebug && clear == ClearType.NO_PLAY) {
                    legacyLogger.v("${detailedChart.title} - ${detailedChart.difficultyClass} (${detailedChart.difficultyNumber})")
                }
                success++
                if (success % 2 == 0) {
                    signalUpdate()
                }
            } catch (e: Exception) {
                legacyLogger.e(e.message ?: "")
                signalError("${entry}\n${e.message}")
            }
        } else if (entry.isNotEmpty()) {
            signalError("Entry is too short")
        }
    }

    private suspend fun signalUpdate(
        current: Int = success + errors,
        total: Int = dataLines.size - 1) {
        withContext(Dispatchers.Main) { listener?.onCountUpdated(current, total) }
    }

    private suspend fun signalError(message: String) {
        errors++
        withContext(Dispatchers.Main) { listener?.onError(errors, message) }
    }

    class SASongEntry(
        var chartId: Long?,
        val score: Long,
        val clearType: ClearType,
        val playStyle: PlayStyle,
        val difficultyClass: DifficultyClass,
    )

    fun cancel() {
        importJob?.cancel()
        importJob = null
    }

    /**
     * Listener class for the manager import process
     */
    interface Listener {
        fun onCountUpdated(current: Int, total: Int)
        fun onError(totalCount: Int, message: String)
        fun onCompleted()
    }

    enum class OpMode { LEGACY, SA, AUTO }
}