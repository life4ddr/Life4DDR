package com.perrigogames.life4.feature.songresults

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FilterPanelViewModel : ViewModel() {

    private val _state = MutableStateFlow(FilterState())
    val state: StateFlow<UIFilterView> = _state
        .map {
            it.toUIFilterView(showPlayStyleSelector = true)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, UIFilterView())

    fun handleAction(action: UIFilterAction) {
        when(action) {
            is UIFilterAction.SelectPlayStyle -> {
                mutate { it.copy(selectedPlayStyle = action.playStyle) }
            }
            is UIFilterAction.SetClearTypeRange -> {
                mutate { it.copy(clearTypeRange = action.range) }
            }
            is UIFilterAction.SetDifficultyNumberRange -> {
                mutate { it.copy(difficultyNumberRange = action.range) }
            }
            is UIFilterAction.SetScoreRange -> {
                mutate {
                    it.copy(
                        scoreRangeBottomValue = action.first,
                        scoreRangeTopValue = action.last
                    )
                }
            }
            is UIFilterAction.ToggleDifficultyClass -> {
                mutate {
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
}