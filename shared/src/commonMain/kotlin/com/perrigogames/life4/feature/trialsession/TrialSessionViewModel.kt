package com.perrigogames.life4.feature.trialsession

import com.perrigogames.life4.util.ViewState
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TrialSessionViewModel : ViewModel() {

    private val _state = MutableStateFlow<ViewState<UITrialSession, Unit>>(
        ViewState.Loading
    ).cMutableStateFlow()
    val state: StateFlow<ViewState<UITrialSession, Unit>> = _state.asStateFlow()


}

sealed class TrialSessionAction {
    data object StartTrial : TrialSessionAction()
    data object TakePhoto : TrialSessionAction()
    data class UseShortcut(
        val songId: String,
        val shortcut: ShortcutType,
    ) : TrialSessionAction()
    data class SubmitFields(
        val items: List<SubmitFieldsItem>
    ) : TrialSessionAction()
}

enum class ShortcutType {
    MFC, PFC, GFC
}

typealias SubmitFieldsItem = Pair<String, String>
