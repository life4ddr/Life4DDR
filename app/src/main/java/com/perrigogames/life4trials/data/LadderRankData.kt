package com.perrigogames.life4trials.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Data class for deserializing the ranks.json file. Describes all of the ranks that can
 * be earned in LIFE4 and the goals required to obtain each.
 */
class LadderRankData(val version: Int,
                     @SerializedName("unlock_requirement") val unlockRequirement: LadderRank,
                     @SerializedName("rank_requirements") val rankRequirements: List<RankEntry>): Serializable

/**
 * Describes a single rank in [LadderRankData] and the goals required to obtain it.
 */
class RankEntry @JvmOverloads constructor(val rank: LadderRank,
                @SerializedName("play_style") val playStyle: PlayStyle,
                val goals: List<BaseRankGoal>,
                val requirements: Int?): Serializable {

    val allowedIgnores: Int get() = requirements?.let { req -> goals.count { !it.mandatory } - req } ?: 0

    val difficultyGoals: List<DifficultyClearGoal> get() = goals.mapNotNull { it as? DifficultyClearGoal }
}