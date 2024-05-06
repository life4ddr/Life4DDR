package com.perrigogames.life4.viewmodel

import com.perrigogames.life4.enums.LadderRank
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent

class RankSelectionViewModel(config: RankSelectionConfig) : ViewModel(), KoinComponent {
    private val _state = MutableStateFlow(RankSelectionState()).cMutableStateFlow()
    val state: StateFlow<RankSelectionState> = _state

    init {
        _state.value =
            _state.value.copy(
                ranks = LadderRank.entries,
                noRank =
                    if (config.firstRun) {
                        UINoRank.FIRST_RUN
                    } else {
                        UINoRank.DEFAULT
                    },
            )
    }
}

data class RankSelectionConfig(
    val firstRun: Boolean = false,
)

data class RankSelectionState(
    val ranks: List<LadderRank?> = emptyList(),
    val noRank: UINoRank = UINoRank.DEFAULT,
    val initialRank: LadderRank? = null,
)
