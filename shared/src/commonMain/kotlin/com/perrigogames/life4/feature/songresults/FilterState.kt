package com.perrigogames.life4.feature.songresults

import com.perrigogames.life4.GameConstants.HIGHEST_DIFFICULTY
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle

data class FilterState(
    val selectedPlayStyle: PlayStyle = PlayStyle.SINGLE,
    val difficultyClassSelection: List<DifficultyClass> = DifficultyClass.entries,
    val difficultyNumberRange: IntRange = DEFAULT_DIFFICULTY_NUMBER_RANGE,
    val clearTypeRange: IntRange = DEFAULT_CLEAR_TYPE_RANGE,
    val scoreRangeBottomValue: Int? = null,
    val scoreRangeTopValue: Int? = null,
    val filterIgnored: Boolean = false,
) {
    companion object {
        val DEFAULT_CLEAR_TYPE_RANGE = 0 until ClearType.entries.size
        val DEFAULT_DIFFICULTY_NUMBER_RANGE = 1..HIGHEST_DIFFICULTY
    }
}
