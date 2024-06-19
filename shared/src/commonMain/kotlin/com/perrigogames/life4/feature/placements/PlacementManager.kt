package com.perrigogames.life4.feature.placements

import co.touchlab.kermit.Logger
import com.perrigogames.life4.MR
import com.perrigogames.life4.api.base.LocalUncachedDataReader
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.data.TrialData
import com.perrigogames.life4.data.trials.UIPlacement
import com.perrigogames.life4.data.trials.UIPlacementListScreen
import com.perrigogames.life4.data.trials.toUITrialSong
import com.perrigogames.life4.feature.songlist.SongDataManager
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.PLACEMENTS_FILE_NAME
import com.perrigogames.life4.model.BaseModel
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import kotlin.math.max
import kotlin.math.min

class PlacementManager: BaseModel() {

    private val logger: Logger by injectLogger("PlacementManager")
    private val songDataManager: SongDataManager by inject()
    private val json: Json by inject()
    private val dataReader: LocalUncachedDataReader by inject(named(PLACEMENTS_FILE_NAME))

    private val baseData: List<Trial> = json
        .decodeFromString(TrialData.serializer(), dataReader.loadInternalString())
        .trials

    private val _placements = songDataManager.libraryFlow
        .map { library ->
            baseData.forEach { placement ->
                placement.songs.forEach { songEntry ->
                    val song = library.songs.keys.firstOrNull { it.skillId == songEntry.skillId }
                    val chart = song?.let { song ->
                        library.songs[song]?.firstOrNull {
                            it.difficultyClass == songEntry.difficultyClass &&
                                    it.playStyle == it.playStyle
                        }
                    }
                    chart?.let { songEntry.chart = it }
                }
            }
            baseData
        }
    val placements: StateFlow<List<Trial>> = _placements
        .stateIn(mainScope, SharingStarted.Lazily, emptyList())

    fun findPlacement(id: String) = _placements.map { placements ->
        placements.firstOrNull { it.id == id }
    }

    fun createUiData() = createUiData(
        placements = placements.value
    )

    fun createUiData(placements: List<Trial>) = UIPlacementListScreen(
        titleText = MR.strings.placements.desc(),
        headerText = MR.strings.placement_list_description.desc(),
        placements = placements.map { placement ->
            UIPlacement(
                id = placement.id,
                rankIcon = placement.placementRank!!.toLadderRank(),
                difficultyRangeString = placement.songs.let { songs ->
                    var lowest = songs[0].chart.difficultyNumber
                    var highest = songs[0].chart.difficultyNumber
                    songs.forEach { song ->
                        lowest = min(lowest, song.chart.difficultyNumber)
                        highest = max(highest, song.chart.difficultyNumber)
                    }
                    "L$lowest-L$highest" // FIXME resource
                },
                songs = placement.songs.map { it.toUITrialSong() }
            )
        }
    )
}
