package com.perrigogames.life4.feature.settings

import com.perrigogames.life4.util.Destination
import kotlinx.serialization.Serializable

@Serializable
sealed class SettingsDestination(override val baseRoute: String) : Destination {

    data object SongLock : SettingsDestination("song_lock")

    data object Credits : SettingsDestination("credits")
}