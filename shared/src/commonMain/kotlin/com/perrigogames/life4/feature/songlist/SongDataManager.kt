package com.perrigogames.life4.feature.songlist

import co.touchlab.kermit.Logger
import com.perrigogames.life4.api.SongListRemoteData
import com.perrigogames.life4.api.base.unwrapLoaded
import com.perrigogames.life4.data.SongList
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.GameVersion
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.ktor.SanbaiSongListResponseItem
import com.perrigogames.life4.model.BaseModel
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.inject

/**
 * A Manager class that keeps track of the available songs
 */
class SongDataManager: BaseModel() {

    private val data: SongListRemoteData by inject()
    private val logger: Logger by injectLogger("SongDataManager")

    val dataVersionString: Flow<String> =
        data.versionState.map { it.versionString }

    private val _libraryFlow = MutableStateFlow(SongLibrary())
    val libraryFlow: CStateFlow<SongLibrary> = _libraryFlow.cStateFlow()

    init {
        mainScope.launch {
            data.dataState
                .unwrapLoaded()
                .filterNotNull()
                .collect { parseDataFile(it) }
        }
        mainScope.launch {
            data.start()
        }
    }

    fun getSong(skillId: String): Song? {
        return libraryFlow.value.songs.keys.firstOrNull { it.skillId == skillId }
    }

    fun getChart(
        skillId: String,
        playStyle: PlayStyle,
        difficultyClass: DifficultyClass,
    ): Chart? {
        val song = getSong(skillId)
        if (song == null) {
            logger.e("Song not found: $skillId")
            return null
        }
        val chart = libraryFlow.value.songs[song]!!
            .firstOrNull { it.playStyle == playStyle && it.difficultyClass == difficultyClass }
        if (chart == null) {
            logger.e("Chart not found: $skillId / ${playStyle.name} / ${difficultyClass.name}")
        }
        return chart
    }

    suspend fun parseSanbaiSongData(data: List<SanbaiSongListResponseItem>) = try {
        val artists = _libraryFlow.value.songs.keys.map { it.skillId to it.artist }.toMap()

        val songs = mutableMapOf<Song, List<Chart>>()
        data.forEach { item ->
            val song = Song(
                id = -1,
                skillId = item.songId,
                title = item.songName,
                artist = artists[item.songId] ?: "Unknown Artist",
                version = GameVersion.entries[item.versionNum],
                preview = false, // FIXME
                deleted = item.deleted == 1
            )
            songs[song] = item.ratings
                .zip(item.tiers) { rating, tier -> rating to tier }
                .zip(item.lockTypes ?: item.ratings.map { null }) { (r, t), lockTypes -> Triple(r, t, lockTypes) }
                .mapIndexedNotNull { idx, (rating, tier, lockType) ->
                    if (rating != 0) {
                        Chart(
                            song = song,
                            playStyle = when (idx) {
                                in (0..4) -> PlayStyle.SINGLE
                                else -> PlayStyle.DOUBLE
                            },
                            difficultyClass = when (idx) {
                                0 -> DifficultyClass.BEGINNER
                                1, 5 -> DifficultyClass.BASIC
                                2, 6 -> DifficultyClass.DIFFICULT
                                3, 7 -> DifficultyClass.EXPERT
                                4, 8 -> DifficultyClass.CHALLENGE
                                else -> throw Exception("Illegal idx $idx for song ${song.title}")
                            },
                            difficultyNumber = rating,
                            difficultyNumberTier = tier.let { if (it == 1.0) null else it },
                            lockType = lockType
                        )
                    } else {
                        null
                    }
                }
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

            songs[song] = charts
            // logger.d("Importing $title / $artist (${data.joinToString()})")
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

class ChartNotFoundException(
    songTitle: String,
    playStyle: PlayStyle,
    difficultyClass: DifficultyClass,
    difficultyNumber: Int
): Exception("$songTitle (${playStyle.aggregateString(difficultyClass)} $difficultyNumber) does not exist in the song database")
