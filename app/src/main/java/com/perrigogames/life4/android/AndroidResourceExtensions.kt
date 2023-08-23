package com.perrigogames.life4.android

import android.content.res.Resources
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.perrigogames.life4.data.PlacementRank
import com.perrigogames.life4.data.RankGoalUserType
import com.perrigogames.life4.enums.*

val LadderRank.nameRes @StringRes get() = when(this) {
    LadderRank.COPPER1 -> R.string.copper_1
    LadderRank.COPPER2 -> R.string.copper_2
    LadderRank.COPPER3 -> R.string.copper_3
    LadderRank.COPPER4 -> R.string.copper_4
    LadderRank.COPPER5 -> R.string.copper_5
    LadderRank.BRONZE1 -> R.string.bronze_1
    LadderRank.BRONZE2 -> R.string.bronze_2
    LadderRank.BRONZE3 -> R.string.bronze_3
    LadderRank.BRONZE4 -> R.string.bronze_4
    LadderRank.BRONZE5 -> R.string.bronze_5
    LadderRank.SILVER1 -> R.string.silver_1
    LadderRank.SILVER2 -> R.string.silver_2
    LadderRank.SILVER3 -> R.string.silver_3
    LadderRank.SILVER4 -> R.string.silver_4
    LadderRank.SILVER5 -> R.string.silver_5
    LadderRank.GOLD1 -> R.string.gold_1
    LadderRank.GOLD2 -> R.string.gold_2
    LadderRank.GOLD3 -> R.string.gold_3
    LadderRank.GOLD4 -> R.string.gold_4
    LadderRank.GOLD5 -> R.string.gold_5
    LadderRank.PLATINUM1 -> R.string.platinum_1
    LadderRank.PLATINUM2 -> R.string.platinum_2
    LadderRank.PLATINUM3 -> R.string.platinum_3
    LadderRank.PLATINUM4 -> R.string.platinum_4
    LadderRank.PLATINUM5 -> R.string.platinum_5
    LadderRank.DIAMOND1 -> R.string.diamond_1
    LadderRank.DIAMOND2 -> R.string.diamond_2
    LadderRank.DIAMOND3 -> R.string.diamond_3
    LadderRank.DIAMOND4 -> R.string.diamond_4
    LadderRank.DIAMOND5 -> R.string.diamond_5
    LadderRank.COBALT1 -> R.string.cobalt_1
    LadderRank.COBALT2 -> R.string.cobalt_2
    LadderRank.COBALT3 -> R.string.cobalt_3
    LadderRank.COBALT4 -> R.string.cobalt_4
    LadderRank.COBALT5 -> R.string.cobalt_5
    LadderRank.PEARL1 -> R.string.pearl_1
    LadderRank.PEARL2 -> R.string.pearl_2
    LadderRank.PEARL3 -> R.string.pearl_3
    LadderRank.PEARL4 -> R.string.pearl_4
    LadderRank.PEARL5 -> R.string.pearl_5
    LadderRank.AMETHYST1 -> R.string.amethyst_1
    LadderRank.AMETHYST2 -> R.string.amethyst_2
    LadderRank.AMETHYST3 -> R.string.amethyst_3
    LadderRank.AMETHYST4 -> R.string.amethyst_4
    LadderRank.AMETHYST5 -> R.string.amethyst_5
    LadderRank.EMERALD1 -> R.string.emerald_1
    LadderRank.EMERALD2 -> R.string.emerald_2
    LadderRank.EMERALD3 -> R.string.emerald_3
    LadderRank.EMERALD4 -> R.string.emerald_4
    LadderRank.EMERALD5 -> R.string.emerald_5
    LadderRank.ONYX1 -> R.string.onyx_1
    LadderRank.ONYX2 -> R.string.onyx_2
    LadderRank.ONYX3 -> R.string.onyx_3
    LadderRank.ONYX4 -> R.string.onyx_4
    LadderRank.ONYX5 -> R.string.onyx_5
}

val LadderRank.categoryNameRes @StringRes get() = when(this) {
    LadderRank.COPPER1,
    LadderRank.BRONZE1,
    LadderRank.SILVER1,
    LadderRank.GOLD1,
    LadderRank.PLATINUM1,
    LadderRank.DIAMOND1,
    LadderRank.COBALT1,
    LadderRank.PEARL1,
    LadderRank.AMETHYST1,
    LadderRank.EMERALD1,
    LadderRank.ONYX1 -> R.string.roman_1
    LadderRank.COPPER2,
    LadderRank.BRONZE2,
    LadderRank.SILVER2,
    LadderRank.GOLD2,
    LadderRank.PLATINUM2,
    LadderRank.DIAMOND2,
    LadderRank.COBALT2,
    LadderRank.PEARL2,
    LadderRank.AMETHYST2,
    LadderRank.EMERALD2,
    LadderRank.ONYX2 -> R.string.roman_2
    LadderRank.COPPER3,
    LadderRank.BRONZE3,
    LadderRank.SILVER3,
    LadderRank.GOLD3,
    LadderRank.PLATINUM3,
    LadderRank.DIAMOND3,
    LadderRank.COBALT3,
    LadderRank.PEARL3,
    LadderRank.AMETHYST3,
    LadderRank.EMERALD3,
    LadderRank.ONYX3 -> R.string.roman_3
    LadderRank.COPPER4,
    LadderRank.BRONZE4,
    LadderRank.SILVER4,
    LadderRank.GOLD4,
    LadderRank.PLATINUM4,
    LadderRank.DIAMOND4,
    LadderRank.COBALT4,
    LadderRank.PEARL4,
    LadderRank.AMETHYST4,
    LadderRank.EMERALD4,
    LadderRank.ONYX4 -> R.string.roman_4
    LadderRank.COPPER5,
    LadderRank.BRONZE5,
    LadderRank.SILVER5,
    LadderRank.GOLD5,
    LadderRank.PLATINUM5,
    LadderRank.DIAMOND5,
    LadderRank.COBALT5,
    LadderRank.PEARL5,
    LadderRank.AMETHYST5,
    LadderRank.EMERALD5,
    LadderRank.ONYX5 -> R.string.roman_5
}

val LadderRank.drawableRes @DrawableRes get() = when(this) {
    LadderRank.COPPER1 -> R.drawable.copper_1
    LadderRank.COPPER2 -> R.drawable.copper_2
    LadderRank.COPPER3 -> R.drawable.copper_3
    LadderRank.COPPER4 -> R.drawable.copper_4
    LadderRank.COPPER5 -> R.drawable.copper_5
    LadderRank.BRONZE1 -> R.drawable.bronze_1
    LadderRank.BRONZE2 -> R.drawable.bronze_2
    LadderRank.BRONZE3 -> R.drawable.bronze_3
    LadderRank.BRONZE4 -> R.drawable.bronze_4
    LadderRank.BRONZE5 -> R.drawable.bronze_5
    LadderRank.SILVER1 -> R.drawable.silver_1
    LadderRank.SILVER2 -> R.drawable.silver_2
    LadderRank.SILVER3 -> R.drawable.silver_3
    LadderRank.SILVER4 -> R.drawable.silver_4
    LadderRank.SILVER5 -> R.drawable.silver_5
    LadderRank.GOLD1 -> R.drawable.gold_1
    LadderRank.GOLD2 -> R.drawable.gold_2
    LadderRank.GOLD3 -> R.drawable.gold_3
    LadderRank.GOLD4 -> R.drawable.gold_4
    LadderRank.GOLD5 -> R.drawable.gold_5
    LadderRank.PLATINUM1 -> R.drawable.platinum_1
    LadderRank.PLATINUM2 -> R.drawable.platinum_2
    LadderRank.PLATINUM3 -> R.drawable.platinum_3
    LadderRank.PLATINUM4 -> R.drawable.platinum_4
    LadderRank.PLATINUM5 -> R.drawable.platinum_5
    LadderRank.DIAMOND1 -> R.drawable.diamond_1
    LadderRank.DIAMOND2 -> R.drawable.diamond_2
    LadderRank.DIAMOND3 -> R.drawable.diamond_3
    LadderRank.DIAMOND4 -> R.drawable.diamond_4
    LadderRank.DIAMOND5 -> R.drawable.diamond_5
    LadderRank.COBALT1 -> R.drawable.cobalt_1
    LadderRank.COBALT2 -> R.drawable.cobalt_2
    LadderRank.COBALT3 -> R.drawable.cobalt_3
    LadderRank.COBALT4 -> R.drawable.cobalt_4
    LadderRank.COBALT5 -> R.drawable.cobalt_5
    LadderRank.PEARL1 -> R.drawable.pearl_1
    LadderRank.PEARL2 -> R.drawable.pearl_2
    LadderRank.PEARL3 -> R.drawable.pearl_3
    LadderRank.PEARL4 -> R.drawable.pearl_4
    LadderRank.PEARL5 -> R.drawable.pearl_5
    LadderRank.AMETHYST1 -> R.drawable.amethyst_1
    LadderRank.AMETHYST2 -> R.drawable.amethyst_2
    LadderRank.AMETHYST3 -> R.drawable.amethyst_3
    LadderRank.AMETHYST4 -> R.drawable.amethyst_4
    LadderRank.AMETHYST5 -> R.drawable.amethyst_5
    LadderRank.EMERALD1 -> R.drawable.emerald_1
    LadderRank.EMERALD2 -> R.drawable.emerald_2
    LadderRank.EMERALD3 -> R.drawable.emerald_3
    LadderRank.EMERALD4 -> R.drawable.emerald_4
    LadderRank.EMERALD5 -> R.drawable.emerald_5
    LadderRank.ONYX1 -> R.drawable.onyx_1
    LadderRank.ONYX2 -> R.drawable.onyx_2
    LadderRank.ONYX3 -> R.drawable.onyx_3
    LadderRank.ONYX4 -> R.drawable.onyx_4
    LadderRank.ONYX5 -> R.drawable.onyx_5
}

val LadderRank.groupNameRes @StringRes get() = group.nameRes
@Deprecated("Migrate to moko resources", replaceWith = ReplaceWith("MR.colors"))
val LadderRank.colorRes @ColorRes get() = group.colorRes

val LadderRankClass.nameRes @StringRes get() = when(this) {
    LadderRankClass.COPPER -> R.string.copper
    LadderRankClass.BRONZE -> R.string.bronze
    LadderRankClass.SILVER -> R.string.silver
    LadderRankClass.GOLD -> R.string.gold
    LadderRankClass.PLATINUM -> R.string.platinum
    LadderRankClass.DIAMOND -> R.string.diamond
    LadderRankClass.COBALT -> R.string.cobalt
    LadderRankClass.PEARL -> R.string.pearl
    LadderRankClass.AMETHYST -> R.string.amethyst
    LadderRankClass.EMERALD -> R.string.emerald
    LadderRankClass.ONYX -> R.string.onyx
}

@Deprecated("Migrate to moko resources", replaceWith = ReplaceWith("MR.colors"))
val LadderRankClass.colorRes @ColorRes get() = when(this) {
    LadderRankClass.COPPER -> R.color.copper
    LadderRankClass.BRONZE -> R.color.bronze
    LadderRankClass.SILVER -> R.color.silver
    LadderRankClass.GOLD -> R.color.gold
    LadderRankClass.PLATINUM -> R.color.platinum
    LadderRankClass.DIAMOND -> R.color.diamond
    LadderRankClass.COBALT -> R.color.cobalt
    LadderRankClass.PEARL -> R.color.pearl
    LadderRankClass.AMETHYST -> R.color.amethyst
    LadderRankClass.EMERALD -> R.color.emerald
    LadderRankClass.ONYX -> R.color.onyx
}

val TrialRank.nameRes @StringRes get() = when(this) {
    TrialRank.COPPER -> R.string.copper
    TrialRank.BRONZE -> R.string.bronze
    TrialRank.SILVER -> R.string.silver
    TrialRank.GOLD -> R.string.gold
    TrialRank.PLATINUM -> R.string.platinum
    TrialRank.DIAMOND -> R.string.diamond
    TrialRank.COBALT -> R.string.cobalt
    TrialRank.PEARL -> R.string.pearl
    TrialRank.AMETHYST -> R.string.amethyst
    TrialRank.EMERALD -> R.string.emerald
    TrialRank.ONYX -> R.string.onyx
}

val TrialRank.drawableRes @DrawableRes get() = when(this) {
    TrialRank.COPPER -> R.drawable.copper_3
    TrialRank.BRONZE -> R.drawable.bronze_3
    TrialRank.SILVER -> R.drawable.silver_3
    TrialRank.GOLD -> R.drawable.gold_3
    TrialRank.PLATINUM -> R.drawable.platinum_3
    TrialRank.DIAMOND -> R.drawable.diamond_3
    TrialRank.COBALT -> R.drawable.cobalt_3
    TrialRank.PEARL -> R.drawable.cobalt_3 //FIXME
    TrialRank.AMETHYST -> R.drawable.amethyst_3
    TrialRank.EMERALD -> R.drawable.emerald_3
    TrialRank.ONYX -> R.drawable.onyx_3
}

@Deprecated("Migrate to moko resources", replaceWith = ReplaceWith("MR.colors"))
val TrialRank.colorRes @ColorRes get() = parent.colorRes

val PlacementRank.nameRes @StringRes get() = parent.nameRes
@Deprecated("Migrate to moko resources", replaceWith = ReplaceWith("MR.colors"))
val PlacementRank.colorRes @ColorRes get() = parent.colorRes

@Deprecated("Migrate to moko resources", replaceWith = ReplaceWith("MR.colors"))
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

@Deprecated("Migrate to moko resources", replaceWith = ReplaceWith("MR.colors"))
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

fun RankGoalUserType.titleString(res: Resources): String = when(this) {
    RankGoalUserType.LEVEL_12 -> res.getString(R.string.level_header, 12)
    RankGoalUserType.LEVEL_13 -> res.getString(R.string.level_header, 13)
    RankGoalUserType.LEVEL_14 -> res.getString(R.string.level_header, 14)
    RankGoalUserType.LEVEL_15 -> res.getString(R.string.level_header, 15)
    RankGoalUserType.LEVEL_16 -> res.getString(R.string.level_header, 16)
    RankGoalUserType.LEVEL_17 -> res.getString(R.string.level_header, 17)
    RankGoalUserType.LEVEL_18 -> res.getString(R.string.level_header, 18)
    RankGoalUserType.LEVEL_19 -> res.getString(R.string.level_header, 19)
    RankGoalUserType.PFC -> res.getString(R.string.rank_goal_category_pfcs)
    RankGoalUserType.COMBO -> res.getString(R.string.rank_goal_category_combo)
    RankGoalUserType.LIFE4 -> res.getString(R.string.rank_goal_category_life4)
    RankGoalUserType.CLEAR -> res.getString(R.string.clear)
    RankGoalUserType.MFC -> res.getString(R.string.rank_goal_category_mfcs)
    RankGoalUserType.SINGLE_SCORE -> res.getString(R.string.rank_goal_category_single_score)
    RankGoalUserType.SINGLE_CLEAR -> res.getString(R.string.rank_goal_category_single_clear)
    RankGoalUserType.SET_CLEAR -> res.getString(R.string.rank_goal_category_set_clear)
    RankGoalUserType.CALORIES -> res.getString(R.string.rank_goal_category_calories)
    RankGoalUserType.TRIALS -> res.getString(R.string.rank_goal_category_trials)
}