package com.perrigogames.life4.android.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.perrigogames.life4.android.activity.firstrun.FirstRunRankListScreen
import com.perrigogames.life4.android.activity.firstrun.FirstRunScreen
import com.perrigogames.life4.android.activity.firstrun.PlacementScreen
import com.perrigogames.life4.android.compose.LIFE4Theme
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
                    navController.navigate(when(launchState) {
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
                                    InitState.PLACEMENTS -> navController.navigate("placement_list")
                                    InitState.RANKS -> navController.navigate("initial_rank_list")
                                    InitState.DONE -> navController.navigate("main_screen")
                                } },
                                onClose = { finish() },
                            )
                        }

                        composable("placement_list") {
                            PlacementScreen(
                                onPlacementSelected = { TODO() },
                                onRanksClicked = { navController.navigate("initial_rank_list") },
                                goToMainScreen = { navController.navigate("main_screen") }
                            )
                        }

                        composable("initial_rank_list") {
                            FirstRunRankListScreen(
                                onPlacementClicked = { navController.navigate("placement_list") },
                                onRankClicked = { TODO() },
                                goToMainScreen = { navController.navigate("main_screen") }
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
