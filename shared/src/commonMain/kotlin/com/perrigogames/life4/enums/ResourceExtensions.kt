package com.perrigogames.life4.enums

import com.perrigogames.life4.MR

val ClearType.colorRes get() = when(this) {
    ClearType.NO_PLAY -> MR.colors.no_play
    ClearType.FAIL -> MR.colors.fail
    ClearType.CLEAR -> MR.colors.clear
    ClearType.LIFE4_CLEAR -> MR.colors.life4
    ClearType.GOOD_FULL_COMBO -> MR.colors.good
    ClearType.GREAT_FULL_COMBO -> MR.colors.great
    ClearType.PERFECT_FULL_COMBO -> MR.colors.perfect
    ClearType.MARVELOUS_FULL_COMBO -> MR.colors.marvelous
}

val DifficultyClass.colorRes get() = when(this) {
    DifficultyClass.BEGINNER -> MR.colors.difficultyBeginner
    DifficultyClass.BASIC -> MR.colors.difficultyBasic
    DifficultyClass.DIFFICULT -> MR.colors.difficultyDifficult
    DifficultyClass.EXPERT -> MR.colors.difficultyExpert
    DifficultyClass.CHALLENGE -> MR.colors.difficultyChallenge
}

val LadderRankClass.colorRes get() = when(this) {
    LadderRankClass.COPPER -> MR.colors.copper
    LadderRankClass.BRONZE -> MR.colors.bronze
    LadderRankClass.SILVER -> MR.colors.silver
    LadderRankClass.GOLD -> MR.colors.gold
    LadderRankClass.PLATINUM -> MR.colors.platinum
    LadderRankClass.DIAMOND -> MR.colors.diamond
    LadderRankClass.COBALT -> MR.colors.cobalt
    LadderRankClass.PEARL -> MR.colors.pearl
    LadderRankClass.AMETHYST -> MR.colors.amethyst
    LadderRankClass.EMERALD -> MR.colors.emerald
    LadderRankClass.ONYX -> MR.colors.onyx
}
