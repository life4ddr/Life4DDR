package com.perrigogames.life4.feature.songresults

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FilterPanelViewModel : ViewModel() {

    private val _state = MutableStateFlow(FilterState())
    val state: StateFlow<UIFilterView> = _state
        .map {
            it.toUIFilterView(showPlayStyleSelector = false)
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
                    val selection = it.difficultyClassSelection.toMutableMap()
                    when {
                        !selection.containsKey(it.selectedPlayStyle) -> {
                            selection[it.selectedPlayStyle] = mapOf(action.difficultyClass to action.selected)
                        }
                        else -> {
                            val styleMap = selection[it.selectedPlayStyle]!!.toMutableMap()
                            styleMap[action.difficultyClass] = action.selected
                            selection[it.selectedPlayStyle] = styleMap
                        }
                    }
                    it.copy(difficultyClassSelection = selection)
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