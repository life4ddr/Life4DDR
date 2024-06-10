package com.perrigogames.life4.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.navigation.firstRunNavigation
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
                        firstRunNavigation(
                            navController = navController,
                            onFinish = ::finish
                        )
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
