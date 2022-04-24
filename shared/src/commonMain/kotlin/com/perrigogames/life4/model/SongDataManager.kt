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
    private val ignoreListManager: IgnoreListManager by inject()
    private val settings: Settings by inject()
    private val dataReader: LocalDataReader by inject(named(SONGS_FILE_NAME))
    private val logger: Logger by injectLogger("SongDataManager")

    private val songList = SongListRemoteData(dataReader, object: CompositeData.NewDataListener<SongList> {
        override fun onDataVersionChanged(data: SongList) {
            refreshSongDatabase(data)
        }

        override fun onMajorVersionBlock() = Unit
    })
    val dataVersionString get() = songList.versionString

    var songs: List<SongInfo> = dbHelper.allSongs()
    var detailedCharts: List<DetailedChartInfo> = dbHelper.allDetailedCharts()
    var groupedCharts: Map<SongInfo, List<DetailedChartInfo>> = generateGroupedCharts()

    init {
        songList.start()
        if (majorUpdates.updates.contains(MajorUpdate.SONG_DB)) {
            initializeSongDatabase()
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
    fun initializeSongDatabase() {
        dbHelper.deleteAll()
        refreshSongDatabase(force = true)
    }

    private fun refreshSongDatabase(input: SongList = songList.data, force: Boolean = false) {
        mainScope.launch {
            try {
                val lines = input.songLines
                if (force || settings.getInt(KEY_SONG_LIST_VERSION, -1) < input.version) {

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
                                        charts.add(ChartInfo(skillId, diff, style, diffNum))
                                    }
                                }
                            }
                        }

                        logger.d("Importing $title / $artist (${data.joinToString()})")
                        if (songs.last().skillId == "P1boP1O60bdI16i6lqqd1Q8ioiPbolqd") {
                            logger.d(songs.last().skillId)
                            logger.d(songs.last().title)
                        }
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
        groupedCharts = generateGroupedCharts()
    }

    private fun generateGroupedCharts(): Map<SongInfo, List<DetailedChartInfo>> {
        val tempSongs = dbHelper.allSongs().toMutableList()
        return detailedCharts.groupBy { it.skillId }
            .mapKeys { (skillId, _) ->
                tempSongs.first { it.skillId == skillId }.also {
                    tempSongs.remove(it)
                }
            }
    }

    companion object {
        const val DEFAULT_IGNORE_VERSION = "A20_US"
    }
}

class SongNotFoundException(name: String): Exception("$name does not exist in the song database")

class ChartNotFoundException(songTitle: String, playStyle: PlayStyle, difficultyClass: DifficultyClass, difficultyNumber: Int): Exception(
    "$songTitle (${playStyle.aggregateString(difficultyClass)} $difficultyNumber) does not exist in the song database")
