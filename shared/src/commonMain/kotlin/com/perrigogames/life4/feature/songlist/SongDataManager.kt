package com.perrigogames.life4.feature.songlist

import co.touchlab.kermit.Logger
import com.perrigogames.life4.api.SongListRemoteData
import com.perrigogames.life4.api.base.LocalDataReader
import com.perrigogames.life4.api.base.unwrapLoaded
import com.perrigogames.life4.db.ChartResult
import com.perrigogames.life4.db.DetailedChartInfo
import com.perrigogames.life4.db.SongInfo
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.feature.songresults.ChartResultPair
import com.perrigogames.life4.feature.songresults.matches
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.SONGS_FILE_NAME
import com.perrigogames.life4.model.BaseModel
import com.perrigogames.life4.model.MajorUpdateManager
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.qualifier.named

/**
 * A Manager class that keeps track of the available songs
 */
class SongDataManager: BaseModel() {

    private val majorUpdates: MajorUpdateManager by inject()
    private val settings: Settings by inject()
    private val dataReader: LocalDataReader by inject(named(SONGS_FILE_NAME))
    private val logger: Logger by injectLogger("SongDataManager")

    private val data = SongListRemoteData(dataReader)
//        override fun onDataLoaded(data: SongList) {
//            refreshMemoryData()
//        }
//
//        override fun onDataVersionChanged(data: SongList) {
//            refreshSongDatabase(data)
//        }

    val dataVersionString: Flow<String> =
        data.versionState.map { it.versionString }

    private val _libraryFlow = MutableStateFlow(SongLibrary())
    val libraryFlow: StateFlow<SongLibrary> = _libraryFlow

    private var callback: (() -> Unit)? = null

    internal fun start(callback: (() -> Unit)?) {
        this.callback = callback
        data.start()
        mainScope.launch {
            data.dataState.unwrapLoaded()
                .map { songList ->
                    // find the old code to parse these lines and create the library from here
                }
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
