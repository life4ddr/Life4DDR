package com.perrigogames.life4.android.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.perrigogames.life4.android.compose.Paddings
import com.perrigogames.life4.android.feature.firstrun.FirstRunScreen
import com.perrigogames.life4.android.feature.firstrun.PlacementDetailsScreen
import com.perrigogames.life4.android.feature.firstrun.PlacementListScreen
import com.perrigogames.life4.android.feature.firstrun.RankListScreen
import com.perrigogames.life4.android.feature.mainscreen.MainScreen
import com.perrigogames.life4.android.popAndNavigate
import com.perrigogames.life4.android.view.compose.ComposeWebView
import com.perrigogames.life4.feature.firstrun.FirstRunDestination
import com.perrigogames.life4.feature.firstrun.InitState

fun NavGraphBuilder.firstRunNavigation(
    navController: NavController,
    onFinish: () -> Unit,
) {
    composable(FirstRunDestination.Landing.baseRoute) {}

    composable(FirstRunDestination.FirstRun.baseRoute) {
        FirstRunScreen(
            onComplete = { when (it) {
                InitState.PLACEMENTS -> navController.popAndNavigate("placement_list")
                InitState.RANKS -> navController.popAndNavigate("initial_rank_list")
                InitState.DONE -> navController.popAndNavigate("main_screen")
            } },
            onClose = { onFinish() },
        )
    }

    composable(FirstRunDestination.PlacementList.baseRoute) {
        PlacementListScreen(
            onPlacementSelected = { placementId -> navController.navigate("placement_details/$placementId") },
            onRanksClicked = { navController.popAndNavigate("initial_rank_list") },
            goToMainScreen = { navController.popAndNavigate("main_screen") }
        )
    }

    composable(
        route = FirstRunDestination.PlacementDetails.BASE_ROUTE,
        arguments = listOf(
            navArgument(FirstRunDestination.PlacementDetails.PLACEMENT_ID) { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val placementId = backStackEntry.arguments?.getString(FirstRunDestination.PlacementDetails.PLACEMENT_ID)
        if (placementId != null) {
            PlacementDetailsScreen(placementId = placementId)
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "No placement ID provided",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Paddings.HUGE)
                )
            }
        }
    }

    composable(FirstRunDestination.InitialRankList.baseRoute) {
        RankListScreen(
            isFirstRun = true,
            onPlacementClicked = { navController.popAndNavigate("placement_list") },
            goToMainScreen = { navController.popAndNavigate("main_screen") }
        )
    }

    composable(FirstRunDestination.MainScreen.baseRoute) {
        MainScreen(
            mainNavController = navController
        )
    }

    composable("sanbai_test") {
        ComposeWebView("https://3icecream.com/oauth/authorize?client_id=82b5fefe2a194c74b7f82ec6357d9708&response_type=code&scope=read_scores&redirect_uri=life4ddr://authorize")
    }
}