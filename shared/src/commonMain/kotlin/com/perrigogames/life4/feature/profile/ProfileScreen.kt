package com.perrigogames.life4.feature.profile

import com.perrigogames.life4.MR
import dev.icerock.moko.resources.StringResource

sealed class ProfileScreen(val route: String, val title: StringResource) {
    object Profile : ProfileScreen("profile", MR.strings.tab_profile)
    object Scores : ProfileScreen("scores", MR.strings.tab_scores)
    object Trials : ProfileScreen("trials", MR.strings.tab_trials)
    object Settings : ProfileScreen("settings", MR.strings.tab_settings)
}