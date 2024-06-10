package com.perrigogames.life4.feature.profile

import com.perrigogames.life4.MR
import com.perrigogames.life4.util.Destination
import dev.icerock.moko.resources.StringResource

sealed class ProfileDestination(override val baseRoute: String, val title: StringResource) : Destination {
    data object Profile : ProfileDestination("profile", MR.strings.tab_profile)
    data object Scores : ProfileDestination("scores", MR.strings.tab_scores)
    data object Trials : ProfileDestination("trials", MR.strings.tab_trials)
    data object Settings : ProfileDestination("settings", MR.strings.tab_settings)
}