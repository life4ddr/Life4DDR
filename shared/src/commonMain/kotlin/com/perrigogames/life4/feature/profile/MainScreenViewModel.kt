package com.perrigogames.life4.feature.profile

import com.perrigogames.life4.feature.profile.ProfileScreen.Profile
import com.perrigogames.life4.feature.profile.ProfileScreen.Settings
import com.perrigogames.life4.feature.profile.ProfileScreen.Trials
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent

/**
 * A ViewModel class for the containing Profile screen. This screen encompasses all of the
 * separate tabs that make up the Profile screen, but leaves the operation of those screens
 * to their own ViewModels.
 */
class MainScreenViewModel : ViewModel(), KoinComponent {
    private val _state = MutableStateFlow(MainScreenState()).cMutableStateFlow()
    val state: StateFlow<MainScreenState> = _state.asStateFlow()

    init {
    }
}

data class MainScreenState(
    val banner: UIBanner = UIBanner(""),
    val tabs: List<ProfileScreen> = listOf(Profile, Trials, Settings),
)

data class UIBanner(
    val text: String,
    val color: Int? = null,
)
