package com.perrigogames.life4.android.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.ui.firstrun.FirstRunRankListScreen
import com.perrigogames.life4.android.ui.firstrun.FirstRunScreen
import com.perrigogames.life4.android.ui.firstrun.PlacementListScreen
import com.perrigogames.life4.model.settings.InitState
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
                                onPlacementSelected = { TODO() },
                                onRanksClicked = { navController.popAndNavigate("initial_rank_list") },
                                goToMainScreen = { navController.popAndNavigate("main_screen") }
                            )
                        }

                        composable(
                            route = "placement_details/{placement_id}",
                            arguments = listOf(navArgument("placement_id") { type = NavType.StringType })
                        ) {
                            PlacementDetailsScreen
                        }

                        composable("initial_rank_list") {
                            FirstRunRankListScreen(
                                onPlacementClicked = { navController.popAndNavigate("placement_list") },
                                onRankClicked = { TODO() },
                                goToMainScreen = { navController.popAndNavigate("main_screen") }
                            )
                        }

                        composable("main_screen") {

                        }
                    }
                }
            }
        }
    }
}

fun NavController.popAndNavigate(destination: String) {
    popBackStack()
    navigate(destination)
}
