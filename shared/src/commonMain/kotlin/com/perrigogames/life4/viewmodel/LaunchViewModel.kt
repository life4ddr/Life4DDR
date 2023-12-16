package com.perrigogames.life4.viewmodel

import com.perrigogames.life4.model.settings.FirstRunSettingsManager
import com.perrigogames.life4.model.settings.InitState
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LaunchViewModel : ViewModel(), KoinComponent {

    private val firstRunSettingsManager: FirstRunSettingsManager by inject()

    private val _launchState = MutableSharedFlow<InitState?>()
    val launchState: Flow<InitState?> = _launchState

    init {
        viewModelScope.launch {
            firstRunSettingsManager.initState.collect(_launchState)
        }
    }
}

data class UILaunchScreen(
    val requireSignin: Boolean,
    val initState: InitState?
)