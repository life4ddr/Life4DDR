package com.perrigogames.life4.feature.songresults

import com.perrigogames.life4.db.ChartResult
import com.perrigogames.life4.feature.songlist.Chart

data class ChartResultPair(
    val chart: Chart,
    val result: ChartResult?,
)

fun Chart.matches(other: ChartResult?) = when (other) {
    null -> false
    else -> song.skillId == other.skillId
            && difficultyClass == other.difficultyClass
            && playStyle == other.playStyle
}

fun ChartResult.matches(other: Chart?) = other?.matches(this) ?: false