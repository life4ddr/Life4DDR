package com.perrigogames.life4.viewmodel

import com.perrigogames.life4.data.SocialNetwork
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.profile.UserRankManager
import com.perrigogames.life4.model.settings.InfoSettingsManager
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlayerProfileViewModel : ViewModel(), KoinComponent {

    private val userRankManager: UserRankManager by inject()
    private val infoSettings: InfoSettingsManager by inject()

    private val _playerInfoViewState = MutableStateFlow(PlayerInfoViewState()).cMutableStateFlow()
    val playerInfoViewState: StateFlow<PlayerInfoViewState> = _playerInfoViewState

    val goalListViewModel = GoalListViewModel(
        GoalListConfig(
            targetRankFlow = userRankManager.targetRank
        )
    )

    init {
        viewModelScope.launch {
            combine(
                infoSettings.userName,
                infoSettings.rivalCodeDisplay,
                infoSettings.socialNetworks,
                userRankManager.rank
            ) { userName, rivalCode, socialNetworks, rank ->
                PlayerInfoViewState(userName, rivalCode, socialNetworks, rank)
            }.collect { _playerInfoViewState.value = it }
        }
    }
}

data class PlayerInfoViewState(
    val username: String = "",
    val rivalCode: String? = null,
    val socialNetworks: Map<SocialNetwork, String> = emptyMap(),
    val rank: LadderRank? = null,
)

sealed class PlayerProfileAction {
    object ChangeRank: PlayerProfileAction()
    object Settings: PlayerProfileAction()
    object Trials: PlayerProfileAction()
}