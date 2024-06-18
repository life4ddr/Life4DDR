package com.perrigogames.life4.feature.songresults

import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle

data class ScoreListContentConfig(
    val playStyle: PlayStyle? = PlayStyle.SINGLE, // TODO Doubles support
    val difficultyClasses: List<DifficultyClass>? = null,
    val difficultyNumbers: List<Long>? = null,
    val clearTypes: List<ClearType>? = null,
    val minScore: Long? = null,
    val maxScore: Long? = null,
)
