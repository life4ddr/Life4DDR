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
        songList.start()
        if (majorUpdates.updates.contains(MajorUpdate.SONG_DB)) {
            initializeSongDatabase()
        }
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
                    val data = line.split('\t')
                    val id = data[0].toLong()
                    val skillId = data[1]
                    val title = data[2]
                    val artist = data[3]
                    var preview = false
                    val mix = GameVersion.parse(data[4].let {
                        it.toLongOrNull() ?: it.substring(0, it.length - 1).let { seg ->
                            preview = true
                            seg.toLong()
                        }
                    }) ?: GameVersion.values().last().also {
                        logException(Exception("No game version found for text \"${data[4]}\""))
                    }
                    dbHelper.insertSong(id, skillId, title, artist, mix, preview)
                    var count = 5
                    PlayStyle.values().forEach { style ->
                        DifficultyClass.values().forEach { diff ->
                            if (style != PlayStyle.DOUBLE || diff != DifficultyClass.BEGINNER) {
                                val diffStr = data[count++]
                                if (diffStr.isNotEmpty()) {
                                    dbHelper.insertChart(id, diff, diffStr.toLong(), style)
                                }
                            }
                        }
                    }
                }
                ignoreListManager.invalidateIgnoredIds()
                settings[KEY_SONG_LIST_VERSION] = lines[0].toInt()
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
