package com.perrigogames.life4.feature.songresults

import com.perrigogames.life4.GameConstants.HIGHEST_DIFFICULTY
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle

data class FilterState(
    val selectedPlayStyle: PlayStyle = PlayStyle.SINGLE,
    val difficultyClassSelection: List<DifficultyClass> = DifficultyClass.entries,
    val difficultyNumberRange: IntRange = 1..HIGHEST_DIFFICULTY,
    val clearTypeRange: IntRange = 0 until ClearType.entries.size,
    val scoreRangeBottomValue: Int? = null,
    val scoreRangeTopValue: Int? = null,
)
