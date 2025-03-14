package com.perrigogames.life4.feature.trialsession

sealed class TrialSessionEvent {
    data class AcquirePhoto(
        val index: Int,
    ) : TrialSessionEvent()

    data object AcquireResultsPhoto : TrialSessionEvent()

    data class ShowBottomSheet(
        val result: UISongDetailBottomSheet,
    ) : TrialSessionEvent()

    data object Close : TrialSessionEvent()
}
