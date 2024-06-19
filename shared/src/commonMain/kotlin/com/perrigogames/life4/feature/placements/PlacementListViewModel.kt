package com.perrigogames.life4.feature.placements

import com.perrigogames.life4.data.trials.UIPlacementListScreen
import com.perrigogames.life4.feature.firstrun.FirstRunSettingsManager
import com.perrigogames.life4.feature.firstrun.InitState
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlacementListViewModel : ViewModel(), KoinComponent {

    private val firstRunSettingsManager: FirstRunSettingsManager by inject()
    private val placementManager: PlacementManager by inject()

    private val _screenData = MutableStateFlow(placementManager.createUiData()).cMutableStateFlow()
    val screenData: CStateFlow<UIPlacementListScreen> = _screenData.cStateFlow()

    fun setFirstRunState(state: InitState) {
        firstRunSettingsManager.setInitState(state)
    }
}