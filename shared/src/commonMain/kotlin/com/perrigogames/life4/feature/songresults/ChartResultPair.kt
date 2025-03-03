package com.perrigogames.life4.feature.songresults

import com.perrigogames.life4.db.ChartResult
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.feature.songlist.Chart

data class ChartResultPair(
    val chart: Chart,
    val result: ChartResult?,
) {

    fun maPointsForDifficulty(): Double {
        val points = when (chart.difficultyNumber) { // first = MFC, 2nd = SDP
            1 -> 0.1 to 0.01
            2, 3 -> 0.25 to 0.025
            4, 5, 6 -> 0.5 to 0.05
            7, 8, 9 -> 1.0 to 0.1
            10 -> 1.5 to 0.15
            11 -> 2.0 to 0.2
            12 -> 4.0 to 0.4
            13 -> 6.0 to 0.6
            14 -> 8.0 to 0.8
            15 -> 15.0 to 1.5
            16, 17, 18, 19, 20 -> 25.0 to 2.5
            else -> 0.0 to 0.0
        }
        return if (result?.clearType == ClearType.MARVELOUS_FULL_COMBO) {
            points.first
        } else if (result?.isSdp() == true) {
            points.second
        } else {
            0.0
        }
    }
}

fun Chart.matches(other: ChartResult?) = when (other) {
    null -> false
    else -> song.skillId == other.skillId
            && difficultyClass == other.difficultyClass
            && playStyle == other.playStyle
}

fun ChartResult.matches(other: Chart?) = other?.matches(this) ?: false

fun ChartResult.isSdp() = score > 999_900 // 10pts/Perfect, must be > 10 Perfects