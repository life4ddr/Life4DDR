package com.perrigogames.life4.feature.placements

import dev.icerock.moko.resources.desc.StringDesc

sealed class PlacementDetailsEvent {

    data object ShowCamera: PlacementDetailsEvent()

    data class ShowTooltip(
        val title: StringDesc,
        val message: StringDesc,
        val ctaText: StringDesc,
        val ctaAction: PlacementDetailsAction,
    ) : PlacementDetailsEvent()

    data class NavigateToMainScreen(
        val submissionUrl: StringDesc? = null,
    ) : PlacementDetailsEvent()
}