package com.perrigogames.life4.data

/**
 * Data class representing the local user's current progress towards
 * a particular goal
 */
class LadderGoalProgress(val progress: Int,
                         val max: Int,
                         val showMax: Boolean = true,
                         val results: List<ILadderResult>? = null) {

    fun isComplete() = progress == max && max > 0
}

interface ILadderResult
