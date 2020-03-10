package com.perrigogames.life4trials.util

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.perrigogames.life4.data.*
import com.perrigogames.life4trials.R

val LadderRank.nameRes @StringRes get() = when(this) {
    LadderRank.WOOD1 -> R.drawable.wood_1
    LadderRank.WOOD2 -> R.drawable.wood_2
    LadderRank.WOOD3 -> R.drawable.wood_3
    LadderRank.BRONZE1 -> R.drawable.bronze_1
    LadderRank.BRONZE2 -> R.drawable.bronze_2
    LadderRank.BRONZE3 -> R.drawable.bronze_3
    LadderRank.SILVER1 -> R.drawable.silver_1
    LadderRank.SILVER2 -> R.drawable.silver_2
    LadderRank.SILVER3 -> R.drawable.silver_3
    LadderRank.GOLD1 -> R.drawable.gold_1
    LadderRank.GOLD2 -> R.drawable.gold_2
    LadderRank.GOLD3 -> R.drawable.gold_3
    LadderRank.PLATINUM1 -> R.drawable.platinum_1
    LadderRank.PLATINUM2 -> R.drawable.platinum_2
    LadderRank.PLATINUM3 -> R.drawable.platinum_3
    LadderRank.DIAMOND1 -> R.drawable.diamond_1
    LadderRank.DIAMOND2 -> R.drawable.diamond_2
    LadderRank.DIAMOND3 -> R.drawable.diamond_3
    LadderRank.COBALT1 -> R.drawable.cobalt_1
    LadderRank.COBALT2 -> R.drawable.cobalt_2
    LadderRank.COBALT3 -> R.drawable.cobalt_3
    LadderRank.AMETHYST1 -> R.drawable.amethyst_1
    LadderRank.AMETHYST2 -> R.drawable.amethyst_2
    LadderRank.AMETHYST3 -> R.drawable.amethyst_3
    LadderRank.EMERALD1 -> R.drawable.emerald_1
    LadderRank.EMERALD2 -> R.drawable.emerald_2
    LadderRank.EMERALD3 -> R.drawable.emerald_3
}

val LadderRank.drawableRes @DrawableRes get() = when(this) {
    LadderRank.WOOD1 -> R.drawable.wood_1
    LadderRank.WOOD2 -> R.drawable.wood_2
    LadderRank.WOOD3 -> R.drawable.wood_3
    LadderRank.BRONZE1 -> R.drawable.bronze_1
    LadderRank.BRONZE2 -> R.drawable.bronze_2
    LadderRank.BRONZE3 -> R.drawable.bronze_3
    LadderRank.SILVER1 -> R.drawable.silver_1
    LadderRank.SILVER2 -> R.drawable.silver_2
    LadderRank.SILVER3 -> R.drawable.silver_3
    LadderRank.GOLD1 -> R.drawable.gold_1
    LadderRank.GOLD2 -> R.drawable.gold_2
    LadderRank.GOLD3 -> R.drawable.gold_3
    LadderRank.PLATINUM1 -> R.drawable.platinum_1
    LadderRank.PLATINUM2 -> R.drawable.platinum_2
    LadderRank.PLATINUM3 -> R.drawable.platinum_3
    LadderRank.DIAMOND1 -> R.drawable.diamond_1
    LadderRank.DIAMOND2 -> R.drawable.diamond_2
    LadderRank.DIAMOND3 -> R.drawable.diamond_3
    LadderRank.COBALT1 -> R.drawable.cobalt_1
    LadderRank.COBALT2 -> R.drawable.cobalt_2
    LadderRank.COBALT3 -> R.drawable.cobalt_3
    LadderRank.AMETHYST1 -> R.drawable.amethyst_1
    LadderRank.AMETHYST2 -> R.drawable.amethyst_2
    LadderRank.AMETHYST3 -> R.drawable.amethyst_3
    LadderRank.EMERALD1 -> R.drawable.emerald_1
    LadderRank.EMERALD2 -> R.drawable.emerald_2
    LadderRank.EMERALD3 -> R.drawable.emerald_3
}

val LadderRank.groupNameRes @StringRes get() = group.nameRes
val LadderRank.colorRes @ColorRes get() = group.colorRes

val LadderRankClass.nameRes @StringRes get() = when(this) {
    LadderRankClass.WOOD -> R.string.wood
    LadderRankClass.BRONZE -> R.string.bronze
    LadderRankClass.SILVER -> R.string.silver
    LadderRankClass.GOLD -> R.string.gold
    LadderRankClass.PLATINUM -> R.string.platinum
    LadderRankClass.DIAMOND -> R.string.diamond
    LadderRankClass.COBALT -> R.string.cobalt
    LadderRankClass.AMETHYST -> R.string.amethyst
    LadderRankClass.EMERALD -> R.string.emerald
}

val LadderRankClass.colorRes @ColorRes get() = when(this) {
    LadderRankClass.WOOD -> R.color.wood
    LadderRankClass.BRONZE -> R.color.bronze
    LadderRankClass.SILVER -> R.color.silver
    LadderRankClass.GOLD -> R.color.gold
    LadderRankClass.PLATINUM -> R.color.platinum
    LadderRankClass.DIAMOND -> R.color.diamond
    LadderRankClass.COBALT -> R.color.cobalt
    LadderRankClass.AMETHYST -> R.color.amethyst
    LadderRankClass.EMERALD -> R.color.emerald
}

val TrialRank.nameRes @StringRes get() = when(this) {
    TrialRank.WOOD -> R.string.wood
    TrialRank.BRONZE -> R.string.bronze
    TrialRank.SILVER -> R.string.silver
    TrialRank.GOLD -> R.string.gold
    TrialRank.PLATINUM -> R.string.platinum
    TrialRank.DIAMOND -> R.string.diamond
    TrialRank.COBALT -> R.string.cobalt
    TrialRank.AMETHYST -> R.string.amethyst
    TrialRank.EMERALD -> R.string.emerald
}

val TrialRank.drawableRes @DrawableRes get() = when(this) {
    TrialRank.WOOD -> R.drawable.wood_3
    TrialRank.BRONZE -> R.drawable.bronze_3
    TrialRank.SILVER -> R.drawable.silver_3
    TrialRank.GOLD -> R.drawable.gold_3
    TrialRank.PLATINUM -> R.drawable.platinum_3
    TrialRank.DIAMOND -> R.drawable.diamond_3
    TrialRank.COBALT -> R.drawable.cobalt_3
    TrialRank.AMETHYST -> R.drawable.amethyst_3
    TrialRank.EMERALD -> R.drawable.emerald_3
}

val TrialRank.colorRes @ColorRes get() = parent.colorRes

val PlacementRank.nameRes @StringRes get() = parent.nameRes
val PlacementRank.colorRes @ColorRes get() = parent.colorRes

val DifficultyClass.colorRes @ColorRes get() = when(this) {
    DifficultyClass.BEGINNER -> R.color.difficultyBeginner
    DifficultyClass.BASIC -> R.color.difficultyBasic
    DifficultyClass.DIFFICULT -> R.color.difficultyDifficult
    DifficultyClass.EXPERT -> R.color.difficultyExpert
    DifficultyClass.CHALLENGE -> R.color.difficultyChallenge
}

val DifficultyClass.nameRes @StringRes get() = when(this) {
    DifficultyClass.BEGINNER -> R.string.bgsp
    DifficultyClass.BASIC -> R.string.bsp
    DifficultyClass.DIFFICULT -> R.string.dsp
    DifficultyClass.EXPERT -> R.string.esp
    DifficultyClass.CHALLENGE -> R.string.csp
}

val DifficultyClass.abbreviationRes @StringRes get() = when(this) {
    DifficultyClass.BEGINNER -> R.string.bgsp
    DifficultyClass.BASIC -> R.string.bsp
    DifficultyClass.DIFFICULT -> R.string.dsp
    DifficultyClass.EXPERT -> R.string.esp
    DifficultyClass.CHALLENGE -> R.string.csp
}

val ClearType.colorRes @ColorRes get() = when(this) {
    ClearType.NO_PLAY -> R.color.no_play
    ClearType.FAIL -> R.color.fail
    ClearType.CLEAR -> R.color.clear
    ClearType.LIFE4_CLEAR -> R.color.life4
    ClearType.GOOD_FULL_COMBO -> R.color.good
    ClearType.GREAT_FULL_COMBO -> R.color.great
    ClearType.PERFECT_FULL_COMBO -> R.color.perfect
    ClearType.MARVELOUS_FULL_COMBO -> R.color.marvelous
}

val ClearType.lampRes @StringRes get() = when(this) {
    ClearType.NO_PLAY -> R.string.not_played
    ClearType.FAIL -> R.string.fail
    ClearType.CLEAR -> R.string.lamp_clear
    ClearType.LIFE4_CLEAR -> R.string.lamp_life4
    ClearType.GOOD_FULL_COMBO -> R.string.lamp_fc
    ClearType.GREAT_FULL_COMBO -> R.string.lamp_gfc
    ClearType.PERFECT_FULL_COMBO -> R.string.lamp_pfc
    ClearType.MARVELOUS_FULL_COMBO -> R.string.lamp_mfc
}

val ClearType.clearRes @StringRes get() = when(this) {
    ClearType.NO_PLAY -> R.string.not_played
    ClearType.FAIL -> R.string.fail
    ClearType.CLEAR -> R.string.clear
    ClearType.LIFE4_CLEAR -> R.string.clear_life4
    ClearType.GOOD_FULL_COMBO -> R.string.clear_fc
    ClearType.GREAT_FULL_COMBO -> R.string.clear_gfc
    ClearType.PERFECT_FULL_COMBO -> R.string.clear_pfc
    ClearType.MARVELOUS_FULL_COMBO -> R.string.clear_mfc
}

val ClearType.clearResShort @StringRes get() = when(this) {
    ClearType.GOOD_FULL_COMBO -> R.string.clear_fc_short
    ClearType.GREAT_FULL_COMBO -> R.string.clear_gfc_short
    ClearType.PERFECT_FULL_COMBO -> R.string.clear_pfc_short
    ClearType.MARVELOUS_FULL_COMBO -> R.string.clear_mfc_short
    else -> clearRes
}
