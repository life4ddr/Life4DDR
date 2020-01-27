package com.perrigogames.life4trials.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Data class for deserializing the ranks_v2_v2.json file. Describes all of the ranks_v2 that can
 * be earned in LIFE4 and the goals required to obtain each.
 */
class LadderRankData(override val version: Int,
                     override val majorVersion: Int,
                     @SerializedName("goals") val goals: List<BaseRankGoal>,
                     @SerializedName("game_versions") val gameVersions: Map<GameVersion, LadderVersion>): Serializable, MajorVersioned

class LadderVersion(@SerializedName("unlock_requirement") val unlockRequirement: LadderRank,
                    @SerializedName("rank_requirements") val rankRequirements: List<RankEntry>): Serializable

/**
 * Describes a single rank in [LadderRankData] and the goals required to obtain it.
 */
class RankEntry(val rank: LadderRank,
                @SerializedName("play_style") val playStyle: PlayStyle,
                @SerializedName("goal_ids") val goalIds: List<Int>,
                @SerializedName("requirements") private val requirementsOpt: Int?): Serializable {

    @Transient var goals = emptyList<BaseRankGoal>()

    val requirements: Int get() = requirementsOpt ?: goals.size

    val allowedIgnores: Int get() = requirementsOpt?.let { req -> goals.count { !it.mandatory } - req } ?: 0

    val difficultyGoals: List<DifficultyClearGoal> get() = goals.mapNotNull { it as? DifficultyClearGoal }
}