package com.perrigogames.life4.android.feature.mainscreen

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.perrigogames.life4.android.activity.profile.PlayerProfileScreen
import com.perrigogames.life4.android.feature.ladder.LadderGoalsScreen
import com.perrigogames.life4.android.feature.settings.SettingsScreen
import com.perrigogames.life4.android.feature.trial.TrialListScreen
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.profile.MainScreenViewModel
import com.perrigogames.life4.feature.profile.PlayerProfileAction
import com.perrigogames.life4.feature.profile.ProfileScreen
import dev.icerock.moko.mvvm.createViewModelFactory

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = viewModel(
        factory = createViewModelFactory { MainScreenViewModel() }
    ),
) {
    val profileNavController = rememberNavController()
    val profileState by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by profileNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                profileState.tabs.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text(stringResource(screen.title.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            profileNavController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(profileNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
//        fun enterTransition(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? {
//            slideInHorizontally(
//                initialOffsetX = { 1000 },
//                animationSpec = tween(500)
//            )
//        }
//        fun exitTransition(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? {
//            slideOutHorizontally(
//                targetOffsetX = { -1000 },
//                animationSpec = tween(500)
//            )
//        }

        NavHost(profileNavController, startDestination = ProfileScreen.Profile.route, Modifier.fillMaxSize().padding(innerPadding)) {
            composable(ProfileScreen.Profile.route) {
                PlayerProfileScreen { action ->
                    when (action) {
                        PlayerProfileAction.ChangeRank -> TODO()
                    }
                }
            }

            composable(ProfileScreen.Scores.route) {
                LadderGoalsScreen()
            }

            composable(ProfileScreen.Trials.route) {
                TrialListScreen(modifier = Modifier.fillMaxSize()) {}
            }

            composable(ProfileScreen.Settings.route) {
                SettingsScreen(
                    modifier = Modifier.fillMaxSize(),
                    onClose = { profileNavController.navigate(ProfileScreen.Profile.route) },
                    onNavigateToCredits = { TODO() }
                )
            }
        }
    }
}