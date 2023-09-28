package com.perrigogames.life4trials.data

import com.perrigogames.life4trials.db.LadderResultDB

/**
 * Data class representing the local user's current progress towards
 * a particular goal
 */
class LadderGoalProgress(val progress: Int,
                         val max: Int,
                         val results: List<LadderResultDB>? = null) {

    fun isComplete() = progress == max && max > 0
}