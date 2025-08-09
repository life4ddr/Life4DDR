package com.perrigogames.life4.feature.settings

import com.perrigogames.life4.AppInfo
import com.perrigogames.life4.feature.sanbai.ISanbaiManager
import com.perrigogames.life4.feature.songlist.SongDataManager
import com.perrigogames.life4.feature.songresults.SongResultsManager
import com.perrigogames.life4.util.Destination
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalSettingsApi::class)
class SettingsViewModel(
    private val onClose: () -> Unit,
    private val onNavigate: (Destination) -> Unit,
) : ViewModel(), KoinComponent {
    private val appInfo: AppInfo by inject()
    private val resultsManager: SongResultsManager by inject()
    private val sanbaiManager: ISanbaiManager by inject()
    private val songDataManager: SongDataManager by inject()
    private val settingsPageProvider: SettingsPageProvider by inject()
    private val flowSettings: FlowSettings by inject() // FIXME need to figure out a way to make the action less generic
    private val userInfoSettings: UserInfoSettings by inject()
    private val ladderSettings: LadderSettings by inject()

    private val pageStackState = MutableStateFlow(listOf(SettingsPage.ROOT)).cMutableStateFlow()
    private val pageFlow = pageStackState.map { it.last() }

    val state: CStateFlow<UISettingsData?> = pageFlow.flatMapLatest { createPage(it) }
        .stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = null).cStateFlow()
    private val _events = MutableSharedFlow<SettingsEvent>()
    val events: SharedFlow<SettingsEvent> = _events

    fun handleAction(action: SettingsAction) {
        when (action) {
            SettingsAction.None -> {}
            is SettingsAction.Modal -> TODO()
            is SettingsAction.Navigate -> pushPage(action.page)
            is SettingsAction.NavigateBack -> {
                if (pageStackState.value.size > 1) {
                    popPage()
                } else {
                    onClose()
                }
            }
            is SettingsAction.SetBoolean -> {
                viewModelScope.launch {
                    flowSettings.putBoolean(action.id, action.newValue)
                }
            }
            is SettingsAction.SetString -> {
                viewModelScope.launch {
                    flowSettings.putString(action.id, action.newValue)
                }
            }
            is SettingsAction.SetGameVersion -> {
                viewModelScope.launch {
                    ladderSettings.setSelectedGameVersion(action.newValue)
                }
            }
            is SettingsAction.Email -> {
                viewModelScope.launch {
                    _events.emit(SettingsEvent.Email(action.email))
                }
            }
            is SettingsAction.WebLink -> {
                viewModelScope.launch {
                    _events.emit(SettingsEvent.WebLink(action.url))
                }
            }
            is SettingsAction.ShowCredits -> onNavigate(SettingsDestination.Credits)
            is SettingsAction.Sanbai.RefreshLibrary -> songDataManager.refreshSanbaiData(force = true)
            is SettingsAction.Sanbai.RefreshUserScores -> {
                viewModelScope.launch {
                    sanbaiManager.fetchScores()
                }
            }
            is SettingsAction.Debug.SongLockPage -> onNavigate(SettingsDestination.SongLock)
        }
    }

    private fun pushPage(page: SettingsPage) {
        pageStackState.value += page
    }

    private fun popPage() {
        pageStackState.value = pageStackState.value.dropLast(1)
    }

    private fun createPage(page: SettingsPage): Flow<UISettingsData> {
        return when (page) {
            SettingsPage.ROOT -> settingsPageProvider.getRootPage(isDebug = appInfo.isDebug)
            SettingsPage.EDIT_USER_INFO -> settingsPageProvider.getEditUserPage()
            SettingsPage.SONG_LIST_SETTINGS -> settingsPageProvider.getSongListPage()
            SettingsPage.TRIAL_SETTINGS -> settingsPageProvider.getTrialPage()
            SettingsPage.SANBAI_SETTINGS -> settingsPageProvider.getSanbaiPage()
            SettingsPage.CLEAR_DATA -> settingsPageProvider.getClearDataPage()
            SettingsPage.DEBUG -> settingsPageProvider.getDebugPage()
        }
    }
}