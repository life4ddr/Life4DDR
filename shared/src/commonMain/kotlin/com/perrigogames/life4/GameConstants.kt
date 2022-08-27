package com.perrigogames.life4

object GameConstants {

    const val HIGHEST_DIFFICULTY = 19
    const val TRIAL_LENGTH = 4
    const val MAX_SCORE = 1_000_000
    const val SCORE_PENALTY_PERFECT = 10
    const val AAA_SCORE = 990_000

    fun mfcPointsForDifficulty(difficulty: Int): Double = when(difficulty) {
        1 -> 0.1
        2, 3 -> 0.25
        4, 5, 6 -> 0.5
        7, 8, 9 -> 1.0
        10 -> 1.5
        11 -> 2.0
        12 -> 4.0
        13 -> 6.0
        14 -> 8.0
        15 -> 15.0
        16, 17, 18, 19, 20 -> 25.0
        else -> 0.0
    }
}
