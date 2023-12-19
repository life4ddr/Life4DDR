package com.perrigogames.life4.viewmodel

import com.perrigogames.life4.data.trials.UIPlacementListScreen
import com.perrigogames.life4.model.PlacementManager
import com.perrigogames.life4.model.settings.FirstRunSettingsManager
import com.perrigogames.life4.model.settings.InitState
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlacementListViewModel : ViewModel(), KoinComponent {

    private val firstRunSettingsManager: FirstRunSettingsManager by inject()
    private val placementManager: PlacementManager by inject()

    private val _screenData = MutableStateFlow(placementManager.createUiData()).cMutableStateFlow()
    val screenData: StateFlow<UIPlacementListScreen> = _screenData

    fun setFirstRunState(state: InitState) {
        firstRunSettingsManager.setInitState(state)
    }
}