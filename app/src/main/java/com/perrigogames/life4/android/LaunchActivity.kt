package com.perrigogames.life4.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.compose.Paddings
import com.perrigogames.life4.android.feature.firstrun.FirstRunRankListScreen
import com.perrigogames.life4.android.feature.firstrun.FirstRunScreen
import com.perrigogames.life4.android.feature.firstrun.PlacementDetailsScreen
import com.perrigogames.life4.android.feature.firstrun.PlacementListScreen
import com.perrigogames.life4.android.ui.ComposeWebView
import com.perrigogames.life4.android.ui.MainScreen
import com.perrigogames.life4.android.ui.ladder.LadderGoalsScreen
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.firstrun.InitState
import com.perrigogames.life4.viewmodel.LaunchViewModel
import dev.icerock.moko.mvvm.createViewModelFactory
import org.koin.core.component.KoinComponent

/**
 * The first launched activity, determines the path through the startup flow that should be taken
 * based on the current save state.
 */
class LaunchActivity: AppCompatActivity(), KoinComponent {

    private var loaded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !loaded }

        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            val viewModel: LaunchViewModel = viewModel(
                factory = createViewModelFactory { LaunchViewModel() }
            )

            LaunchedEffect(Unit) {
                viewModel.launchState.collect { launchState ->
                    navController.popAndNavigate(when(launchState) {
                        null -> "first_run"
                        InitState.PLACEMENTS -> "placement_list"
                        InitState.RANKS -> "initial_rank_list"
                        InitState.DONE -> "main_screen"
                    })
                    loaded = true
                }
            }

            LIFE4Theme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "landing",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("landing") {}

                        composable("first_run") {
                            FirstRunScreen(
                                onComplete = { when (it) {
                                    InitState.PLACEMENTS -> navController.popAndNavigate("placement_list")
                                    InitState.RANKS -> navController.popAndNavigate("initial_rank_list")
                                    InitState.DONE -> navController.popAndNavigate("main_screen")
                                } },
                                onClose = { finish() },
                            )
                        }

                        composable("placement_list") {
                            PlacementListScreen(
                                onPlacementSelected = { placementId -> navController.navigate("placement_details/$placementId") },
                                onRanksClicked = { navController.popAndNavigate("initial_rank_list") },
                                goToMainScreen = { navController.popAndNavigate("main_screen") }
                            )
                        }

                        composable(
                            route = "placement_details/{placement_id}",
                            arguments = listOf(navArgument("placement_id") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val placementId = backStackEntry.arguments?.getString("placement_id")
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

                        composable("initial_rank_list") {
                            FirstRunRankListScreen(
                                onPlacementClicked = { navController.popAndNavigate("placement_list") },
                                goToMainScreen = { navController.popAndNavigate("main_screen") }
                            )
                        }

                        composable(
                            route = "rank_details/{ladder_rank_id}",
                            arguments = listOf(navArgument("ladder_rank_id") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val ladderRankId = backStackEntry.arguments?.getLong("ladder_rank_id")
                            LadderGoalsScreen(
                                targetRank = LadderRank.parse(ladderRankId)
                            )
                        }

                        composable("main_screen") {
                            MainScreen()
                        }

                        composable("sanbai_test") {
                            ComposeWebView("https://3icecream.com/oauth/authorize?client_id=82b5fefe2a194c74b7f82ec6357d9708&response_type=code&scope=read_scores&redirect_uri=life4ddr://authorize")
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val uri = intent?.data
        println(uri)
        // Extract the authorization code from the URI and exchange it for an access token
    }
}

fun NavController.popAndNavigate(destination: String) {
    popBackStack()
    navigate(destination)
}
