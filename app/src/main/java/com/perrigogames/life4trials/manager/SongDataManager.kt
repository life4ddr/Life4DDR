package com.perrigogames.life4trials.manager

import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.crashlytics.android.Crashlytics
import com.perrigogames.life4.data.DifficultyClass
import com.perrigogames.life4.data.GameVersion
import com.perrigogames.life4.data.PlayStyle
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_IMPORT_GAME_VERSION
import com.perrigogames.life4.ktor.GithubDataAPI
import com.perrigogames.life4trials.api.LocalRemoteData
import com.perrigogames.life4trials.db.ChartDB
import com.perrigogames.life4trials.db.ChartDB_
import com.perrigogames.life4trials.db.SongDB
import com.perrigogames.life4trials.db.SongDB_
import com.perrigogames.life4trials.event.MajorUpdateProcessEvent
import com.perrigogames.life4trials.manager.SettingsManager.Companion.KEY_SONG_LIST_VERSION
import com.perrigogames.life4trials.repo.SongRepo
import com.perrigogames.life4trials.util.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Response


/**
 * A Manager class that keeps track of the available songs
 */
class SongDataManager(private val context: Context,
                      private val githubDataAPI: GithubDataAPI,
                      private val songRepo: SongRepo,
                      private val settingsManager: SettingsManager,
                      private val ignoreListManager: IgnoreListManager): BaseManager() {

    private val songList = object: LocalRemoteData<String>(context, R.raw.songs, SONGS_FILE_NAME) {
        override fun createLocalDataFromText(text: String) = text
        override fun createTextToData(data: String) = data
        override fun getDataVersion(data: String) = data.substring(0, data.indexOfOrEnd('\n')).trim().toInt()
        override suspend fun getRemoteResponse(): Response<String> = githubDataAPI.getSongList()
        override fun onFetchUpdated(data: String) {
            super.onFetchUpdated(data)
            refreshSongDatabase(data)
        }
    }

    init {
        Life4Application.eventBus.register(this)
        songList.start()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMajorVersion(e: MajorUpdateProcessEvent) {
        if (e.version == MajorUpdate.SONG_DB) {
            initializeSongDatabase()
        } else if (e.version == MajorUpdate.DOUBLES_FIX) {
            chartBox.remove(getChartsByPlayStyle(PlayStyle.DOUBLE))
            refreshSongDatabase(force = true)
        }
    }

    //
    // Song List Management
    //
    fun initializeSongDatabase() {
        chartBox.removeAll()
        songRepo.clear()
        refreshSongDatabase(force = true)
    }

    private fun refreshSongDatabase(input: String = songList.data, force: Boolean = false) {
        val lines = input.lines()
        if (force || settingsManager.getUserInt(KEY_SONG_LIST_VERSION, -1) < lines[0].toInt()) {
            val songContents = songRepo.getSongs()

            val dbSongs = mutableListOf<SongDB>()
            val dbCharts = mutableListOf<ChartDB>()
            lines.forEachIndexed { idx, line ->
                if (idx == 0 || line.isEmpty()) {
                    return@forEachIndexed
                }
                val data = line.split(";")
                val id = data[0].toLong()
                val title = data[1]
                var preview = false
                val mix = GameVersion.parse(data[2].let {
                    it.toLongOrNull() ?: it.substring(0, it.length - 1).let { seg ->
                        preview = true
                        seg.toLong()
                    }
                })
                val existingSong: SongDB? = songContents.firstOrNull { it.id == id }
                existingSong?.let {
                    if (it.title != title || it.artist != null || it.version != mix || it.preview != preview) {
                        it.title = title
                        it.artist = null
                        it.version = mix
                        it.preview = preview
                        songRepo.put(it)
                    }
                }
                val song = existingSong ?: SongDB(title, null, mix, preview).also { song ->
                    songRepo.attach(song)
                }
                PlayStyle.values().forEachIndexed { sIdx, style ->
                    DifficultyClass.values().forEachIndexed { dIdx, diff ->
                        val diffStr = data[3 + ((sIdx * DifficultyClass.values().size) + dIdx)]
                        if (diffStr.isNotEmpty()) {
                            updateOrCreateChartForSong(song, style, diff, diffStr.toInt(), false).also { chart ->
                                dbCharts.add(chart)
                                song.charts.add(chart)
                            }
                        }
                    }
                }
                dbSongs.add(song)
            }
            chartBox.put(dbCharts)
            songRepo.put(dbSongs)
            ignoreListManager.invalidateIgnoredIds()
            settingsManager.setUserInt(KEY_SONG_LIST_VERSION, lines[0].toInt())
        }
    }

    fun onA20RequiredUpdate(context: Context) {
        ignoreListManager.invalidateIgnoredIds()
        settingsManager.setUserString(KEY_IMPORT_GAME_VERSION, DEFAULT_IGNORE_VERSION)
        Handler().postDelayed({
            AlertDialog.Builder(context)
                .setTitle(R.string.a20_update)
                .setMessage(R.string.a20_update_explanation)
                .setNegativeButton(R.string.more_info) { d, _ ->
                    context.openWebUrlFromRes(R.string.url_a20_update)
                    d.dismiss()
                }
                .setPositiveButton(R.string.okay) { d, _ -> d.dismiss() }
                .setCancelable(true)
                .create()
                .show()
        }, 10)
    }

    //
    // ObjectBoxes
    //
    private val chartBox get() = objectBox.boxFor(ChartDB::class.java)

    //
    // Queries
    //
    private val chartPlayStyleQuery = chartBox.query()
        .equal(ChartDB_.playStyle, 0).parameterAlias("play_style")
        .build()
    private val chartDifficultyQuery = chartBox.query().apply {
        equal(ChartDB_.difficultyNumber, 0).parameterAlias("difficulty")
        equal(ChartDB_.playStyle, 0).parameterAlias("play_style")
    }.build()
    private val filteredChartDifficultyQuery = chartBox.query().apply {
        equal(ChartDB_.difficultyNumber, 0).parameterAlias("difficulty")
        equal(ChartDB_.playStyle, 0).parameterAlias("play_style")
        link(ChartDB_.song).notIn(SongDB_.id, ignoreListManager.selectedIgnoreSongIds)
        notIn(ChartDB_.id, ignoreListManager.selectedIgnoreChartIds)
    }.build()

    fun getCurrentlyIgnoredSongs() = songRepo.getSongsById(ignoreListManager.selectedIgnoreSongIds)

    fun getChartById(id: Long): ChartDB? = chartBox.get(id)

    fun getChartsById(ids: LongArray): MutableList<ChartDB> = chartBox.get(ids)

    fun getChartsByPlayStyle(playStyle: PlayStyle): MutableList<ChartDB> =
        chartPlayStyleQuery.setParameter("play_style", playStyle.stableId)
            .find()

    fun getCurrentlyIgnoredCharts() = getChartsById(ignoreListManager.selectedIgnoreChartIds)

    fun getChartsByDifficulty(difficulty: Int, playStyle: PlayStyle): MutableList<ChartDB> =
        chartDifficultyQuery.setParameter("difficulty", difficulty.toLong())
            .setParameter("play_style", playStyle.stableId)
            .find()

    fun getFilteredChartsByDifficulty(difficulty: Int, playStyle: PlayStyle): MutableList<ChartDB> =
        filteredChartDifficultyQuery.setParameter("difficulty", difficulty.toLong())
            .setParameter("play_style", playStyle.stableId)
            .find()

    fun getChartsByDifficulty(difficultyList: IntArray, playStyle: PlayStyle): MutableList<ChartDB> = mutableListOf<ChartDB>().apply {
        difficultyList.forEach {
            addAll(chartDifficultyQuery
                .setParameter("difficulty", it.toLong())
                .setParameter("play_style", playStyle.stableId)
                .find())
        }
    }

    fun getFilteredChartsByDifficulty(difficultyList: IntArray, playStyle: PlayStyle): MutableList<ChartDB> = mutableListOf<ChartDB>().apply {
        difficultyList.forEach {
            addAll(filteredChartDifficultyQuery
                .setParameter("difficulty", it.toLong())
                .setParameter("play_style", playStyle.stableId)
                .find())
        }
    }

    /**
     * Checks to see if a chart is present and correct as it should be.
     * Throws an exception on an invalid chart, so this should always be surrounded with a try/catch
     */
    @Throws(ChartNotFoundException::class, UnexpectedDifficultyNumberException::class)
    fun validateChartDifficulty(song: SongDB,
                                playStyle: PlayStyle,
                                difficultyClass: DifficultyClass,
                                difficultyNumber: Int) {
        val chart = song.getChart(playStyle, difficultyClass)
        when {
            chart == null ->
                throw ChartNotFoundException(song, playStyle, difficultyClass, difficultyNumber)
            chart.difficultyNumber != difficultyNumber ->
                throw UnexpectedDifficultyNumberException(chart, difficultyNumber)
        }
    }

    fun updateOrCreateChartForSong(song: SongDB,
                                   playStyle: PlayStyle,
                                   difficultyClass: DifficultyClass,
                                   difficultyNumber: Int,
                                   commit: Boolean = true): ChartDB {
        val chart = song.getChart(playStyle, difficultyClass)
        chart?.let {
            if (it.difficultyNumber != difficultyNumber) {
                if (!BuildConfig.DEBUG) {
                    Crashlytics.logException(UnexpectedDifficultyNumberException(it, difficultyNumber))
                }
                it.difficultyNumber = difficultyNumber
                if (commit) {
                    chartBox.put(it)
                }
            }
        }
        return chart ?: ChartDB(difficultyClass, difficultyNumber, playStyle).also {
            song.charts.add(it)
            if (commit) {
                chartBox.put(it)
                songRepo.put(song)
            }
        }
    }

    fun updateChart(chart: ChartDB) = chartBox.put(chart)

    fun dumpData() {
        val songStrings = songRepo.getSongs().map { song ->
            val builder = StringBuilder("${song.title};")
            val chartsCopy = song.charts.toMutableList()
            PlayStyle.values().forEach { style ->
                DifficultyClass.values().forEach { diff ->
                    val chart = chartsCopy.firstOrNull { it.playStyle == style && it.difficultyClass == diff }
                    if (chart != null) {
                        chartsCopy.remove(chart)
                        builder.append("${chart.difficultyNumber};")
                    } else {
                        builder.append(";")
                    }
                }
            }
            builder.toString()
        }.toMutableList()
        with(StringBuilder()) {
            while (songStrings.isNotEmpty()) {
                repeat((0..10).count()) {
                    if (songStrings.isNotEmpty()) {
                        append("${songStrings.removeAt(0)}[][]")
                    }
                }
                Log.v("SongDataManager", this.toString())
                setLength(0)
            }
        }
    }

    companion object {
        const val SONGS_FILE_NAME = "songs.csv"
        const val IGNORES_FILE_NAME = "ignore_lists_v2.json"
        const val DEFAULT_IGNORE_VERSION = "A20_US"
    }
}

class UnexpectedDifficultyNumberException(chart: ChartDB, newDiff: Int): Exception(
    "${chart.song.target.title} (${chart.styleDifficultyString} ${chart.difficultyNumber}) changed: ${chart.difficultyNumber} -> $newDiff")

class SongNotFoundException(name: String): Exception("$name does not exist in the song database")

class ChartNotFoundException(song: SongDB, playStyle: PlayStyle, difficultyClass: DifficultyClass, difficultyNumber: Int): Exception(
    "${song.title} (${playStyle.aggregateString(difficultyClass)} $difficultyNumber) does not exist in the song database")
