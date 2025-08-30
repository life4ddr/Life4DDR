package com.perrigogames.life4.feature.songlist

import co.touchlab.kermit.Logger
import com.perrigogames.life4.MR
import com.perrigogames.life4.api.SongListRemoteData
import com.perrigogames.life4.api.base.FetchListener
import com.perrigogames.life4.api.base.unwrapLoaded
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.GameVersion
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.feature.banners.BannerLocation
import com.perrigogames.life4.feature.banners.IBannerManager
import com.perrigogames.life4.feature.banners.UIBannerTemplates
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.ktor.SanbaiAPI
import com.perrigogames.life4.ktor.SongListResponse
import com.perrigogames.life4.ktor.SongListResponseItem
import com.perrigogames.life4.model.BaseModel
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.inject

/**
 * A Manager class that keeps track of the available songs
 */
interface SongDataManager {
    val dataVersionString: Flow<String>
    val libraryFlow: CStateFlow<SongLibrary>

    fun getSong(skillId: String): Song?
    fun getChart(skillId: String, playStyle: PlayStyle, difficultyClass: DifficultyClass): Chart?
    fun refreshSanbaiData(force: Boolean = false)
}

class DefaultSongDataManager: BaseModel(), SongDataManager {

    private val data: SongListRemoteData by inject()
    private val bannerManager: IBannerManager by inject()
    private val sanbaiAPI: SanbaiAPI by inject()
    private val logger: Logger by injectLogger("SongDataManager")

    override val dataVersionString: Flow<String> =
        data.versionState.map { it.versionString }

    private val _libraryFlow = MutableStateFlow(SongLibrary())
    override val libraryFlow: CStateFlow<SongLibrary> = _libraryFlow.cStateFlow()

    init {
        mainScope.launch {
            data.dataState
                .unwrapLoaded()
                .filterNotNull()
                .collect { parseSanbaiSongData(it.songs) }
        }
        mainScope.launch {
            data.start(object : FetchListener<SongListResponse> {
                override suspend fun onFetchFailed(e: Throwable) {
                    bannerManager.setBanner(
                        banner = UIBannerTemplates.error(MR.strings.song_list_sync_error.desc()),
                        BannerLocation.PROFILE, BannerLocation.SCORES,
                        durationSeconds = 5L,
                    )
                }
            })
        }
    }

    override fun refreshSanbaiData(force: Boolean) {
        // TODO move this into CompositeData
        ktorScope.launch {
            val data = sanbaiAPI.getSongData()
//            if (force || data.lastUpdated > sanbaiAPISettings.songDataUpdated) {
//                sanbaiAPISettings.songDataUpdated = data.lastUpdated
            parseSanbaiSongData(data.songs)
            // FIXME actually cache the data to disk
//            }
        }
    }

    override fun getSong(skillId: String): Song? {
        return libraryFlow.value.songs.keys.firstOrNull { it.skillId == skillId }
    }

    override fun getChart(
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

    private suspend fun parseSanbaiSongData(data: List<SongListResponseItem>) = try {
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
