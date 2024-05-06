package com.perrigogames.life4.model

import com.perrigogames.life4.db.ChartResult
import com.perrigogames.life4.db.DetailedChartInfo
import com.perrigogames.life4.enums.ClearType

fun DetailedChartInfo.toStringExt() = "$title ${difficultyClass.aggregateString(playStyle)} ($difficultyNumber)}"

fun ChartResult.toStringExt() = "$score - $clearType"

val ChartResult?.safeScore
    get() = this?.score ?: 0

val ChartResult?.safeClear
    get() = this?.clearType ?: ClearType.NO_PLAY
