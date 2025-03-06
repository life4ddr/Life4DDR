package com.perrigogames.life4.feature.songresults

import com.perrigogames.life4.SettingsKeys.KEY_ENABLE_DIFFICULTY_TIERS
import com.perrigogames.life4.feature.settings.SettingsManager
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalSettingsApi::class)
class SongResultSettings : SettingsManager() {

    val enableDifficultyTiers: Flow<Boolean> =
        settings.getBooleanFlow(KEY_ENABLE_DIFFICULTY_TIERS, false)
            .distinctUntilChanged()

    fun setEnableDifficultyTiers(enabled: Boolean) = mainScope.launch {
        settings.putBoolean(KEY_ENABLE_DIFFICULTY_TIERS, enabled)
    }
}