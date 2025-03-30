package com.perrigogames.life4.feature.trials.viewmodel

sealed class TrialSessionEvent {
    data object Close : TrialSessionEvent()
}
