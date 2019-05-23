package com.perrigogames.life4trials.data

/**
 * Enum to describe the possible ways to finish a song, and consequently an
 * entire folder.
 */
enum class ClearType(val passing: Boolean = true) {
    FAIL(false),
    CLEAR,
    LIFE4_CLEAR,
    GOOD_FULL_COMBO,
    GREAT_FULL_COMBO,
    PERFECT_FULL_COMBO,
    MARVELOUS_FULL_COMBO
}