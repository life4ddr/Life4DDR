package com.perrigogames.life4.feature.songresults

import com.perrigogames.life4.db.ChartResult
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.feature.songlist.Chart

data class ChartResultPair(
    val chart: Chart,
    val result: ChartResult?,
) {

    fun maPointsThousandths(): Int {
        val points = when (chart.difficultyNumber) { // first = MFC, 2nd = SDP
            1 -> 100
            2, 3 -> 250
            4, 5, 6 -> 500
            7, 8, 9 -> 1000
            10 -> 1500
            11 -> 2000
            12 -> 4000
            13 -> 6000
            14 -> 8000
            15 -> 15000
            16, 17, 18, 19, 20 -> 25000
            else -> 0
        }
        return when (result?.clearType) {
            ClearType.MARVELOUS_FULL_COMBO -> points
            ClearType.SINGLE_DIGIT_PERFECTS -> points / 10
            else -> 0
        }
    }

    fun maPoints(): Double = maPointsThousandths().toMAPointsDouble()
}

fun Int.toMAPointsDouble(): Double = this / 1000.0

fun Chart.matches(other: ChartResult?) = when (other) {
    null -> false
    else -> song.skillId == other.skillId
            && difficultyClass == other.difficultyClass
            && playStyle == other.playStyle
}

fun ChartResult.matches(other: Chart?) = other?.matches(this) ?: false
