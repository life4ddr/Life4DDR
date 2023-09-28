package com.perrigogames.life4trials.data

class TrialProgress(private val session: TrialSession) {

    ///
    /// Score Methods
    ///

    /** Finds the goal set for the current goal rank */
    val currentGoalSet get() = session.trial.goals!!.first { it.rank == session.goalRank!! }

    /** Finds the goal set for the current goal rank */
    val currentSongScoreScale get() = currentGoalSet.score

//    val currentSongScoreScaleProgress get() =


    ///
    /// Step Judgment Methods
    ///


    ///
    /// EX Methods
    ///

    val projectedEx: Int
        get() {
            val projectedMaxPercent = session.currentTotalExScore.toDouble() / session.currentMaxExScore.toDouble()
            return (session.trial.total_ex!! * projectedMaxPercent).toInt()
        }

    ///
    ///
    ///
}