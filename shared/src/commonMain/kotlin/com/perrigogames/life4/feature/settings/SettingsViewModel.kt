package com.perrigogames.life4.feature.settings

import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.*

class SettingsViewModel(
    private val onClose: () -> Unit,
    private val onNavigateToCredits: () -> Unit,
) : ViewModel() {
    private val pageStackState = MutableStateFlow(listOf(SettingsPage.ROOT)).cMutableStateFlow()
    private val pageFlow = pageStackState.map { it.last() }

    val state: CStateFlow<UISettingsData?> = pageFlow.map { createPage(it) }
        .stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = null).cStateFlow()

    fun handleAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.Email -> TODO()
            is SettingsAction.Modal -> TODO()
            is SettingsAction.Navigate -> pushPage(action.page)
            is SettingsAction.NavigateBack -> {
                if (pageStackState.value.size > 1) {
                    popPage()
                } else {
                    onClose()
                }
            }
            SettingsAction.None -> {}
            is SettingsAction.SetBoolean -> TODO()
            is SettingsAction.WebLink -> TODO()
            is SettingsAction.ShowCredits -> onNavigateToCredits()
        }
    }

    private fun pushPage(page: SettingsPage) {
        pageStackState.value += page
    }

    private fun popPage() {
        pageStackState.value = pageStackState.value.dropLast(1)
    }

    private fun createPage(page: SettingsPage): UISettingsData {
        return when (page) {
            SettingsPage.ROOT -> UISettingsMocks.Root.page
            SettingsPage.EDIT_USER_INFO -> UISettingsMocks.EditUser.page
            SettingsPage.TRIAL_SETTINGS -> UISettingsMocks.Trial.page
            SettingsPage.CLEAR_DATA -> UISettingsMocks.ClearData.page
        }
    }
}