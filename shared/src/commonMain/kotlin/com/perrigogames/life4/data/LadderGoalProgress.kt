package com.perrigogames.life4.data

import com.perrigogames.life4.db.ChartResult
import com.perrigogames.life4.db.ChartResultPair
import com.perrigogames.life4.enums.ClearType

/**
 * Data class representing the local user's current progress towards
 * a particular goal
 */
class LadderGoalProgress(
    val progress: Int,
    val max: Int,
    val showMax: Boolean = true,
    val results: List<ChartResultPair>? = null
) {

    fun isComplete() = progress == max && max > 0
}

fun ChartResult.satisfiesClear(type: ClearType) = clearType.ordinal >= type.ordinal
