package com.perrigogames.life4.feature.profile

import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.laddergoals.UILadderData
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainScreenProfileViewModel : ViewModel(), KoinComponent {
    private val userInfoManager: UserInfoManager by inject()
    private val userRankManager: UserRankManager by inject()

    private val _state = MutableStateFlow<UIMainScreenProfile?>(null).cMutableStateFlow()
    val state: StateFlow<UIMainScreenProfile?> = _state

    init {
        combine(
            userInfoManager.userInfoFlow,
            userRankManager.rank,
        ) { userInfo, rank ->
            _state.value =
                UIMainScreenProfile(
                    infoCard =
                        UIProfileInfoCard(
                            name = userInfo.userName,
                            rivalCode = userInfo.rivalCode,
                            rank = rank,
                        ),
                    ladderData = null,
                )
        }.launchIn(viewModelScope)
    }
}

data class UIMainScreenProfile(
    val infoCard: UIProfileInfoCard,
    val ladderData: UILadderData?,
)

data class UIProfileInfoCard(
    val name: String,
    val rivalCode: String,
    val rank: LadderRank?,
)
