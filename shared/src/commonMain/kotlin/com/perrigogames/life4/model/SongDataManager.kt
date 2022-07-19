package com.perrigogames.life4.model

import co.touchlab.kermit.Logger
import com.perrigogames.life4.SettingsKeys.KEY_SONG_LIST_VERSION
import com.perrigogames.life4.api.SongListRemoteData
import com.perrigogames.life4.api.base.CompositeData
import com.perrigogames.life4.api.base.LocalDataReader
import com.perrigogames.life4.data.SongList
import com.perrigogames.life4.db.*
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.GameVersion
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.SONGS_FILE_NAME
import com.perrigogames.life4.logException
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
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

    private val songList = SongListRemoteData(dataReader, object: CompositeData.NewDataListener<SongList> {
        override fun onDataLoaded(data: SongList) {
            refreshMemoryData()
        }

        override fun onDataVersionChanged(data: SongList) {
            refreshSongDatabase(data)
        }
    })
    val dataVersionString get() = songList.versionString

    lateinit var songs: List<SongInfo>
    lateinit var detailedCharts: List<DetailedChartInfo>
    lateinit var chartsGroupedBySong: Map<SongInfo, List<DetailedChartInfo>>
    lateinit var chartsGroupedByDifficulty: Map<PlayStyle, Map<Long, List<DetailedChartInfo>>>

    private var callback: (() -> Unit)? = null

    internal fun start(callback: (() -> Unit)?) {
        this.callback = callback
        songList.start()
        if (majorUpdates.updates.contains(MajorUpdate.SONG_DB)) { // initial launch
            refreshSongDatabase(delete = true)
        } else {
            refreshMemoryData()
        }
    }

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
        input: SongList = songList.data,
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
                        }) ?: GameVersion.values().last().also {
                            logException(Exception("No game version found for mix code \"$mixCode\""))
                        }

                        val title = data[12]
                        val artist = data[13]

                        songs.add(SongInfo(skillId, id, title, artist, mix, preview))
                        var count = 3
                        PlayStyle.values().forEach { style ->
                            DifficultyClass.values().forEach { diff ->
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

        // group by song
        val sortedCharts = detailedCharts.groupBy { it.skillId }
        chartsGroupedBySong = dbHelper.allSongs()
            .associateWith { sortedCharts[it.skillId]!! }

        // group by play style and difficulty
        chartsGroupedByDifficulty = detailedCharts.groupBy { it.playStyle }
            .mapValues { (_, charts) ->
                charts.groupBy { it.difficultyNumber }
            }

        callback?.invoke()
    }

    companion object {
        const val DEFAULT_IGNORE_VERSION = "A20_US"
    }
}

class SongNotFoundException(name: String): Exception("$name does not exist in the song database")

class ChartNotFoundException(songTitle: String, playStyle: PlayStyle, difficultyClass: DifficultyClass, difficultyNumber: Int): Exception(
    "$songTitle (${playStyle.aggregateString(difficultyClass)} $difficultyNumber) does not exist in the song database")
