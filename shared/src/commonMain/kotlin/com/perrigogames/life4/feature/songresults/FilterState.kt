package com.perrigogames.life4.feature.songresults

import com.perrigogames.life4.GameConstants
import com.perrigogames.life4.GameConstants.HIGHEST_DIFFICULTY
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.feature.songresults.FilterState.Companion.DEFAULT_CLEAR_TYPE_RANGE
import com.perrigogames.life4.feature.songresults.FilterState.Companion.DEFAULT_DIFFICULTY_NUMBER_RANGE
import com.perrigogames.life4.feature.songresults.IgnoreFilterType.*

data class FilterState(
    val chartFilter: ChartFilterState = ChartFilterState(),
    val resultFilter: ResultFilterState = ResultFilterState(),
) {
    constructor(
        selectedPlayStyle: PlayStyle = PlayStyle.SINGLE,
        difficultyClassSelection: List<DifficultyClass> = DifficultyClass.entries,
        difficultyNumberRange: IntRange = DEFAULT_DIFFICULTY_NUMBER_RANGE,
        clearTypeRange: IntRange = DEFAULT_CLEAR_TYPE_RANGE,
        scoreRange: IntRange = (0..GameConstants.MAX_SCORE),
        filterIgnored: Boolean = false,
    ) : this(
        chartFilter = ChartFilterState(
            selectedPlayStyle = selectedPlayStyle,
            difficultyClassSelection = difficultyClassSelection,
            difficultyNumberRange = difficultyNumberRange,
        ),
        resultFilter = ResultFilterState(
            clearTypeRange = clearTypeRange,
            scoreRange = scoreRange,
            filterIgnored = filterIgnored
        )
    )

    companion object {
        val DEFAULT_CLEAR_TYPE_RANGE = 0 until ClearType.entries.size
        val DEFAULT_DIFFICULTY_NUMBER_RANGE = 1..HIGHEST_DIFFICULTY
    }
}

data class ChartFilterState(
    val selectedPlayStyle: PlayStyle = PlayStyle.SINGLE,
    val difficultyClassSelection: List<DifficultyClass> = DifficultyClass.entries,
    val difficultyNumberRange: IntRange = DEFAULT_DIFFICULTY_NUMBER_RANGE,
    val ignoreFilterType: IgnoreFilterType = IgnoreFilterType.ALL,
)

/**
 * Describes the mode of song filtering based on supplied ignore codes.
 * @property BASIC no unlockable songs will be shown
 * @property EXPANDED easily unlockable songs will be shown, but others like
 *  Asia-exclusives and Grand Prix paid songs will not be shown
 * @property ALL all songs will be shown
 */
enum class IgnoreFilterType {
    BASIC, EXPANDED, ALL
}

data class ResultFilterState(
    val clearTypeRange: IntRange = DEFAULT_CLEAR_TYPE_RANGE,
    val scoreRange: IntRange = (0..GameConstants.MAX_SCORE),
    val filterIgnored: Boolean = false,
)
