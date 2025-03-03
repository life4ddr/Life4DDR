package com.perrigogames.life4.feature.songresults

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FilterPanelViewModel : ViewModel() {

    private val _state = MutableStateFlow(FilterState())
    val dataState: StateFlow<FilterState> = _state.asStateFlow()
    val uiState: StateFlow<UIFilterView> = _state
        .map {
            it.toUIFilterView(showPlayStyleSelector = true)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, UIFilterView())

    fun handleAction(action: UIFilterAction) {
        when(action) {
            is UIFilterAction.SelectPlayStyle -> {
                mutateChartFilter { it.copy(selectedPlayStyle = action.playStyle) }
            }
            is UIFilterAction.SetClearTypeRange -> {
                mutateResultFilter { it.copy(clearTypeRange = action.range) }
            }
            is UIFilterAction.SetDifficultyNumberRange -> {
                mutateChartFilter { it.copy(difficultyNumberRange = action.range) }
            }
            is UIFilterAction.SetScoreRange -> {
                mutateResultFilter {
                    val first = action.first ?: it.scoreRange.first
                    val last = action.last ?: it.scoreRange.last
                    it.copy(scoreRange = (first .. last))
                }
            }
            is UIFilterAction.ToggleDifficultyClass -> {
                mutateChartFilter {
                    val selection = it.difficultyClassSelection.toMutableSet()
                    if (action.selected) {
                        selection.add(action.difficultyClass)
                    } else {
                        selection.remove(action.difficultyClass)
                    }
                    it.copy(difficultyClassSelection = selection.toList())
                }
            }
        }
    }

    private fun mutate(block: (FilterState) -> FilterState) {
        viewModelScope.launch {
            val newValue = block(_state.value)
            _state.emit(newValue)
        }
    }

    private fun mutateChartFilter(block: (ChartFilterState) -> ChartFilterState) {
        mutate { it.copy(chartFilter = block(it.chartFilter)) }
    }

    private fun mutateResultFilter(block: (ResultFilterState) -> ResultFilterState) {
        mutate { it.copy(resultFilter = block(it.resultFilter)) }
    }
}