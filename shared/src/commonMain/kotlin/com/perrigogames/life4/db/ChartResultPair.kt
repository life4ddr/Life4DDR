package com.perrigogames.life4.db

data class ChartResultPair(
    val chart: DetailedChartInfo,
    val result: ChartResult,
)

fun DetailedChartInfo.matches(other: ChartResult?) = when (other) {
    null -> false
    else -> skillId == other.skillId &&
            playStyle == other.playStyle &&
            difficultyClass == other.difficultyClass
}

fun ChartResult.matches(other: DetailedChartInfo?) = other?.matches(this) ?: false