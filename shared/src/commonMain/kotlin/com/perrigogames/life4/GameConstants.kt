package com.perrigogames.life4

object GameConstants {

    fun mfcPointsForDifficulty(difficulty: Int) = when(difficulty) {
        1 -> 0.1f
        2, 3 -> 0.25f
        4, 5, 6 -> 0.5f
        7, 8, 9 -> 1
        10 -> 1.5f
        11 -> 2
        12 -> 4
        13 -> 6
        14 -> 8
        15 -> 15
        16, 17, 18, 19, 20 -> 25
        else -> 0
    }
}
