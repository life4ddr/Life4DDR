package com.perrigogames.life4.model

import com.perrigogames.life4.db.ChartResult
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.feature.songlist.Chart

fun Chart.toStringExt() = "${song.title} ${difficultyClass.aggregateString(playStyle)} ($difficultyNumber)}"

fun ChartResult.toStringExt() = "$score - $clearType"

val ChartResult?.safeScore
    get() = this?.score ?: 0

val ChartResult?.safeClear
    get() = this?.clearType ?: ClearType.NO_PLAY