package com.perrigogames.life4.android.activity.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.perrigogames.life4.data.SocialNetwork
import com.perrigogames.life4.model.UserRankManager
import com.perrigogames.life4.model.settings.InfoSettingsManager
import com.perrigogames.life4.viewmodel.GoalListConfig
import com.perrigogames.life4.viewmodel.GoalListViewModel
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
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

    val goalListViewModel = viewModel<GoalListViewModel>(
        factory = viewModelFactory {
            GoalListViewModel(
                GoalListConfig(
                    targetRank = userRankManager.targetRank.value
                )
            )
        }
    )

    init {
        viewModelScope.launch {
            combine(
                infoSettings.userName,
                infoSettings.rivalCodeDisplay,
                infoSettings.socialNetworks,
            ) { userName, rivalCode, socialNetworks ->
                PlayerInfoViewState(userName, rivalCode, socialNetworks)
            }.collect { _playerInfoViewState.value = it }
        }
    }
}

data class PlayerInfoViewState(
    val username: String = "",
    val rivalCode: String? = null,
    val socialNetworks: Map<SocialNetwork, String> = emptyMap(),
)

sealed class PlayerProfileAction {
    object ChangeRank: PlayerProfileAction()
    object Settings: PlayerProfileAction()
    object Trials: PlayerProfileAction()
}