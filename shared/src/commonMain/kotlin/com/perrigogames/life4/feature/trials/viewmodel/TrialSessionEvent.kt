package com.perrigogames.life4.feature.trials.viewmodel

import com.perrigogames.life4.feature.trials.view.UISongDetailBottomSheet

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
