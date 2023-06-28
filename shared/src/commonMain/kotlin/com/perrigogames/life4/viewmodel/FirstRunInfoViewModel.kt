package com.perrigogames.life4.viewmodel

import com.perrigogames.life4.MR
import com.perrigogames.life4.data.SocialNetwork
import com.perrigogames.life4.model.settings.InfoSettingsManager
import com.perrigogames.life4.model.settings.InitState
import com.perrigogames.life4.model.settings.InitState.*
import com.perrigogames.life4.viewmodel.FirstRunStep.*
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceStringDesc
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

    private val _stateStack = MutableStateFlow(listOf(FirstRunState(step = Landing))).cMutableStateFlow()
    val state: Flow<FirstRunState> = _stateStack.map { it.last() }

    private val currentState get() = _stateStack.value.last()
    private val currentPath get() = currentState.path

    init {
        viewModelScope.launch {
            infoSettings.userName.collect { username.emit(it) }
            infoSettings.rivalCode.collect { rivalCode.emit(it) }
            infoSettings.socialNetworks.collect { socialNetworks.emit(it.toMutableMap()) }
        }
    }

    fun newUserSelected(isNewUser: Boolean) {
        require(currentPath == null) { "Called newUserSelected when path is already set to $currentPath" }
        val path = when (isNewUser) {
            true -> FirstRunPath.NEW_USER_LOCAL
            false -> FirstRunPath.EXISTING_USER_LOCAL
        }
        _stateStack.value += FirstRunState(
            path = path,
            step = path.steps[0],
        )
    }

    fun rankMethodSelected(method: InitState) {
        infoSettings.setUserBasics(
            name = username.value,
            rivalCode = rivalCode.value,
            socialNetworks = socialNetworks.value,
        )
        _stateStack.value += currentState.copy(
            step = currentState.nextStep,
            rankSelection = method,
        )
    }

    fun navigateNext() {
        when (val currentStep = currentState.step) {
            is Username -> {
                if (username.value.isEmpty()) {
                    replaceStep(currentStep.copy(
                        usernameError = StringDesc.Resource(MR.strings.first_run_error_username))
                    )
                    return
                } else {
                    replaceStep(currentStep.copy(usernameError = null))
                }
            }
            is Password -> {
                // TODO validate password
            }
            is UsernamePassword -> {
                // TODO make network call and handle result
                return
            }
            is RivalCode -> {
                val length = rivalCode.value.length
                if (length != 0 && length != 8) {
                    replaceStep(currentStep.copy(
                        rivalCodeError = StringDesc.Resource(MR.strings.first_run_error_rival_code))
                    )
                    return
                } else {
                    replaceStep(currentStep.copy(rivalCodeError = null))
                }
            }
            else -> {}
        }
        appendState(currentState.copy(
            step = currentState.nextStep,
        ))
        println("${_stateStack.value.size} / ${_stateStack.value.last()}")
    }

    private fun appendState(state: FirstRunState) {
        _stateStack.value += state
    }

    private fun replaceState(state: FirstRunState) {
        _stateStack.value = _stateStack.value.toMutableList().also { stack ->
            stack.removeLast()
            stack.add(state)
        }
    }

    private fun replaceStep(step: FirstRunStep) {
        replaceState(currentState.copy(step = step))
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

data class FirstRunState(
    val step: FirstRunStep,
    val path: FirstRunPath? = null,
    val rankSelection: InitState? = null,
) {

    val nextStep: FirstRunStep
        get() {
            require(path != null) { "Must select a path to advance the step" }
            val currentIndex = path.steps.indexOfFirst { it::class == step::class }
            return path.steps[currentIndex + 1]
        }

    val headerText: ResourceStringDesc? = when (step) {
        is Username -> when (path?.isNewUser ?: true) {
            true -> StringDesc.Resource(MR.strings.first_run_username_new_header)
            false -> StringDesc.Resource(MR.strings.first_run_username_existing_header)
        }
        else -> null
    }

    val descriptionText: ResourceStringDesc? = when (step) {
        is Username -> when (path?.isNewUser ?: true) {
            true -> StringDesc.Resource(MR.strings.first_run_username_description)
            else -> null
        }
        else -> null
    }
}

enum class FirstRunPath(
    val isNewUser: Boolean,
    vararg val steps: FirstRunStep
) {
    NEW_USER_LOCAL (isNewUser = true, Username(), RivalCode(), InitialRankSelection, Completed),
    NEW_USER_REMOTE (isNewUser = true, Username(), Password(), RivalCode(), InitialRankSelection),
    EXISTING_USER_LOCAL (isNewUser = false, Username(), RivalCode(), InitialRankSelection, Completed),
    EXISTING_USER_REMOTE (isNewUser = false, UsernamePassword(), Completed),
    ;

    fun allowedRankSelectionTypes(): List<InitState> = when (this) {
        NEW_USER_LOCAL -> listOf(DONE, PLACEMENTS, RANKS)
        NEW_USER_REMOTE -> listOf(DONE, PLACEMENTS)
        EXISTING_USER_LOCAL -> listOf(DONE, RANKS)
        EXISTING_USER_REMOTE -> listOf()
    }
}

sealed class FirstRunStep(
    val showNextButton: Boolean = true
) {
    object Landing : FirstRunStep(showNextButton = false)

    data class Username(
        val usernameError: ResourceStringDesc? = null
    ) : FirstRunStep()

    data class Password(
        val passwordError: ResourceStringDesc? = null
    ) : FirstRunStep()

    data class UsernamePassword(
        val usernameError: ResourceStringDesc? = null,
        val passwordError: ResourceStringDesc? = null,
    ) : FirstRunStep()

    data class RivalCode(
        val rivalCodeError: ResourceStringDesc? = null,
    ) : FirstRunStep()

    object SocialHandles : FirstRunStep()

    object InitialRankSelection : FirstRunStep(showNextButton = false)

    object Completed : FirstRunStep(showNextButton = false)
}
