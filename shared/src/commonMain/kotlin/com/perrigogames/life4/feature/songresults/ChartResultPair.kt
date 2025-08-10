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
            1 -> 100 to 10
            2, 3 -> 250 to 25
            4, 5, 6 -> 500 to 50
            7, 8, 9 -> 1000 to 100
            10 -> 1500 to 150
            11 -> 2000 to 200
            12 -> 4000 to 400
            13 -> 6000 to 60
            14 -> 8000 to 80
            15 -> 15000 to 1500
            16, 17, 18, 19, 20 -> 25000 to 2500
            else -> 0 to 0
        }
        return when (result?.clearType) {
            ClearType.MARVELOUS_FULL_COMBO -> points.first
            ClearType.SINGLE_DIGIT_PERFECTS -> points.second
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
