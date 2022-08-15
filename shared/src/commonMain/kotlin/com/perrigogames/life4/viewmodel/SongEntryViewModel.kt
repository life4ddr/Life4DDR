package com.perrigogames.life4.viewmodel

import com.perrigogames.life4.SettingsKeys
import com.perrigogames.life4.data.Song
import com.perrigogames.life4.data.SongResult
import com.perrigogames.life4.getDebugBoolean
import com.perrigogames.life4.model.TrialSessionManager
import com.perrigogames.life4.util.mutate
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SongEntryViewModel(
    private val songIndex: Int,
    entryState: EntryState = EntryState.BASIC,
    private val requireAllData: Boolean,
): ViewModel(), KoinComponent {

    private val trialSessionManager: TrialSessionManager by inject()
    private val settings: Settings by inject()
    private val currentSession get() = trialSessionManager.currentSession!!
    private val song: Song get() = currentSession.trial.songs[songIndex]
    private val result: SongResult get() = currentSession.results[songIndex]!!

    val scoreText: CMutableStateFlow<String> = MutableStateFlow("").cMutableStateFlow()
    val exScoreText: CMutableStateFlow<String> = MutableStateFlow("").cMutableStateFlow()
    val missesText: CMutableStateFlow<String> = MutableStateFlow("").cMutableStateFlow()
    val goodsText: CMutableStateFlow<String> = MutableStateFlow("").cMutableStateFlow()
    val greatsText: CMutableStateFlow<String> = MutableStateFlow("").cMutableStateFlow()
    val perfectsText: CMutableStateFlow<String> = MutableStateFlow("").cMutableStateFlow()

    val passedChecked: CMutableStateFlow<Boolean> = MutableStateFlow(true).cMutableStateFlow()

    val score: CStateFlow<Int> = scoreText.map { it.toIntOrNull() ?: -1 }
        .stateIn(viewModelScope, SharingStarted.Lazily, -1).cStateFlow()
    val exScore: CStateFlow<Int> = exScoreText.map { it.toIntOrNull() ?: -1 }
        .stateIn(viewModelScope, SharingStarted.Lazily, -1).cStateFlow()
    val misses: CStateFlow<Int> = missesText.map { it.toIntOrNull() ?: -1 }
        .stateIn(viewModelScope, SharingStarted.Lazily, -1).cStateFlow()
    val goods: CStateFlow<Int> = goodsText.map { it.toIntOrNull() ?: -1 }
        .stateIn(viewModelScope, SharingStarted.Lazily, -1).cStateFlow()
    val greats: CStateFlow<Int> = greatsText.map { it.toIntOrNull() ?: -1 }
        .stateIn(viewModelScope, SharingStarted.Lazily, -1).cStateFlow()
    val perfects: CStateFlow<Int> = perfectsText.map { it.toIntOrNull() ?: -1 }
        .stateIn(viewModelScope, SharingStarted.Lazily, -1).cStateFlow()

    private val _scoreState = MutableStateFlow(InputFieldState()).cMutableStateFlow()
    private val _exScoreState = MutableStateFlow(InputFieldState()).cMutableStateFlow()
    private val _missesState = MutableStateFlow(InputFieldState()).cMutableStateFlow()
    private val _goodsState = MutableStateFlow(InputFieldState()).cMutableStateFlow()
    private val _greatsState = MutableStateFlow(InputFieldState()).cMutableStateFlow()
    private val _perfectsState = MutableStateFlow(InputFieldState()).cMutableStateFlow()
    private val _clearControlsVisible = MutableStateFlow(false).cMutableStateFlow()

    val scoreState: CStateFlow<InputFieldState> = _scoreState.cStateFlow()
    val exScoreState: CStateFlow<InputFieldState> = _exScoreState.cStateFlow()
    val missesState: CStateFlow<InputFieldState> = _missesState.cStateFlow()
    val goodsState: CStateFlow<InputFieldState> = _goodsState.cStateFlow()
    val greatsState: CStateFlow<InputFieldState> = _greatsState.cStateFlow()
    val perfectsState: CStateFlow<InputFieldState> = _perfectsState.cStateFlow()
    val clearControlsVisible: CStateFlow<Boolean> = _clearControlsVisible.cStateFlow()

    val imageUri = result.photoUriString

    var entryState: EntryState = entryState
        set(value) {
            field = value
            when(entryState) {
                EntryState.EX_ONLY -> setInputAttributes(
                    exScoreVisible = true,
                )
                EntryState.BASIC -> setInputAttributes(
                    scoreVisible = true,
                    exScoreVisible = true,
                )
                EntryState.BASIC_MISS -> setInputAttributes(
                    scoreVisible = true,
                    exScoreVisible = true,
                    missVisible = true,
                )
                EntryState.FULL -> setInputAttributes(
                    allVisible = true,
                )
                EntryState.FULL_FC -> setInputAttributes(
                    allVisible = true,
                    missEnabled = false,
                )
                EntryState.FULL_PFC -> setInputAttributes(
                    allVisible = true,
                    missEnabled = false,
                    goodEnabled = false,
                    greatEnabled = false,
                )
                EntryState.FULL_MFC -> setInputAttributes(
                    allVisible = true,
                    missEnabled = false,
                    goodEnabled = false,
                    greatEnabled = false,
                    perfectEnabled = false,
                )
            }
        }

    init {
        this.entryState = entryState
    }

    /**
     * Sets visibility and enabled attributes on each of the inputs.
     * If an input is determined to be visible, it will also be enabled unless specified otherwise.
     */
    private fun setInputAttributes(
        allVisible: Boolean = false,
        scoreVisible: Boolean = allVisible,
        exScoreVisible: Boolean = allVisible,
        missVisible: Boolean = allVisible,
        goodVisible: Boolean = allVisible,
        greatVisible: Boolean = allVisible,
        perfectVisible: Boolean = allVisible,
        scoreEnabled: Boolean = scoreVisible,
        exScoreEnabled: Boolean = exScoreVisible,
        missEnabled: Boolean = missVisible,
        goodEnabled: Boolean = goodVisible,
        greatEnabled: Boolean = greatVisible,
        perfectEnabled: Boolean = perfectVisible,
    ) {
        _scoreState.mutate { copy(visible = scoreVisible, enabled = scoreEnabled) }
        _exScoreState.mutate { copy(visible = exScoreVisible, enabled = exScoreEnabled) }
        _missesState.mutate { copy(visible = missVisible, enabled = missEnabled) }
        _goodsState.mutate { copy(visible = goodVisible, enabled = goodEnabled) }
        _greatsState.mutate { copy(visible = greatVisible, enabled = greatEnabled) }
        _perfectsState.mutate { copy(visible = perfectVisible, enabled = perfectEnabled) }
    }

    private fun updateErrorAttribute(
        stateFlow: CMutableStateFlow<InputFieldState>,
        validInputFlow: CStateFlow<Boolean>,
    ) {
        stateFlow.mutate { copy(hasError = !validInputFlow.value) }
    }

    private val hasScore: CStateFlow<Boolean> = hasInputField(score, scoreState)
    private val hasExScore: CStateFlow<Boolean> = hasInputField(exScore, exScoreState)
    private val hasMisses: CStateFlow<Boolean> = hasInputField(misses, missesState)
    private val hasGoods: CStateFlow<Boolean> = hasInputField(goods, goodsState)
    private val hasGreats: CStateFlow<Boolean> = hasInputField(greats, greatsState)
    private val hasPerfects: CStateFlow<Boolean> = hasInputField(perfects, perfectsState)

    private fun hasInputField(
        inputFlow: CStateFlow<Int>,
        stateFlow: CStateFlow<InputFieldState>,
    ): CStateFlow<Boolean> =
        combine(inputFlow, stateFlow) { input, state ->
            state.visible && input >= 0
        }.stateIn(viewModelScope, SharingStarted.Lazily, false).cStateFlow()

    private val hasExtraInfo: CStateFlow<Boolean> =
        combine(hasGoods, hasGreats, hasPerfects) { hasGoods, hasGreats, hasPerfects ->
            hasGoods && hasGreats && hasPerfects
        }.stateIn(viewModelScope, SharingStarted.Lazily, false).cStateFlow()

    private val hasCompleteInfo: CStateFlow<Boolean> =
        combine(score, exScore, hasMisses, hasExtraInfo) { score, exScore, hasMisses, hasExtraInfo ->
            score >= 0 && exScore >= 0 && hasMisses && hasExtraInfo
        }.stateIn(viewModelScope, SharingStarted.Lazily, false).cStateFlow()

    /**
     * Attempts to submit the currently stored data to the manager
     * @return whether the submission was successful
     */
    fun submit(): Boolean {
        return if (
            requireAllData &&
            !hasCompleteInfo.value &&
            !settings.getDebugBoolean(SettingsKeys.KEY_DEBUG_ACCEPT_INVALID)
        ) {
            _scoreState.mutate { copy(hasError = !hasScore.value) }
            _exScoreState.mutate { copy(hasError = !hasExScore.value) }
            _missesState.mutate { copy(hasError = !hasMisses.value) }
            _goodsState.mutate { copy(hasError = !hasGoods.value) }
            _greatsState.mutate { copy(hasError = !hasGreats.value) }
            _perfectsState.mutate { copy(hasError = !hasPerfects.value) }
            false
        } else {
            result.also {
                it.score = score.positiveOrNull
                it.exScore = exScore.positiveOrNull
                it.misses = misses.positiveOrNull
                it.goods = goods.positiveOrNull
                it.greats = greats.positiveOrNull
                it.perfects = perfects.positiveOrNull
                it.passed = passedChecked.value
            }
            true
        }
    }

    private val CStateFlow<Int>.positiveOrNull get() = value.let {
        if (it >= 0) it
        else null
    }

    data class InputFieldState(
        val visible: Boolean = false,
        val enabled: Boolean = false,
        val hasError: Boolean = false,
    )

    enum class EntryState {
        EX_ONLY,
        BASIC,
        BASIC_MISS,
        FULL,
        FULL_FC,
        FULL_PFC,
        FULL_MFC,
    }
}