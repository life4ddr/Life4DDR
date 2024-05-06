package com.perrigogames.life4.data

data class TrialEXProgress(
    val currentExScore: Int,
    val currentMaxExScore: Int,
    val maxExScore: Int,
) {
    /**
     * The percentage of the current obtained EX
     */
    val currentExPercent = currentExScore.toFloat() / maxExScore

    /**
     * The percentage of the current maximum EX
     */
    val currentMaxExPercent = currentMaxExScore.toFloat() / maxExScore

    /**
     * The number of EX that hasn't been obtained yet from unplayed only
     */
    val remainingPotentialExScore = currentMaxExScore - currentExScore

    /**
     * The number of EX that hasn't been obtained yet, counting both played and unplayed
     */
    val missingExScore = maxExScore - currentExScore

    /**
     * The number of EX that have been permanently lost for this session
     */
    val lostExScore = maxExScore - currentMaxExScore
}
