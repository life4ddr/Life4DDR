package com.perrigogames.life4.viewmodel

import co.touchlab.kermit.Logger
import com.perrigogames.life4.data.PlacementRank
import com.perrigogames.life4.data.trials.UITrialSong
import com.perrigogames.life4.data.trials.toUITrialSong
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.songlist.SongDataManager
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.model.PlacementManager
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlacementDetailsViewModel(
    placementId: String
) : ViewModel(), KoinComponent {

    private val logger: Logger by injectLogger("PlacementDetailsViewModel")
    private val placementManager: PlacementManager by inject()
    private val songDataManager: SongDataManager by inject()

    private val _state = MutableStateFlow(UIPlacementDetails()).cMutableStateFlow()
    val state: StateFlow<UIPlacementDetails> = _state

    init {
        val placement = placementManager.findPlacement(placementId)
        if (placement == null) {
            logger.e("Placement ID $placementId not found")
        } else {
            _state.value = _state.value.copy(
                rankIcon = placement.placementRank!!.toLadderRank(),
                songs = placement.songs.map { song ->
                    song.toUITrialSong(songInfo = songDataManager.findSong(song.skillId))
                }
            )
        }
    }
}

data class UIPlacementDetails(
    val rankIcon: LadderRank = PlacementRank.COPPER.toLadderRank(),
    val descriptionPoints: List<StringDesc> = emptyList(),
    val songs: List<UITrialSong> = emptyList()
)