package com.perrigogames.life4.android.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.perrigogames.life4.MR
import dev.icerock.moko.resources.StringResource

fun NavGraphBuilder.profileGraph(navController: NavController) {
    navigation(startDestination = ProfileScreen.Profile.route, route = ProfileScreen.Profile.route) {

    }
}

sealed class ProfileScreen(val route: String, val title: StringResource) {
    object Profile : ProfileScreen("profile", MR.strings.tab_profile)
    object Browse : ProfileScreen("browse", MR.strings.tab_browse)
    object Trials : ProfileScreen("trials", MR.strings.tab_trials)
    object Settings : ProfileScreen("settings", MR.strings.tab_settings)

    companion object {
        val activeScreens = listOf(Profile, Trials, Settings)
    }
}
