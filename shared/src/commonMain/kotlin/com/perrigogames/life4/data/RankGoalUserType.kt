@file:OptIn(ExperimentalSerializationApi::class)

package com.perrigogames.life4.data

import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.LadderRankClass
import kotlinx.serialization.ExperimentalSerializationApi

enum class RankGoalUserType {
    LEVEL_12,
    LEVEL_13,
    LEVEL_14,
    LEVEL_15,
    LEVEL_16,
    LEVEL_17,
    LEVEL_18,
    LEVEL_19,
    PFC,
    AAA,
    AA_PLUS,
    AA,
    A_PLUS,
    A, //
    COMBO,
    LIFE4,
    MFC, //
    SINGLE_CLEAR,
    SET_CLEAR,
    CALORIES,
    TRIALS,
    ;
}

fun BaseRankGoal.userType(rank: LadderRank): RankGoalUserType {
    return when (this) {
        is CaloriesRankGoal -> RankGoalUserType.CALORIES
        is TrialGoal -> RankGoalUserType.TRIALS
        is DifficultySetGoal -> RankGoalUserType.SET_CLEAR
        is MFCPointsGoal -> RankGoalUserType.MFC
        is SongsClearGoal -> {
            if (rank.group <= LadderRankClass.SILVER) {
                if (songCount != null && songCount == 1) {
                    return RankGoalUserType.SINGLE_CLEAR
                }
            }
            if (rank.group >= LadderRankClass.PLATINUM) {
                if (diffNum != null && diffNum >= 14) {
                    diffNum.toLevelUserType()?.let { return it }
                }
            }
            return when (clearType) {
                ClearType.PERFECT_FULL_COMBO -> RankGoalUserType.PFC
                ClearType.GREAT_FULL_COMBO,
                ClearType.GOOD_FULL_COMBO -> RankGoalUserType.COMBO
                ClearType.LIFE4_CLEAR -> RankGoalUserType.LIFE4
                else -> error("No user type for goal $id")
            }
        }
        else -> error("No user type for goal $id")
    }
}

private fun Int.toLevelUserType() = when (this) {
    12 -> RankGoalUserType.LEVEL_12
    13 -> RankGoalUserType.LEVEL_13
    14 -> RankGoalUserType.LEVEL_14
    15 -> RankGoalUserType.LEVEL_15
    16 -> RankGoalUserType.LEVEL_16
    17 -> RankGoalUserType.LEVEL_17
    18 -> RankGoalUserType.LEVEL_18
    19 -> RankGoalUserType.LEVEL_19
    else -> null
}

private fun Int.toScoreUserType() = when (this) {
    990_000 -> RankGoalUserType.AAA
    950_000 -> RankGoalUserType.AA_PLUS
    900_000 -> RankGoalUserType.AA
    850_000 -> RankGoalUserType.A_PLUS
    800_000 -> RankGoalUserType.A
    else -> null
}