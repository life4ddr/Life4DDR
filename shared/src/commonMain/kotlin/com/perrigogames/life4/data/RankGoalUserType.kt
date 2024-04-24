@file:OptIn(ExperimentalSerializationApi::class)

package com.perrigogames.life4.data

import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.LadderRankClass
import com.perrigogames.life4.enums.LadderRankClass.SILVER
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
enum class RankGoalUserType constructor(val serialized: String) {
    LEVEL_12("12"),
    LEVEL_13("13"),
    LEVEL_14("14"),
    LEVEL_15("15"),
    LEVEL_16("16"),
    LEVEL_17("17"),
    LEVEL_18("18"),
    LEVEL_19("19"),
    PFC("pfc"),
    COMBO("combo"),
    LIFE4("life4"),
    MFC("mfc"),
    SINGLE_SCORE("single_score"),
    CLEAR("clear"),
    SINGLE_CLEAR("single_clear"),
    SET_CLEAR("set_clear"),
    CALORIES("calories"),
    TRIALS("trials"),
    ;

    companion object {
        fun parse(v: String) = values().firstOrNull { it.serialized == v }
    }
}

fun BaseRankGoal.userType(rank: LadderRank): RankGoalUserType {
    return when (this) {
        is StackedRankGoalWrapper -> mainGoal.userType(rank)
        is CaloriesStackedRankGoal -> RankGoalUserType.CALORIES
        is TrialStackedGoal -> RankGoalUserType.TRIALS
        is DifficultySetGoal -> RankGoalUserType.SET_CLEAR
        is MFCPointsStackedGoal -> RankGoalUserType.MFC
        is SongsClearGoal -> {
            if (this.userType != null) {
                return this.userType
            }
            if (rank.group <= SILVER) {
                return when {
                    score != null -> RankGoalUserType.SINGLE_SCORE
                    songCount != null && songCount == 1 -> RankGoalUserType.SINGLE_CLEAR
                    else -> RankGoalUserType.CLEAR
                }
            }
            if (diffNum != null &&
                diffNum >= 12 &&
                rank.group >= LadderRankClass.GOLD
            ) {
                diffNum.toLevelUserType()?.let { return it }
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

@ExperimentalSerializationApi
object RankGoalUserTypeSerializer: KSerializer<RankGoalUserType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("rankGoalUserType", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder) = RankGoalUserType.parse(decoder.decodeString())!!
    override fun serialize(encoder: Encoder, value: RankGoalUserType) {
        encoder.encodeString(value.serialized)
    }
}
