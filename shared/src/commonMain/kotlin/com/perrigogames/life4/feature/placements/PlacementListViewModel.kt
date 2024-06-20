package com.perrigogames.life4.feature.placements

import com.perrigogames.life4.data.trials.UIPlacementListScreen
import com.perrigogames.life4.feature.firstrun.FirstRunSettingsManager
import com.perrigogames.life4.feature.firstrun.InitState
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlacementListViewModel : ViewModel(), KoinComponent {

    private val firstRunSettingsManager: FirstRunSettingsManager by inject()
    private val placementManager: PlacementManager by inject()

    private val _screenData = MutableStateFlow(placementManager.createUiData()).cMutableStateFlow()
    val screenData: CStateFlow<UIPlacementListScreen> = _screenData.cStateFlow()

    init {
        viewModelScope.launch {
            placementManager.placements
                .map { placementManager.createUiData(it) }
                .collect(_screenData)
        }
    }

    fun setFirstRunState(state: InitState) {
        firstRunSettingsManager.setInitState(state)
    }
}