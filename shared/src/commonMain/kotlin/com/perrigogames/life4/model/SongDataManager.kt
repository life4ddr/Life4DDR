package com.perrigogames.life4.model

import com.perrigogames.life4.SettingsKeys.KEY_SONG_LIST_VERSION
import com.perrigogames.life4.api.FetchListener
import com.perrigogames.life4.api.LocalDataReader
import com.perrigogames.life4.api.SongListRemoteData
import com.perrigogames.life4.db.ChartInfo
import com.perrigogames.life4.db.SongDatabaseHelper
import com.perrigogames.life4.db.SongInfo
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.GameVersion
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.SONGS_FILE_NAME
import com.perrigogames.life4.logException
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.launch
import org.koin.core.inject
import org.koin.core.qualifier.named
import kotlin.jvm.Transient


/**
 * A Manager class that keeps track of the available songs
 */
class SongDataManager: BaseModel() {

    private val dbHelper: SongDatabaseHelper by inject()
    private val majorUpdates: MajorUpdateManager by inject()
    private val ignoreListManager: IgnoreListManager by inject()
    private val settings: Settings by inject()
    private val dataReader: LocalDataReader by inject(named(SONGS_FILE_NAME))

    private val songList = SongListRemoteData(dataReader, object: FetchListener<String> {
        override fun onFetchUpdated(data: String) {
            refreshSongDatabase(data)
        }
    })

    init {
        if (majorUpdates.updates.contains(MajorUpdate.SONG_DB)) {
            initializeSongDatabase()
        }
        songList.start()
    }

    //
    // Song List Management
    //
    fun initializeSongDatabase() {
        dbHelper.deleteAll()
        refreshSongDatabase(force = true)
    }

    private fun refreshSongDatabase(input: String = songList.data, force: Boolean = false) {
        mainScope.launch {
            val lines = input.lines()
            if (force || settings.getInt(KEY_SONG_LIST_VERSION, -1) < lines[0].toInt()) {
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
                    }) ?: GameVersion.values().last().also {
                        logException(Exception("No game version found for text \"${data[2]}\""))
                    }
                    dbHelper.insertSong(id, title, null, mix, preview)
                    PlayStyle.values().forEachIndexed { sIdx, style ->
                        DifficultyClass.values().forEachIndexed { dIdx, diff ->
                            val diffStr = data[3 + ((sIdx * DifficultyClass.values().size) + dIdx)]
                            if (diffStr.isNotEmpty()) {
                                dbHelper.insertChart(id, diff, diffStr.toLong(), style)
                            }
                        }
                    }
                }
                ignoreListManager.invalidateIgnoredIds()
                settings[KEY_SONG_LIST_VERSION] = lines[0].toInt()
            }
        }
    }

    fun getCurrentlyIgnoredSongs() = dbHelper.selectSongs(ignoreListManager.selectedIgnoreSongIds)

    fun getCurrentlyIgnoredCharts(): Map<SongInfo, List<ChartInfo>> =
        dbHelper.selectSongsAndCharts(ignoreListManager.selectedIgnoreCharts.map { it.id }).mapValues { entry ->
            val validCharts = ignoreListManager.selectedIgnoreCharts.filter { it.id == entry.key.id }
            entry.value.filter { info -> validCharts.any { it.matches(info)  } }
        }

    fun dumpData() {
        //FIXME
//        val songStrings = dbHelper.songs().map { song ->
//            val builder = StringBuilder("${song.title};")
//            val chartsCopy = song.charts.toMutableList()
//            PlayStyle.values().forEach { style ->
//                DifficultyClass.values().forEach { diff ->
//                    val chart = chartsCopy.firstOrNull { it.playStyle == style && it.difficultyClass == diff }
//                    if (chart != null) {
//                        chartsCopy.remove(chart)
//                        builder.append("${chart.difficultyNumber};")
//                    } else {
//                        builder.append(";")
//                    }
//                }
//            }
//            builder.toString()
//        }.toMutableList()
//        with(StringBuilder()) {
//            while (songStrings.isNotEmpty()) {
//                repeat((0..10).count()) {
//                    if (songStrings.isNotEmpty()) {
//                        append("${songStrings.removeAt(0)}[][]")
//                    }
//                }
//                log("SongDataManager", this.toString())
//                setLength(0)
//            }
//        }
    }

    companion object {
        const val DEFAULT_IGNORE_VERSION = "A20_US"
    }
}

class SongNotFoundException(name: String): Exception("$name does not exist in the song database")

class ChartNotFoundException(songTitle: String, playStyle: PlayStyle, difficultyClass: DifficultyClass, difficultyNumber: Int): Exception(
    "$songTitle (${playStyle.aggregateString(difficultyClass)} $difficultyNumber) does not exist in the song database")
