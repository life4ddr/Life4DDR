package com.perrigogames.life4.feature.placements

import co.touchlab.kermit.Logger
import com.perrigogames.life4.MR
import com.perrigogames.life4.data.PlacementRank
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.songlist.SongDataManager
import com.perrigogames.life4.feature.trials.view.UITrialSong
import com.perrigogames.life4.feature.trials.view.toUITrialSong
import com.perrigogames.life4.injectLogger
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlacementDetailsViewModel(
    placementId: String
) : ViewModel(), KoinComponent {

    private val logger: Logger by injectLogger("PlacementDetailsViewModel")
    private val placementManager: PlacementManager by inject()
    private val songDataManager: SongDataManager by inject()

    private val _state = MutableStateFlow(UIPlacementDetails()).cMutableStateFlow()
    val state: CStateFlow<UIPlacementDetails> = _state.cStateFlow()

    private val _events = MutableSharedFlow<PlacementDetailsEvent>(replay = 0)
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            placementManager.findPlacement(placementId)
                .collect { placement ->
                    if (placement == null) {
                        logger.e("Placement ID $placementId not found")
                    } else {
                        _state.value = _state.value.copy(
                            rankIcon = placement.placementRank!!.toLadderRank(),
                            descriptionPoints = listOf(
                                MR.strings.placement_detail_description_1.desc(),
                                MR.strings.placement_detail_description_2.desc(),
                                MR.strings.placement_detail_description_3.desc(),
                            ),
                            songs = placement.songs.map { song ->
                                song.toUITrialSong()
                            }
                        )
                    }
                }
        }
    }

    fun handleAction(action: PlacementDetailsAction) {
        viewModelScope.launch {
            when (action) {
                PlacementDetailsAction.FinalizeClicked -> _events.emit(PlacementDetailsEvent.ShowCamera)
                PlacementDetailsAction.PictureTaken -> _events.emit(
                    PlacementDetailsEvent.ShowTooltip(
                        title = MR.strings.placement_complete_tooltip_title.desc(),
                        message = MR.strings.placement_complete_tooltip_message.desc(),
                        ctaText = MR.strings.okay.desc(),
                        ctaAction = PlacementDetailsAction.TooltipDismissed
                    )
                )
                PlacementDetailsAction.TooltipDismissed -> {
                    _events.emit(PlacementDetailsEvent.NavigateToMainScreen(
                        submissionUrl = MR.strings.url_submission.desc()
                    ))
                }
            }
        }
    }
}

data class UIPlacementDetails(
    val rankIcon: LadderRank = PlacementRank.COPPER.toLadderRank(),
    val descriptionPoints: List<StringDesc> = emptyList(),
    val songs: List<UITrialSong> = emptyList(),
    val ctaText: StringDesc = MR.strings.finalize.desc(),
    val ctaAction: PlacementDetailsAction = PlacementDetailsAction.FinalizeClicked,
)
