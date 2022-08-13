package com.perrigogames.life4.viewmodel

import com.perrigogames.life4.model.TrialSessionManager
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
    private val showAdvanced: Boolean,
    private val requireAllData: Boolean,
): ViewModel(), KoinComponent {

    private val trialSessionManager: TrialSessionManager by inject()
    private val settings: Settings by inject()
    private val currentSession get() = trialSessionManager.currentSession!!

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

    private val _scoreVisible = MutableStateFlow(true).cMutableStateFlow()
    private val _exScoreVisible = MutableStateFlow(true).cMutableStateFlow()
    private val _missesVisible = MutableStateFlow(false).cMutableStateFlow()
    private val _goodGreatPerfectVisible = MutableStateFlow(false).cMutableStateFlow()
    private val _scoreEnabled = MutableStateFlow(true).cMutableStateFlow()
    private val _exScoreEnabled = MutableStateFlow(true).cMutableStateFlow()
    private val _missesEnabled = MutableStateFlow(false).cMutableStateFlow()
    private val _goodsEnabled = MutableStateFlow(false).cMutableStateFlow()
    private val _greatsEnabled = MutableStateFlow(false).cMutableStateFlow()
    private val _perfectsEnabled = MutableStateFlow(false).cMutableStateFlow()
    private val _clearControlsVisible = MutableStateFlow(false).cMutableStateFlow()

    val scoreVisible: CStateFlow<Boolean> = _scoreVisible.cStateFlow()
    val exScoreVisible: CStateFlow<Boolean> = _exScoreVisible.cStateFlow()
    val missesVisible: CStateFlow<Boolean> = _missesVisible.cStateFlow()
    val goodGreatPerfectVisible: CStateFlow<Boolean> = _goodGreatPerfectVisible.cStateFlow()
    val scoreEnabled: CStateFlow<Boolean> = _scoreEnabled.cStateFlow()
    val exScoreEnabled: CStateFlow<Boolean> = _exScoreEnabled.cStateFlow()
    val missesEnabled: CStateFlow<Boolean> = _missesEnabled.cStateFlow()
    val goodsEnabled: CStateFlow<Boolean> = _goodsEnabled.cStateFlow()
    val greatsEnabled: CStateFlow<Boolean> = _greatsEnabled.cStateFlow()
    val perfectsEnabled: CStateFlow<Boolean> = _perfectsEnabled.cStateFlow()
    val clearControlsVisible: CStateFlow<Boolean> = _clearControlsVisible.cStateFlow()

    var entryState: EntryState = EntryState.BASIC
    set(value) {
        field = value
        when(entryState) {
            EntryState.BASIC -> {
                setFieldsVisibility(score = true, exScore = true)
            }
            EntryState.BASIC_MISS -> {
                setFieldsVisibility(score = true, exScore = true, miss = true)
                setFieldsEnabled(miss = true)
            }
            EntryState.FULL -> {
                setFieldsVisibility(score = true, exScore = true, miss = true, ggp = true)
                setFieldsEnabled(miss = true, good = true, great = true, perfect = true)
            }
            EntryState.FULL_FC -> {
                setFieldsVisibility(score = true, exScore = true, miss = true, ggp = true)
                setFieldsEnabled(good = true, great = true, perfect = true)
            }
            EntryState.FULL_PFC -> {
                setFieldsVisibility(score = true, exScore = true, miss = true, ggp = true)
                setFieldsEnabled(perfect = true)
            }
            EntryState.FULL_MFC -> {
                setFieldsVisibility(score = true, exScore = true, miss = true, ggp = true)
                setFieldsEnabled(score = false, exScore = false)
            }
        }
    }

    private fun setFieldsVisibility(
        score: Boolean = false,
        exScore: Boolean = false,
        miss: Boolean = false,
        ggp: Boolean = false,
    ) {
        _scoreVisible.value = score
        _exScoreVisible.value = exScore
        _missesVisible.value = miss
        _goodGreatPerfectVisible.value = ggp
    }

    private fun setFieldsEnabled(
        score: Boolean = true,
        exScore: Boolean = true,
        miss: Boolean = false,
        good: Boolean = false,
        great: Boolean = false,
        perfect: Boolean = false,
    ) {
        _missesEnabled.value = miss
        _goodsEnabled.value = good
        _greatsEnabled.value = great
        _perfectsEnabled.value = perfect
    }

    private val hasMisses: CStateFlow<Boolean> =
        combine(misses, missesVisible) { misses, visible ->
            visible && misses >= 0
        }.stateIn(viewModelScope, SharingStarted.Lazily, false).cStateFlow()

    private val hasGoods: CStateFlow<Boolean> =
        combine(goods, goodGreatPerfectVisible) { goods, visible ->
            visible && goods >= 0
        }.stateIn(viewModelScope, SharingStarted.Lazily, false).cStateFlow()

    private val hasGreats: CStateFlow<Boolean> =
        combine(greats, goodGreatPerfectVisible) { greats, visible ->
            visible && greats >= 0
        }.stateIn(viewModelScope, SharingStarted.Lazily, false).cStateFlow()

    private val hasPerfects: CStateFlow<Boolean> =
        combine(perfects, goodGreatPerfectVisible) { perfects, visible ->
            visible && perfects >= 0
        }.stateIn(viewModelScope, SharingStarted.Lazily, false).cStateFlow()

    private val hasExtraInfo: CStateFlow<Boolean> =
        combine(hasGoods, hasGreats, hasPerfects) { hasGoods, hasGreats, hasPerfects ->
            hasGoods && hasGreats && hasPerfects
        }.stateIn(viewModelScope, SharingStarted.Lazily, false).cStateFlow()

    enum class EntryState {
        BASIC,
        BASIC_MISS,
        FULL,
        FULL_FC,
        FULL_PFC,
        FULL_MFC,
    }
}