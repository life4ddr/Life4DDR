package com.perrigogames.life4.feature.songresults

import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.feature.songlist.SortedSongsRepo
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ScoreListViewModel : ViewModel(), KoinComponent {
    private val songsRepo: SortedSongsRepo by inject()
    private val resultsRepo: SortedResultsRepo by inject()

    private val _state = MutableStateFlow(UIScoreList()).cMutableStateFlow()
    val state: StateFlow<UIScoreList> = _state

    init {
        viewModelScope.launch {
//            combine(
//                songsRepo.chartsOfPlayStyle(PlayStyle.SINGLE),
//                resultsRepo.resultsOfPlayStyle(PlayStyle.SINGLE),
//            )
//            songsRepo.chartsOfPlayStyle(PlayStyle.SINGLE)
//                .collect { charts ->
//                    _state.value.copy(
//                        scores = charts.map { score ->
//                            UIScore(
//                                leftText = score.score.toString(),
//                                rightText = score.goal.goalString(),
//                                leftDifficulty = score.goal.difficultyClass,
//                                rightClearType = score.clearType
//                            )
//                        }
//                    )
//                }
        }
    }
}

data class ScoreListContentConfig(
    val playStyles: List<PlayStyle>? = listOf(PlayStyle.SINGLE), // TODO Doubles support
    val difficultyClasses: List<DifficultyClass>? = null,
    val difficultyNumbers: List<Int>? = null,
    val clearTypes: List<ClearType>? = null,
    val minScore: Int? = null,
    val maxScore: Int? = null,
)

data class UIScoreList(
    val scores: List<UIScore> = emptyList(),
)

/**
 * @param leftDifficulty the difficulty that determines the color of [leftText]
 * @param rightClearType the clear type that determines the color of [rightText]
 */
data class UIScore(
    val leftText: String = "",
    val rightText: String = "",
    val leftDifficulty: DifficultyClass? = null,
    val rightClearType: ClearType? = null,
)
