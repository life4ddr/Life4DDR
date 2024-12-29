package com.perrigogames.life4.feature.songresults

import com.perrigogames.life4.GameConstants.HIGHEST_DIFFICULTY
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle

data class FilterState(
    val selectedPlayStyle: PlayStyle = PlayStyle.SINGLE,
    val difficultyClassSelection: Map<PlayStyle, Map<DifficultyClass, Boolean>> = emptyMap(),
    val difficultyNumberRange: IntRange = 1..HIGHEST_DIFFICULTY,
    val clearTypeRange: IntRange = 0 until ClearType.entries.size,
    val scoreRangeBottomValue: Int? = null,
    val scoreRangeTopValue: Int? = null,
) {

    fun isDifficultyClassSelected(style: PlayStyle, difficultyClass: DifficultyClass): Boolean {
        return difficultyClassSelection[style]?.let { styleMap ->
            styleMap[difficultyClass] ?: false
        } ?: false
    }
}
