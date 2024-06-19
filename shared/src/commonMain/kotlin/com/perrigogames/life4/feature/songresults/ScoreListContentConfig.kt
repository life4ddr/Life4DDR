package com.perrigogames.life4.feature.songresults

import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle

data class ScoreListContentConfig(
    val playStyle: PlayStyle? = PlayStyle.SINGLE,
    val difficultyClasses: List<DifficultyClass>? = null,
    val difficultyNumbers: IntRange? = null,
    val clearTypes: List<ClearType>? = null,
    val minScore: Long? = null,
    val maxScore: Long? = null,
    val filterIgnored: Boolean = false,
)
