package com.perrigogames.life4.android.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.perrigogames.life4.android.feature.profile.PlayerProfileScreen
import com.perrigogames.life4.android.feature.scorelist.ScoreListScreen
import com.perrigogames.life4.android.feature.settings.SettingsScreen
import com.perrigogames.life4.android.feature.trial.TrialListScreen
import com.perrigogames.life4.feature.ladder.LadderDestination
import com.perrigogames.life4.feature.profile.PlayerProfileAction
import com.perrigogames.life4.feature.profile.ProfileDestination
import com.perrigogames.life4.feature.trials.TrialDestination

fun NavGraphBuilder.profileNavigation(
    mainNavController: NavController,
    profileNavController: NavController
) {
    composable(ProfileDestination.Profile.route) {
        PlayerProfileScreen { action ->
            when (action) {
                PlayerProfileAction.ChangeRank -> {
                    mainNavController.navigate(LadderDestination.RankList.route)
                }
            }
        }
    }

    composable(ProfileDestination.Scores.route) {
        ScoreListScreen()
    }

    composable(ProfileDestination.Trials.route) {
        TrialListScreen(
            modifier = Modifier.fillMaxSize(),
            onTrialSelected = { selectedTrial ->
                mainNavController.navigate(TrialDestination.TrialDetails(selectedTrial).route)
            }
        )
    }

    composable(ProfileDestination.Settings.route) {
        SettingsScreen(
            modifier = Modifier.fillMaxSize(),
            onClose = { mainNavController.popBackStack() },
            onNavigateToCredits = { TODO() }
        )
    }
}