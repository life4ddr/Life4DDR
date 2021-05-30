package com.perrigogames.life4

object GameConstants {

    fun mfcPointsForDifficulty(difficulty: Int) = when(difficulty) {
        8, 9, 10 -> 1
        11, 12 -> 2
        13 -> 4
        14 -> 8
        15 -> 15
        16, 17, 18, 19, 20 -> 25
        else -> 0
    }
}
