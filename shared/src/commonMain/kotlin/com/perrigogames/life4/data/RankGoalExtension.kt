package com.perrigogames.life4.data

import com.perrigogames.life4.enums.DifficultyClass

class DifficultyClassSet(
    val set: List<DifficultyClass>,
    val requireAll: Boolean,
) {

    companion object {

        fun parse(sourceString: String): DifficultyClassSet {
            var requireAll = false
            val difficulties = sourceString.mapNotNull { ch ->
                when (ch) {
                    'b' -> DifficultyClass.BEGINNER
                    'B' -> DifficultyClass.BASIC
                    'D' -> DifficultyClass.DIFFICULT
                    'E' -> DifficultyClass.EXPERT
                    'C' -> DifficultyClass.CHALLENGE
                    '*' -> {
                        requireAll = true
                        null
                    }
                    else -> throw Exception("Illegal difficulty string character ($ch)")
                }
            }
            return DifficultyClassSet(difficulties, requireAll)
        }
    }
}
