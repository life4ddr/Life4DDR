package com.perrigogames.life4.feature.profile

import com.perrigogames.life4.util.Destination
import kotlinx.serialization.Serializable

@Serializable
sealed class ProfileDestination(override val baseRoute: String, val title: String) : Destination {
    @Serializable data object Profile : ProfileDestination("profile", "Profile")
    @Serializable data object Scores : ProfileDestination("scores", "Scores")
    @Serializable data object Trials : ProfileDestination("trials", "Trials")
    @Serializable data object Settings : ProfileDestination("settings", "Settings")
}