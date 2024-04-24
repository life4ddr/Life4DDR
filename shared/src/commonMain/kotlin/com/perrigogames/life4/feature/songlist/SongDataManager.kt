package com.perrigogames.life4.feature.songlist

import co.touchlab.kermit.Logger
import com.perrigogames.life4.SettingsKeys.KEY_SONG_LIST_VERSION
import com.perrigogames.life4.api.SongListRemoteData
import com.perrigogames.life4.api.base.LocalDataReader
import com.perrigogames.life4.api.base.unwrapLoaded
import com.perrigogames.life4.data.SongList
import com.perrigogames.life4.db.ChartInfo
import com.perrigogames.life4.db.ChartResult
import com.perrigogames.life4.db.DetailedChartInfo
import com.perrigogames.life4.db.SongInfo
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.GameVersion
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.feature.songresults.ChartResultPair
import com.perrigogames.life4.feature.songresults.matches
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.SONGS_FILE_NAME
import com.perrigogames.life4.logException
import com.perrigogames.life4.model.BaseModel
import com.perrigogames.life4.model.MajorUpdate
import com.perrigogames.life4.model.MajorUpdateManager
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.qualifier.named


/**
 * A Manager class that keeps track of the available songs
 */
class SongDataManager: BaseModel() {

    private val dbHelper: SongDatabaseHelper by inject()
    private val majorUpdates: MajorUpdateManager by inject()
    private val settings: Settings by inject()
    private val dataReader: LocalDataReader by inject(named(SONGS_FILE_NAME))
    private val logger: Logger by injectLogger("SongDataManager")

    private val remoteData = SongListRemoteData(dataReader)
//        override fun onDataLoaded(data: SongList) {
//            refreshMemoryData()
//        }
//
//        override fun onDataVersionChanged(data: SongList) {
//            refreshSongDatabase(data)
//        }

    val dataVersionString get() = remoteData.versionState.value.versionString

    lateinit var songs: List<SongInfo>
    @Deprecated("Use libraryFlow instead")
    lateinit var detailedCharts: List<DetailedChartInfo>
    lateinit var chartsGroupedBySong: Map<SongInfo, List<DetailedChartInfo>>
    lateinit var chartsGroupedByDifficulty: Map<PlayStyle, Map<Long, List<DetailedChartInfo>>>

    private val _libraryFlow = MutableStateFlow(SongLibrary())
    val libraryFlow: StateFlow<SongLibrary> = _libraryFlow

    private var callback: (() -> Unit)? = null

    internal fun start(callback: (() -> Unit)?) {
        this.callback = callback
        remoteData.start()
        if (majorUpdates.updates.contains(MajorUpdate.SONG_DB)) { // initial launch
            refreshSongDatabase(delete = true)
        } else {
            refreshMemoryData()
        }
    }

    fun findSong(id: Long): SongInfo? = songs.find { it.id == id }

    fun findSong(skillId: String): SongInfo? = songs.find { it.skillId == skillId }

    fun findSongByName(title: String): SongInfo? = songs.find { it.title == title }

    fun getChartGroup(
        playStyle: PlayStyle,
        difficultyNumber: Long,
        allowHigherDifficulty: Boolean = false,
    ): List<DetailedChartInfo> {
        return if (allowHigherDifficulty) {
            chartsGroupedByDifficulty[playStyle]!!
                .filterKeys { it >= difficultyNumber }
                .flatMap { (_, songs) -> songs }
        } else {
            chartsGroupedByDifficulty[playStyle]!![difficultyNumber]!!
        }
    }

    fun matchWithDetailedCharts(charts: List<ChartResult>): List<ChartResultPair> {
        return charts.map { result ->
            ChartResultPair(
                chart = detailedCharts.first { it.matches(result) },
                result = result,
            )
        }
    }

    //
    // Song List Management
    //
    internal fun refreshSongDatabase(
        input: SongList = remoteData.dataState.value.unwrapLoaded()!!,
        force: Boolean = false,
        delete: Boolean = false,
    ) {
        if (delete) {
            dbHelper.deleteAll()
        }
        mainScope.launch {
            var chartId = 1L
            try {
                val lines = input.songLines
                if (force || delete || settings.getInt(KEY_SONG_LIST_VERSION, -1) < input.version) {

                    val songs = mutableListOf<SongInfo>()
                    val charts = mutableListOf<ChartInfo>()

                    lines.forEach { line ->
                        if (line.isEmpty()) {
                            return@forEach
                        }
                        val data = line.split('\t')
                        val id = data[0].toLong()
                        val skillId = data[1]

                        var preview = false
                        val mixCode = data[2]
                        val mix = GameVersion.parse(mixCode.let {
                            it.toLongOrNull() ?: it.substring(0, it.length - 1).let { seg ->
                                preview = true
                                seg.toLong()
                            }
                        }) ?: GameVersion.entries.last().also {
                            logException(Exception("No game version found for mix code \"$mixCode\""))
                        }

                        val title = data[12]
                        val artist = data[13]

                        songs.add(SongInfo(skillId, id, title, artist, mix, preview))
                        var count = 3
                        PlayStyle.entries.forEach { style ->
                            DifficultyClass.entries.forEach { diff ->
                                if (style != PlayStyle.DOUBLE || diff != DifficultyClass.BEGINNER) {
                                    val diffNum = data[count++].toLong()
                                    if (diffNum != -1L) {
                                        charts.add(ChartInfo(chartId++, skillId, diff, style, diffNum))
                                    }
                                }
                            }
                        }

                        logger.d("Importing $title / $artist (${data.joinToString()})")
                    }
                    dbHelper.insertSongsAndCharts(songs, charts)

                    settings[KEY_SONG_LIST_VERSION] = input.version

                    refreshMemoryData()
                }
            } catch (e: Exception) {
                logger.e(e.stackTraceToString())
            }
        }
    }

    private fun refreshMemoryData() {
        songs = dbHelper.allSongs()
        detailedCharts = dbHelper.allDetailedCharts()
        _libraryFlow.tryEmit(SongLibrary(songs, detailedCharts))

        callback?.invoke()
    }

    companion object {
        const val DEFAULT_IGNORE_VERSION = "A3_US"
    }
}

data class SongLibrary(
    val songs: List<SongInfo> = emptyList(),
    val charts: List<DetailedChartInfo> = emptyList(),
)

class ChartNotFoundException(songTitle: String, playStyle: PlayStyle, difficultyClass: DifficultyClass, difficultyNumber: Int): Exception(
    "$songTitle (${playStyle.aggregateString(difficultyClass)} $difficultyNumber) does not exist in the song database")
