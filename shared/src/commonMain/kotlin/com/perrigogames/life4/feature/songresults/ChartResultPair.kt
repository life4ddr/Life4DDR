package com.perrigogames.life4.feature.songresults

import com.perrigogames.life4.db.ChartResult
import com.perrigogames.life4.db.DetailedChartInfo

data class ChartResultPair(
    val chart: DetailedChartInfo,
    val result: ChartResult?,
)

fun DetailedChartInfo.matches(other: ChartResult?) =
    when (other) {
        null -> false
        else -> id == other.chartId
    }

fun ChartResult.matches(other: DetailedChartInfo?) = other?.matches(this) ?: false
