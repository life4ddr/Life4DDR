package com.perrigogames.life4.viewmodel

import com.perrigogames.life4.data.SocialNetwork
import com.perrigogames.life4.enums.LadderRank
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent

class HomeProfileViewModel : ViewModel(), KoinComponent {

    private val _state = MutableStateFlow(UIHomeProfile()).cMutableStateFlow()
    val state: StateFlow<UIHomeProfile> = _state.asStateFlow()
}

data class UIHomeProfile(
    val header: UIHomeProfileHeader = UIHomeProfileHeader(),
)

data class UIHomeProfileHeader(
    val name: String = "",
    val rank: LadderRank? = null,
    val socialNetworks: List<SocialNetwork> = emptyList(),
)