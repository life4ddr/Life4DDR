package com.perrigogames.life4.feature.profile

import com.perrigogames.life4.feature.profile.ProfileDestination.*
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent

/**
 * A ViewModel class for the containing Profile screen. This screen encompasses all the
 * separate tabs that make up the Profile screen, but leaves the operation of those screens
 * to their own ViewModels.
 */
class MainScreenViewModel : ViewModel(), KoinComponent {

    private val _state = MutableStateFlow(MainScreenState()).cMutableStateFlow()
    val state: StateFlow<MainScreenState> = _state.asStateFlow()
}

data class MainScreenState(
    val tabs: List<ProfileDestination> = listOf(Profile, Scores, Trials, Settings)
)
