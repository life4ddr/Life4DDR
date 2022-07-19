package com.perrigogames.life4.db

data class ChartResultPair(
    val chart: DetailedChartInfo,
    val result: ChartResult,
)

fun DetailedChartInfo.matches(other: ChartResult?) = when (other) {
    null -> false
    else -> id == other.chartId
}

fun ChartResult.matches(other: DetailedChartInfo?) = other?.matches(this) ?: false