package com.perrigogames.life4.model

import co.touchlab.kermit.Logger
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.feature.songlist.IgnoreListManager
import com.perrigogames.life4.feature.songresults.ChartResultPair
import com.perrigogames.life4.feature.songresults.FilterState
import com.perrigogames.life4.feature.songresults.FilterState.Companion.DEFAULT_CLEAR_TYPE_RANGE
import com.perrigogames.life4.feature.songresults.FilterState.Companion.DEFAULT_DIFFICULTY_NUMBER_RANGE
import com.perrigogames.life4.feature.songresults.SongResultsManager
import com.perrigogames.life4.injectLogger
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

typealias OrganizerBase = Map<PlayStyle, Map<Int, List<ChartResultPair>>>

class ChartResultOrganizer: BaseModel(), KoinComponent {

    private val ignoreListManager: IgnoreListManager by inject()
    private val songResultsManager: SongResultsManager by inject()
    private val logger: Logger by injectLogger("ChartResultOrganizer")

    private val basicOrganizer = MutableStateFlow<OrganizerBase>(emptyMap()).cMutableStateFlow()

    init {
        mainScope.launch {
            songResultsManager.library
                .map { pairs ->
                    pairs.groupBy { it.chart.playStyle }
                        .mapValues { (_, chartsByPlayStyle) ->
                            chartsByPlayStyle.groupBy { it.chart.difficultyNumber }
                        }
                }
                .collect(basicOrganizer)
        }
    }

//    fun getResultsFlow(
//        playStyle: PlayStyle,
//        diffNum: Int,
//        populated: Boolean,
//        filterIgnored: Boolean,
//    ): Flow<ChartResults> = basicOrganizer.map { ChartResults(it[playStyle]!![diffNum]!!) }
//        .filter(FilterConfiguration(populated = populated, filterIgnored = filterIgnored))

    fun resultsForConfig(config: FilterState): StateFlow<List<ChartResultPair>> {
        return basicOrganizer
            .map { it[config.selectedPlayStyle] ?: emptyMap() }
            .map { chartsByDifficultyNumber ->
                var temp = if (config.difficultyNumberRange != DEFAULT_DIFFICULTY_NUMBER_RANGE) {
                    config.difficultyNumberRange.flatMap { chartsByDifficultyNumber[it]!! }
                } else {
                    chartsByDifficultyNumber.values.flatten()
                }

                if (config.difficultyClassSelection != DifficultyClass.entries) {
                    temp = temp.filter { it.chart.difficultyClass in config.difficultyClassSelection }
                }

                if (config.clearTypeRange != DEFAULT_CLEAR_TYPE_RANGE) {
                    temp = temp.filter { chart ->
                        val clearType = chart.result?.clearType ?: ClearType.NO_PLAY
                        clearType in config.clearTypeRange.map { ClearType.entries[it] }
                    }
                }

                if (config.scoreRangeBottomValue != null || config.scoreRangeTopValue != null) {
                    val minScore = config.scoreRangeBottomValue ?: 0
                    val maxScore = config.scoreRangeTopValue ?: Int.MAX_VALUE
                    val allowNullScores = minScore == 0
                    temp = temp.filter { chart ->
                        val score = chart.result?.score
                        (score == null && allowNullScores)
                                || (score != null && score >= minScore && score <= maxScore)
                    }
                }

                temp
            }
            .stateIn(mainScope, SharingStarted.Eagerly, initialValue = emptyList())
    }
}

data class FilterConfiguration(
    val populated: Boolean = true,
    val empty: Boolean = true,
    val filterIgnored: Boolean = false,
)
