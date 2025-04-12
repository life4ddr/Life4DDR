package com.perrigogames.life4.feature.placements

sealed class PlacementDetailsAction {

    data object FinalizeClicked: PlacementDetailsAction()

    data object PictureTaken : PlacementDetailsAction()

    data object TooltipDismissed : PlacementDetailsAction()
}