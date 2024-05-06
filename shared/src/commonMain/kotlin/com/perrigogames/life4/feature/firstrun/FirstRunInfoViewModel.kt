package com.perrigogames.life4.feature.firstrun

import com.perrigogames.life4.MR
import com.perrigogames.life4.data.SocialNetwork
import com.perrigogames.life4.feature.firstrun.FirstRunError.RivalCodeError
import com.perrigogames.life4.feature.firstrun.FirstRunError.UsernameError
import com.perrigogames.life4.feature.firstrun.FirstRunStep.Landing
import com.perrigogames.life4.feature.firstrun.FirstRunStep.PathStep
import com.perrigogames.life4.feature.firstrun.FirstRunStep.PathStep.Completed
import com.perrigogames.life4.feature.firstrun.FirstRunStep.PathStep.InitialRankSelection
import com.perrigogames.life4.feature.firstrun.FirstRunStep.PathStep.Password
import com.perrigogames.life4.feature.firstrun.FirstRunStep.PathStep.RivalCode
import com.perrigogames.life4.feature.firstrun.FirstRunStep.PathStep.SocialHandles
import com.perrigogames.life4.feature.firstrun.FirstRunStep.PathStep.Username
import com.perrigogames.life4.feature.firstrun.FirstRunStep.PathStep.UsernamePassword
import com.perrigogames.life4.feature.firstrun.InitState.DONE
import com.perrigogames.life4.feature.firstrun.InitState.PLACEMENTS
import com.perrigogames.life4.feature.firstrun.InitState.RANKS
import com.perrigogames.life4.feature.settings.UserInfoSettings
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
import kotlin.reflect.KClass

class FirstRunInfoViewModel : ViewModel(), KoinComponent {
    private val infoSettings: UserInfoSettings by inject()
    private val firstRunSettings: FirstRunSettingsManager by inject()

    val username = MutableStateFlow("").cMutableStateFlow()
    val rivalCode = MutableStateFlow("").cMutableStateFlow()
    val socialNetworks = MutableStateFlow<MutableMap<SocialNetwork, String>>(mutableMapOf()).cMutableStateFlow()

    private val _stateStack = MutableStateFlow<List<FirstRunStep>>(listOf(Landing)).cMutableStateFlow()
    val state: Flow<FirstRunStep> = _stateStack.map { it.last() }

    private val currentStep: FirstRunStep get() = _stateStack.value.last()
    private val currentPath: FirstRunPath? get() = (currentStep as? PathStep)?.path

    private val _errors = MutableStateFlow<List<FirstRunError>>(emptyList()).cMutableStateFlow()
    val errors: Flow<List<FirstRunError>> = _errors

    inline fun <reified T : FirstRunError> errorOfType(): Flow<T?> = errors.map { errors -> errors.firstOrNull { it is T } as? T }

    init {
        viewModelScope.launch {
            infoSettings.userName.collect { username.emit(it) }
        }
        viewModelScope.launch {
            infoSettings.rivalCode.collect { rivalCode.emit(it) }
        }
        viewModelScope.launch {
            infoSettings.socialNetworks.collect { socialNetworks.emit(it.toMutableMap()) }
        }
    }

    fun newUserSelected(isNewUser: Boolean) {
        require(currentPath == null) { "Called newUserSelected when path is already set to $currentPath" }
        val path =
            when (isNewUser) {
                true -> FirstRunPath.NEW_USER_LOCAL
                false -> FirstRunPath.EXISTING_USER_LOCAL
            }
        _stateStack.value += createStateClass(path, clazz = path.steps[0])
    }

    private fun <T : FirstRunStep> createStateClass(
        path: FirstRunPath = currentPath!!,
        rankMethod: InitState? = null,
        clazz: KClass<T>,
    ): FirstRunStep {
        rankMethod?.let { firstRunSettings.setInitState(it) }
        return when (clazz) {
            Username::class -> Username(path)
            Password::class -> Password(path)
            UsernamePassword::class -> UsernamePassword(path)
            RivalCode::class -> RivalCode(path)
            SocialHandles::class -> SocialHandles(path)
            InitialRankSelection::class -> InitialRankSelection(path)
            Completed::class -> Completed(path, rankMethod!!)
            else -> error("Invalid class ${clazz.simpleName}")
        }
    }

    fun rankMethodSelected(method: InitState) {
        infoSettings.setUserBasics(
            name = username.value,
            rivalCode = rivalCode.value,
            socialNetworks = socialNetworks.value,
        )
        firstRunSettings.setInitState(InitState.DONE)
        _stateStack.value += createStateClass(rankMethod = method, clazz = nextStep)
    }

    fun navigateNext() {
        when (currentStep) {
            is Username -> {
                if (username.value.isEmpty()) {
                    emitError(UsernameError())
                    return
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
                    emitError(RivalCodeError())
                    return
                }
            }
            else -> {}
        }
        clearError()
        appendState(createStateClass(clazz = nextStep))
        println("${_stateStack.value.size} / ${_stateStack.value.last()}")
    }

    private fun appendState(step: FirstRunStep) {
        _stateStack.value += step
    }

    private fun emitError(vararg errors: FirstRunError) {
        _errors.value = listOf(*errors)
    }

    private fun clearError() {
        if (_errors.value.isNotEmpty()) {
            _errors.value = emptyList()
        }
    }

    private val nextStep: KClass<out FirstRunStep>
        get() {
            val path = currentPath
            require(path != null) { "Must select a path to advance the step" }
            val currentIndex = path.steps.indexOfFirst { it == currentStep::class }
            return path.steps[currentIndex + 1]
        }

    fun navigateBack(): Boolean {
        if (_stateStack.value.size == 1) {
            return false
        }
        val popped = _stateStack.value.last()
        _stateStack.value -= popped
        clearError()
        return true
    }
}

enum class FirstRunPath(
    val isNewUser: Boolean,
    vararg val steps: KClass<out FirstRunStep>,
) {
    NEW_USER_LOCAL(isNewUser = true, Username::class, RivalCode::class, InitialRankSelection::class, Completed::class),
    NEW_USER_REMOTE(isNewUser = true, Username::class, Password::class, RivalCode::class, InitialRankSelection::class),
    EXISTING_USER_LOCAL(isNewUser = false, Username::class, RivalCode::class, InitialRankSelection::class, Completed::class),
    EXISTING_USER_REMOTE(isNewUser = false, UsernamePassword::class, Completed::class),
    ;

    fun allowedRankSelectionTypes(): List<InitState> =
        when (this) {
            NEW_USER_LOCAL -> listOf(DONE, PLACEMENTS, RANKS)
            NEW_USER_REMOTE -> listOf(DONE, PLACEMENTS)
            EXISTING_USER_LOCAL -> listOf(DONE, RANKS)
            EXISTING_USER_REMOTE -> listOf()
        }
}

sealed class FirstRunStep(
    val showNextButton: Boolean = true,
) {
    data object Landing : FirstRunStep(showNextButton = false)

    sealed class PathStep(
        showNextButton: Boolean = true,
    ) : FirstRunStep(showNextButton) {
        abstract val path: FirstRunPath

        data class Username(
            override val path: FirstRunPath,
        ) : PathStep() {
            val headerText: ResourceStringDesc =
                StringDesc.Resource(
                    when (path.isNewUser) {
                        true -> MR.strings.first_run_username_new_header
                        false -> MR.strings.first_run_username_existing_header
                    },
                )

            val descriptionText: ResourceStringDesc? =
                when (path.isNewUser) {
                    true -> StringDesc.Resource(MR.strings.first_run_username_description)
                    else -> null
                }
        }

        data class Password(override val path: FirstRunPath) : PathStep()

        data class UsernamePassword(override val path: FirstRunPath) : PathStep()

        data class RivalCode(
            override val path: FirstRunPath,
            val rivalCodeError: RivalCodeError? = null,
        ) : PathStep()

        data class SocialHandles(override val path: FirstRunPath) : PathStep()

        data class InitialRankSelection(
            override val path: FirstRunPath,
            val availableMethods: List<InitState> = path.allowedRankSelectionTypes(),
        ) : PathStep(showNextButton = false)

        data class Completed(
            override val path: FirstRunPath,
            val rankSelection: InitState,
        ) : PathStep(showNextButton = false)
    }
}

sealed class FirstRunError {
    abstract val errorText: StringDesc

    class UsernameError(
        override val errorText: StringDesc = StringDesc.Resource(MR.strings.first_run_error_username),
    ) : FirstRunError()

    class PasswordError(
        override val errorText: StringDesc = StringDesc.Resource(MR.strings.first_run_error_password),
    ) : FirstRunError()

    class RivalCodeError(
        override val errorText: StringDesc = StringDesc.Resource(MR.strings.first_run_error_rival_code),
    ) : FirstRunError()
}
