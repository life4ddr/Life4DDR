package com.perrigogames.life4.feature.songresults

import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.colorRes
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.ColorResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ScoreListViewModel: ViewModel(), KoinComponent {

    private val resultsManager: SongResultsManager by inject()

    private val _state = MutableStateFlow(UIScoreList()).cMutableStateFlow()
    val state: StateFlow<UIScoreList> = _state

    init {
        viewModelScope.launch {
            resultsManager.resultsForConfig(ScoreListContentConfig())
                .collect { results ->
                    _state.value = UIScoreList(
                        scores = results.map { it.toUIScore() }
                    )
                }
        }
    }
}

data class UIScoreList(
    val scores: List<UIScore> = emptyList(),
)

data class UIScore(
    val leftText: String = "",
    val rightText: String = "",
    val leftColor: ColorResource,
    val rightColor: ColorResource,
)

fun ChartResultPair.toUIScore() = UIScore(
    leftText = chart.title,
    rightText = (result?.score ?: 0).toString(),
    leftColor = chart.difficultyClass.colorRes,
    rightColor = (result?.clearType ?: ClearType.NO_PLAY).colorRes,
)
