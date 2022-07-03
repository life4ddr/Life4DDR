package com.perrigogames.life4.data

import com.perrigogames.life4.db.ChartResult
import com.perrigogames.life4.db.ChartResultPair
import com.perrigogames.life4.enums.ClearType

/**
 * Data class representing the local user's current progress towards
 * a particular goal
 */
class LadderGoalProgress(
    val progress: Double,
    val max: Double,
    val showMax: Boolean = true,
    val results: List<ChartResultPair>? = null,
) {

    constructor(
        progress: Int,
        max: Int,
        showMax: Boolean = true,
        results: List<ChartResultPair>? = null,
    ) : this(progress.toDouble(), max.toDouble(), showMax, results)

    fun isComplete() = progress == max && max > 0
}

fun ChartResult.satisfiesClear(type: ClearType) = clearType.ordinal >= type.ordinal
