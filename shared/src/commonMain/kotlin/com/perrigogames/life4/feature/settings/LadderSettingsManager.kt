package com.perrigogames.life4.feature.settings

import com.perrigogames.life4.enums.GameVersion
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalSettingsApi::class)
class LadderSettingsManager : SettingsManager() {
    val selectedGameVersion: StateFlow<GameVersion> =
        settings.getStringFlow(KEY_GAME_VERSION, "")
            .map { GameVersion.parse(it) ?: GameVersion.defaultVersion }
            .stateIn(mainScope, SharingStarted.Lazily, GameVersion.defaultVersion)

    fun setSelectedGameVersion(version: GameVersion) {
        mainScope.launch {
            settings.putString(KEY_GAME_VERSION, version.name)
        }
    }

    companion object {
        const val KEY_GAME_VERSION = "KEY_GAME_VERSION"
    }
}