package com.perrigogames.life4.enums

import com.perrigogames.life4.MR
import com.perrigogames.life4.data.PlacementRank
import com.perrigogames.life4.data.RankGoalUserType
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc

val DifficultyClass.nameRes get() = when(this) {
    DifficultyClass.BEGINNER -> MR.strings.bgsp
    DifficultyClass.BASIC -> MR.strings.bsp
    DifficultyClass.DIFFICULT -> MR.strings.dsp
    DifficultyClass.EXPERT -> MR.strings.esp
    DifficultyClass.CHALLENGE -> MR.strings.csp
}

val DifficultyClass.abbreviationRes get() = when(this) {
    DifficultyClass.BEGINNER -> MR.strings.bgsp
    DifficultyClass.BASIC -> MR.strings.bsp
    DifficultyClass.DIFFICULT -> MR.strings.dsp
    DifficultyClass.EXPERT -> MR.strings.esp
    DifficultyClass.CHALLENGE -> MR.strings.csp
}

val LadderRank.nameRes get() = when(this) {
    LadderRank.COPPER1 -> MR.strings.copper_1
    LadderRank.COPPER2 -> MR.strings.copper_2
    LadderRank.COPPER3 -> MR.strings.copper_3
    LadderRank.COPPER4 -> MR.strings.copper_4
    LadderRank.COPPER5 -> MR.strings.copper_5
    LadderRank.BRONZE1 -> MR.strings.bronze_1
    LadderRank.BRONZE2 -> MR.strings.bronze_2
    LadderRank.BRONZE3 -> MR.strings.bronze_3
    LadderRank.BRONZE4 -> MR.strings.bronze_4
    LadderRank.BRONZE5 -> MR.strings.bronze_5
    LadderRank.SILVER1 -> MR.strings.silver_1
    LadderRank.SILVER2 -> MR.strings.silver_2
    LadderRank.SILVER3 -> MR.strings.silver_3
    LadderRank.SILVER4 -> MR.strings.silver_4
    LadderRank.SILVER5 -> MR.strings.silver_5
    LadderRank.GOLD1 -> MR.strings.gold_1
    LadderRank.GOLD2 -> MR.strings.gold_2
    LadderRank.GOLD3 -> MR.strings.gold_3
    LadderRank.GOLD4 -> MR.strings.gold_4
    LadderRank.GOLD5 -> MR.strings.gold_5
    LadderRank.PLATINUM1 -> MR.strings.platinum_1
    LadderRank.PLATINUM2 -> MR.strings.platinum_2
    LadderRank.PLATINUM3 -> MR.strings.platinum_3
    LadderRank.PLATINUM4 -> MR.strings.platinum_4
    LadderRank.PLATINUM5 -> MR.strings.platinum_5
    LadderRank.DIAMOND1 -> MR.strings.diamond_1
    LadderRank.DIAMOND2 -> MR.strings.diamond_2
    LadderRank.DIAMOND3 -> MR.strings.diamond_3
    LadderRank.DIAMOND4 -> MR.strings.diamond_4
    LadderRank.DIAMOND5 -> MR.strings.diamond_5
    LadderRank.COBALT1 -> MR.strings.cobalt_1
    LadderRank.COBALT2 -> MR.strings.cobalt_2
    LadderRank.COBALT3 -> MR.strings.cobalt_3
    LadderRank.COBALT4 -> MR.strings.cobalt_4
    LadderRank.COBALT5 -> MR.strings.cobalt_5
    LadderRank.PEARL1 -> MR.strings.pearl_1
    LadderRank.PEARL2 -> MR.strings.pearl_2
    LadderRank.PEARL3 -> MR.strings.pearl_3
    LadderRank.PEARL4 -> MR.strings.pearl_4
    LadderRank.PEARL5 -> MR.strings.pearl_5
    LadderRank.AMETHYST1 -> MR.strings.amethyst_1
    LadderRank.AMETHYST2 -> MR.strings.amethyst_2
    LadderRank.AMETHYST3 -> MR.strings.amethyst_3
    LadderRank.AMETHYST4 -> MR.strings.amethyst_4
    LadderRank.AMETHYST5 -> MR.strings.amethyst_5
    LadderRank.EMERALD1 -> MR.strings.emerald_1
    LadderRank.EMERALD2 -> MR.strings.emerald_2
    LadderRank.EMERALD3 -> MR.strings.emerald_3
    LadderRank.EMERALD4 -> MR.strings.emerald_4
    LadderRank.EMERALD5 -> MR.strings.emerald_5
    LadderRank.ONYX1 -> MR.strings.onyx_1
    LadderRank.ONYX2 -> MR.strings.onyx_2
    LadderRank.ONYX3 -> MR.strings.onyx_3
    LadderRank.ONYX4 -> MR.strings.onyx_4
    LadderRank.ONYX5 -> MR.strings.onyx_5
}

val LadderRank.categoryNameRes get() = when(this) {
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
    LadderRank.ONYX1 -> MR.strings.roman_1
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
    LadderRank.ONYX2 -> MR.strings.roman_2
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
    LadderRank.ONYX3 -> MR.strings.roman_3
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
    LadderRank.ONYX4 -> MR.strings.roman_4
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
    LadderRank.ONYX5 -> MR.strings.roman_5
}

val LadderRank.groupNameRes get() = group.nameRes

val LadderRankClass.nameRes get() = when(this) {
    LadderRankClass.COPPER -> MR.strings.copper
    LadderRankClass.BRONZE -> MR.strings.bronze
    LadderRankClass.SILVER -> MR.strings.silver
    LadderRankClass.GOLD -> MR.strings.gold
    LadderRankClass.PLATINUM -> MR.strings.platinum
    LadderRankClass.DIAMOND -> MR.strings.diamond
    LadderRankClass.COBALT -> MR.strings.cobalt
    LadderRankClass.PEARL -> MR.strings.pearl
    LadderRankClass.AMETHYST -> MR.strings.amethyst
    LadderRankClass.EMERALD -> MR.strings.emerald
    LadderRankClass.ONYX -> MR.strings.onyx
}

val PlacementRank.nameRes get() = parent.nameRes

val TrialRank.nameRes get() = when(this) {
    TrialRank.COPPER -> MR.strings.copper
    TrialRank.BRONZE -> MR.strings.bronze
    TrialRank.SILVER -> MR.strings.silver
    TrialRank.GOLD -> MR.strings.gold
    TrialRank.PLATINUM -> MR.strings.platinum
    TrialRank.DIAMOND -> MR.strings.diamond
    TrialRank.COBALT -> MR.strings.cobalt
    TrialRank.PEARL -> MR.strings.pearl
    TrialRank.AMETHYST -> MR.strings.amethyst
    TrialRank.EMERALD -> MR.strings.emerald
    TrialRank.ONYX -> MR.strings.onyx
}

val ClearType.lampRes get() = when(this) {
    ClearType.NO_PLAY -> MR.strings.not_played
    ClearType.FAIL -> MR.strings.fail
    ClearType.CLEAR -> MR.strings.lamp_clear
    ClearType.LIFE4_CLEAR -> MR.strings.lamp_life4
    ClearType.GOOD_FULL_COMBO -> MR.strings.lamp_fc
    ClearType.GREAT_FULL_COMBO -> MR.strings.lamp_gfc
    ClearType.PERFECT_FULL_COMBO -> MR.strings.lamp_pfc
    ClearType.MARVELOUS_FULL_COMBO -> MR.strings.lamp_mfc
}

val ClearType.clearRes get() = when(this) {
    ClearType.NO_PLAY -> MR.strings.not_played
    ClearType.FAIL -> MR.strings.fail
    ClearType.CLEAR -> MR.strings.clear
    ClearType.LIFE4_CLEAR -> MR.strings.clear_life4
    ClearType.GOOD_FULL_COMBO -> MR.strings.clear_fc
    ClearType.GREAT_FULL_COMBO -> MR.strings.clear_gfc
    ClearType.PERFECT_FULL_COMBO -> MR.strings.clear_pfc
    ClearType.MARVELOUS_FULL_COMBO -> MR.strings.clear_mfc
}

val ClearType.clearResShort get() = when(this) {
    ClearType.GOOD_FULL_COMBO -> MR.strings.clear_fc_short
    ClearType.GREAT_FULL_COMBO -> MR.strings.clear_gfc_short
    ClearType.PERFECT_FULL_COMBO -> MR.strings.clear_pfc_short
    ClearType.MARVELOUS_FULL_COMBO -> MR.strings.clear_mfc_short
    else -> clearRes
}

fun RankGoalUserType.titleString(): StringDesc = when(this) {
    RankGoalUserType.LEVEL_12 -> StringDesc.ResourceFormatted(MR.strings.level_header, 12)
    RankGoalUserType.LEVEL_13 -> StringDesc.ResourceFormatted(MR.strings.level_header, 13)
    RankGoalUserType.LEVEL_14 -> StringDesc.ResourceFormatted(MR.strings.level_header, 14)
    RankGoalUserType.LEVEL_15 -> StringDesc.ResourceFormatted(MR.strings.level_header, 15)
    RankGoalUserType.LEVEL_16 -> StringDesc.ResourceFormatted(MR.strings.level_header, 16)
    RankGoalUserType.LEVEL_17 -> StringDesc.ResourceFormatted(MR.strings.level_header, 17)
    RankGoalUserType.LEVEL_18 -> StringDesc.ResourceFormatted(MR.strings.level_header, 18)
    RankGoalUserType.LEVEL_19 -> StringDesc.ResourceFormatted(MR.strings.level_header, 19)
    RankGoalUserType.PFC -> StringDesc.ResourceFormatted(MR.strings.rank_goal_category_pfcs)
    RankGoalUserType.COMBO -> StringDesc.ResourceFormatted(MR.strings.rank_goal_category_combo)
    RankGoalUserType.LIFE4 -> StringDesc.ResourceFormatted(MR.strings.rank_goal_category_life4)
    RankGoalUserType.CLEAR -> StringDesc.ResourceFormatted(MR.strings.clear)
    RankGoalUserType.MFC -> StringDesc.ResourceFormatted(MR.strings.rank_goal_category_mfcs)
    RankGoalUserType.SINGLE_SCORE -> StringDesc.ResourceFormatted(MR.strings.rank_goal_category_single_score)
    RankGoalUserType.SINGLE_CLEAR -> StringDesc.ResourceFormatted(MR.strings.rank_goal_category_single_clear)
    RankGoalUserType.SET_CLEAR -> StringDesc.ResourceFormatted(MR.strings.rank_goal_category_set_clear)
    RankGoalUserType.CALORIES -> StringDesc.ResourceFormatted(MR.strings.rank_goal_category_calories)
    RankGoalUserType.TRIALS -> StringDesc.ResourceFormatted(MR.strings.rank_goal_category_trials)
}

val LadderRank.colorRes get() = group.colorRes

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

val TrialRank.colorRes get() = parent.colorRes
val PlacementRank.colorRes get() = parent.colorRes

val DifficultyClass.colorRes get() = when(this) {
    DifficultyClass.BEGINNER -> MR.colors.difficultyBeginner
    DifficultyClass.BASIC -> MR.colors.difficultyBasic
    DifficultyClass.DIFFICULT -> MR.colors.difficultyDifficult
    DifficultyClass.EXPERT -> MR.colors.difficultyExpert
    DifficultyClass.CHALLENGE -> MR.colors.difficultyChallenge
}

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
