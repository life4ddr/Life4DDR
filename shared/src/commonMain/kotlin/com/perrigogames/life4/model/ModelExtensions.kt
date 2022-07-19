package com.perrigogames.life4.model

import com.perrigogames.life4.db.ChartResult
import com.perrigogames.life4.db.DetailedChartInfo

fun DetailedChartInfo.toStringExt() = "$title ${difficultyClass.aggregateString(playStyle)} ($difficultyNumber)}"

fun ChartResult.toStringExt() = "$score - $clearType"