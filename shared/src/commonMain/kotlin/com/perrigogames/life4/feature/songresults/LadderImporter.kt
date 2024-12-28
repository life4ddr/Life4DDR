package com.perrigogames.life4.feature.songresults

import co.touchlab.kermit.Logger
import com.perrigogames.life4.AppInfo
import com.perrigogames.life4.LadderDialogs
import com.perrigogames.life4.db.ResultDatabaseHelper
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.DifficultyClass.BEGINNER
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.enums.PlayStyle.DOUBLE
import com.perrigogames.life4.feature.songlist.SongDataManager
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.model.BaseModel
import com.perrigogames.life4.model.MainScope
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

    private val appInfo: AppInfo by inject()
    private val saLogger: Logger by injectLogger("SAImport")
    private val legacyLogger: Logger by injectLogger("Import")
    private val songDataManager: SongDataManager by inject()
    private val resultDbHelper: ResultDatabaseHelper by inject()
    private val songResultsManager: SongResultsManager by inject()
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
                OpMode.SA -> parseManagerLine(it)
            } }
            withContext(Dispatchers.Main) {
                ladderDialogs.showImportFinishedToast()
//                ignoreListManager.invalidateIgnoredIds() FIXME
                // FIXME eventBus.post(SongResultsImportCompletedEvent())
                songResultsManager.refresh()
                if (success > 0) {
                    // FIXME eventBus.post(SongResultsUpdatedEvent())
                }
                importJob = null
                songResultsManager.refresh()
                listener?.onCompleted()
            }
        }
    }

    private suspend fun autoParseManagerLine(entry: String) {
        val chunk = entry.substring(0, 6)
        when {
            chunk.count { it == '\t' } >= 1 -> parseManagerLine(entry)
            else -> signalError("Unable to determine input mode")
        }
    }

    private suspend fun parseManagerLine(entry: String) {
        val entryParts = entry.trim().split('\t').toMutableList()
        if (entryParts.size == 39) {
            /*val id =*/ entryParts.removeAt(0)
            val entries = PlayStyle.entries.flatMap { playStyle ->
                DifficultyClass.entries.mapNotNull { difficulty ->
                    if (playStyle == DOUBLE && difficulty == BEGINNER) { null }
                    else {
                        val grade = entryParts.removeAt(0)
                        val score = entryParts.removeAt(0).toLong()
                        val fullCombo = entryParts.removeAt(0)
                        /*val combo =*/ entryParts.removeAt(0).toInt()
                        if (grade == "NoPlay") {
                            null
                        } else {
                            SASongEntry(
                                skillId = "",
                                playStyle = playStyle,
                                difficultyClass = difficulty,
                                score = score,
                                clearType = ClearType.parseSA(grade, fullCombo),
                            )
                        }
                    }
                }
            }

            val skillId = entryParts.removeAt(0)
            val songName = entryParts.removeAt(0).replace('+', ' ')

            val (song, charts) = songDataManager.libraryFlow.value.songs.entries
                .firstOrNull { it.key.skillId == skillId }
                ?: run{
                    signalError("Song \"$songName\" ($skillId) not found in the database")
                    return
                }
            entries.forEach { it.skillId = skillId }

            if (appInfo.isDebug) {
                saLogger.v("${song.title} ($skillId) - ${entries.size} found, ${entries.joinToString { it.score.toString() }}")
            }
            resultDbHelper.insertSAResults(entries)
            success++
            signalUpdate()
        } else if (entry.isNotEmpty()) {
            signalError("Entry is too short or too long")
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
        var skillId: String,
        val playStyle: PlayStyle,
        val difficultyClass: DifficultyClass,
        val score: Long,
        val clearType: ClearType,
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

    enum class OpMode { SA, AUTO }
}