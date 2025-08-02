package com.perrigogames.life4.android.navigation

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.perrigogames.life4.android.feature.profile.PlayerProfileScreen
import com.perrigogames.life4.android.feature.scorelist.ScoreListScreen
import com.perrigogames.life4.android.feature.settings.SettingsScreen
import com.perrigogames.life4.android.feature.trial.TrialListScreen
import com.perrigogames.life4.feature.firstrun.FirstRunDestination
import com.perrigogames.life4.feature.ladder.LadderDestination
import com.perrigogames.life4.feature.profile.PlayerProfileAction
import com.perrigogames.life4.feature.profile.ProfileDestination
import com.perrigogames.life4.feature.trials.TrialDestination

fun NavGraphBuilder.profileNavigation(
    context: Context,
    mainNavController: NavController,
    profileNavController: NavController
) {
    composable(ProfileDestination.Profile.route) {
        PlayerProfileScreen(
            onBackPressed = { mainNavController.popBackStack() },
            onAction = { action ->
                when (action) {
                    PlayerProfileAction.ChangeRank -> {
                        mainNavController.navigate(LadderDestination.RankList.route)
                    }
                }
            },
        )
    }

    composable(ProfileDestination.Scores.route) {
        ScoreListScreen(
            showSanbaiLogin = { url ->
//                mainNavController.navigate(FirstRunDestination.SanbaiImport(url))
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = android.net.Uri.parse(url)
                }
                context.startActivity(intent)
            },
            onBackPressed = { profileNavController.navigate(ProfileDestination.Profile.route) }
        )
    }

    composable(ProfileDestination.Trials.route) {
        TrialListScreen(
            modifier = Modifier.fillMaxSize(),
            onTrialSelected = { selectedTrial ->
                mainNavController.navigate(TrialDestination.TrialDetails(selectedTrial).route)
            },
            onPlacementsSelected = {
                mainNavController.navigate(FirstRunDestination.PlacementList.baseRoute)
            }
        )
    }

    composable(ProfileDestination.Settings.route) {
        SettingsScreen(
            modifier = Modifier.fillMaxSize(),
            onClose = { mainNavController.popBackStack() },
            onNavigate = { destination ->
                mainNavController.navigate(destination.route)
            }
        )
    }
}