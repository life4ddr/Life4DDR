package com.perrigogames.life4.feature.songlist

import co.touchlab.kermit.Logger
import com.perrigogames.life4.api.SongListRemoteData
import com.perrigogames.life4.api.base.LocalDataReader
import com.perrigogames.life4.api.base.unwrapLoaded
import com.perrigogames.life4.data.SongList
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.GameVersion
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.SONGS_FILE_NAME
import com.perrigogames.life4.model.BaseModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.qualifier.named

/**
 * A Manager class that keeps track of the available songs
 */
class SongDataManager: BaseModel() {

    private val dataReader: LocalDataReader by inject(named(SONGS_FILE_NAME))
    private val logger: Logger by injectLogger("SongDataManager")

    private val data = SongListRemoteData(dataReader)

    val dataVersionString: Flow<String> =
        data.versionState.map { it.versionString }

    private val _libraryFlow = MutableStateFlow(SongLibrary())
    val libraryFlow: StateFlow<SongLibrary> = _libraryFlow

    init {
        data.start()
        mainScope.launch {
            data.dataState.unwrapLoaded()
                .filterNotNull()
                .map { parseDataFile(it) }
        }
    }

    private suspend fun parseDataFile(input: SongList) = try {
        val lines = input.songLines
        val songs = mutableMapOf<Song, List<Chart>>()

        lines.forEach { line ->
            if (line.isEmpty()) {
                return@forEach
            }
            val data = line.split('\t')
            val id = data[0].toLong()
            val skillId = data[1]

            var preview = false
            val mixCode = data[2]
            val gameVersion = GameVersion.parse(mixCode.let {
                it.toLongOrNull() ?: it.substring(0, it.length - 1).let { seg ->
                    preview = true
                    seg.toLong()
                }
            }) ?: GameVersion.entries.last().also {
                logger.e { "No game version found for mix code \"$mixCode\"" }
            }

            val title = data[12]
            val artist = data[13]
            val song = Song(
                id = id,
                skillId = skillId,
                title = title,
                artist = artist,
                version = gameVersion,
                preview = preview,
            )

            val charts = mutableListOf<Chart>()
            var count = 3
            PlayStyle.entries.forEach { style ->
                DifficultyClass.entries.forEach { diff ->
                    if (style != PlayStyle.DOUBLE || diff != DifficultyClass.BEGINNER) {
                        val diffNum = data[count++].toInt()
                        if (diffNum != -1) {
                            charts.add(
                                Chart(
                                    song = song,
                                    difficultyClass = diff,
                                    playStyle = style,
                                    difficultyNumber = diffNum,
                                )
                            )
                        }
                    }
                }
            }

            logger.d("Importing $title / $artist (${data.joinToString()})")
        }

        _libraryFlow.emit(
            SongLibrary(
                songs = songs,
                charts = songs.values.flatten()
            )
        )
    } catch (e: Exception) {
        logger.e(e.stackTraceToString())
    }
}

data class SongLibrary(
    val songs: Map<Song, List<Chart>> = emptyMap(),
    val charts: List<Chart> = emptyList(),
)

class ChartNotFoundException(songTitle: String, playStyle: PlayStyle, difficultyClass: DifficultyClass, difficultyNumber: Int): Exception(
    "$songTitle (${playStyle.aggregateString(difficultyClass)} $difficultyNumber) does not exist in the song database")
