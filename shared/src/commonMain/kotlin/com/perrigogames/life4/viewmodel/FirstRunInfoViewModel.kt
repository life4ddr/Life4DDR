package com.perrigogames.life4.viewmodel

import com.perrigogames.life4.MR
import com.perrigogames.life4.data.SocialNetwork
import com.perrigogames.life4.model.settings.InfoSettingsManager
import com.perrigogames.life4.model.settings.InitState
import com.perrigogames.life4.viewmodel.FirstRunState.*
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FirstRunInfoViewModel : ViewModel(), KoinComponent {

    private val infoSettings: InfoSettingsManager by inject()

    val username = MutableStateFlow("").cMutableStateFlow()
    val rivalCode = MutableStateFlow("").cMutableStateFlow()
    val socialNetworks = MutableStateFlow<MutableMap<SocialNetwork, String>>(mutableMapOf()).cMutableStateFlow()

    private val _stateStack = MutableStateFlow<List<FirstRunState>>(listOf(Landing)).cMutableStateFlow()
    val state: Flow<FirstRunState> = _stateStack.map { it.last() }

    init {
        viewModelScope.launch {
            infoSettings.userName.collect { username.emit(it) }
            infoSettings.rivalCode.collect { rivalCode.emit(it) }
            infoSettings.socialNetworks.collect { socialNetworks.emit(it.toMutableMap()) }
        }
    }

    fun newUserSelected(isNewUser: Boolean) {
        _stateStack.value += Username(isNewUser = isNewUser)
    }

    fun rankMethodSelected(method: InitState) {
        infoSettings.setUserBasics(
            name = username.value,
            rivalCode = rivalCode.value,
            socialNetworks = socialNetworks.value,
        )
        _stateStack.value += Completed(method)
    }

    fun navigateNext() {
        when (val currState = _stateStack.value.last()) {
            Landing -> error("Must call newUserSelected")
            is Username -> {
                _stateStack.value += RivalCode(isNewUser = currState.isNewUser)
            }
            is RivalCode -> {
                // remove social handles until it works better
//                _stateStack.value += SocialHandles(isNewUser = currState.isNewUser)
//            }
//            is SocialHandles -> {
                if (currState.isNewUser) {
                    _stateStack.value += InitialRankSelection
                } else {
                    _stateStack.value += Completed(method = InitState.RANKS)
                }
            }
            InitialRankSelection -> error("Must call rankMethodSelected")
            is Completed -> error("Cannot continue from this state")
            else -> error("Unhandled state ${currState::class.simpleName}")
        }
    }

    fun navigateBack(): Boolean {
        if (_stateStack.value.size == 1) {
            return false
        }
        val popped = _stateStack.value.last()
        _stateStack.value -= popped
        return true
    }
}

sealed class FirstRunState(
    val showNextButton: Boolean = true,
    val isNewUser: Boolean = false,
) {
    object Landing: FirstRunState(showNextButton = false)

    class Username(isNewUser: Boolean): FirstRunState(isNewUser = isNewUser) {

        val headerText = when (isNewUser) {
            true -> StringDesc.Resource(MR.strings.first_run_username_new_header)
            false -> StringDesc.Resource(MR.strings.first_run_username_existing_header)
        }

        val descriptionText = when (isNewUser) {
            true -> StringDesc.Resource(MR.strings.first_run_username_description)
            false -> null
        }
    }

    class RivalCode(isNewUser: Boolean): FirstRunState(isNewUser = isNewUser)

    class SocialHandles(isNewUser: Boolean): FirstRunState(isNewUser = isNewUser)

    object InitialRankSelection: FirstRunState(showNextButton = false)

    class Completed(val method: InitState): FirstRunState(showNextButton = false)
}
