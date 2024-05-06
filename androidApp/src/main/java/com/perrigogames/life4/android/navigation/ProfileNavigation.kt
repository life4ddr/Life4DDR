package com.perrigogames.life4.android.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.perrigogames.life4.feature.profile.ProfileScreen

fun NavGraphBuilder.profileGraph(navController: NavController) {
    navigation(startDestination = ProfileScreen.Profile.route, route = ProfileScreen.Profile.route) {
    }
}
