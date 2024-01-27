package com.perrigogames.life4.feature.placements

import co.touchlab.kermit.Logger
import com.perrigogames.life4.MR
import com.perrigogames.life4.api.base.LocalUncachedDataReader
import com.perrigogames.life4.data.TrialData
import com.perrigogames.life4.data.trials.UIPlacement
import com.perrigogames.life4.data.trials.UIPlacementListScreen
import com.perrigogames.life4.data.trials.toUITrialSong
import com.perrigogames.life4.feature.songlist.SongDataManager
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.isDebug
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.PLACEMENTS_FILE_NAME
import com.perrigogames.life4.model.BaseModel
import com.perrigogames.life4.setCrashString
import dev.icerock.moko.resources.desc.desc
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

    private val placementData: TrialData = json.decodeFromString(TrialData.serializer(), dataReader.loadInternalString())

    val placements get() = placementData.trials

    fun findPlacement(id: String) = placements.firstOrNull { it.id == id }

    fun previousPlacement(id: String) = previousPlacement(placements.indexOfFirst { it.id == id })

    fun previousPlacement(index: Int) = placements.getOrNull(index - 1)

    fun nextPlacement(id: String) = nextPlacement(placements.indexOfFirst { it.id == id })

    fun nextPlacement(index: Int) = placements.getOrNull(index + 1)

    fun createUiData() = UIPlacementListScreen(
        titleText = MR.strings.placements.desc(),
        headerText = MR.strings.placement_list_description.desc(),
        placements = placements.map { placement ->
            UIPlacement(
                id = placement.id,
                rankIcon = placement.placementRank!!.toLadderRank(),
                difficultyRangeString = placement.songs.let { songs ->
                    var lowest = songs[0].difficultyNumber
                    var highest = songs[0].difficultyNumber
                    songs.forEach { song ->
                        lowest = min(lowest, song.difficultyNumber)
                        highest = max(highest, song.difficultyNumber)
                    }
                    "L$lowest-L$highest" // FIXME resource
                },
                songs = placement.songs.map { song ->
                    val songInfo = songDataManager.findSong(skillId = song.skillId)
                    if (songInfo == null) {
                        logger.e("Placement ${placement.name} has skillId ${song.skillId} which was not found")
                    }
                    song.toUITrialSong(songInfo)
                }
            )
        }
    )

    override fun onApplicationException() {
        if (!isDebug) {
            setCrashString("placements", placements.joinToString { it.id })
        }
    }
}
