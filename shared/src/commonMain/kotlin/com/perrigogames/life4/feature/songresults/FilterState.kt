package com.perrigogames.life4.feature.songresults

import com.perrigogames.life4.GameConstants
import com.perrigogames.life4.GameConstants.HIGHEST_DIFFICULTY
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.feature.songresults.FilterState.Companion.DEFAULT_CLEAR_TYPE_RANGE
import com.perrigogames.life4.feature.songresults.FilterState.Companion.DEFAULT_DIFFICULTY_NUMBER_RANGE

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
)

data class ResultFilterState(
    val clearTypeRange: IntRange = DEFAULT_CLEAR_TYPE_RANGE,
    val scoreRange: IntRange = (0..GameConstants.MAX_SCORE),
    val filterIgnored: Boolean = false,
) {

    constructor(
        clearTypeRange: IntRange = DEFAULT_CLEAR_TYPE_RANGE,
        scoreRangeBottomValue: Int = 0,
        scoreRangeTopValue: Int = GameConstants.MAX_SCORE,
        filterIgnored: Boolean = false,
    ) : this(
        clearTypeRange = clearTypeRange,
        scoreRange = scoreRangeBottomValue..scoreRangeTopValue,
        filterIgnored = filterIgnored
    )
}
