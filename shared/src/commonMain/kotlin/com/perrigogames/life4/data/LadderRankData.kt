@file:UseSerializers(PlayStyleSerializer::class,
    LadderRankSerializer::class)

package com.perrigogames.life4.data

import com.perrigogames.life4.enums.GameVersion
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.enums.PlayStyleSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers

/**
 * Data class for deserializing the ranks_v2_v2.json file. Describes all of the ranks_v2 that can
 * be earned in LIFE4 and the goals required to obtain each.
 */
@Serializable
class LadderRankData(override val version: Int,
                     @SerialName("major_version") override val majorVersion: Int,
                     @SerialName("goals") val goals: List<BaseRankGoal>,
                     @SerialName("game_versions") val gameVersions: Map<GameVersion, LadderVersion>): MajorVersioned {

    init {
        gameVersions.values.flatMap { it.rankRequirements }.forEach {  entry ->
            entry.goals = entry.goalIds.map { id ->
                goals.firstOrNull { it.id == id } ?:
                error("ID not found: $id")
            }
        }
    }

    companion object {
        const val LADDER_RANK_MAJOR_VERSION = 1
    }
}

@Serializable
class LadderVersion(@SerialName("unlock_requirement") val unlockRequirement: LadderRank,
                    @SerialName("rank_requirements") val rankRequirements: List<RankEntry>)

/**
 * Describes a single rank in [LadderRankData] and the goals required to obtain it.
 */
@Serializable
class RankEntry(val rank: LadderRank,
                @SerialName("play_style") val playStyle: PlayStyle,
                @SerialName("goal_ids") val goalIds: List<Int>,
                @SerialName("requirements") private val requirementsOpt: Int? = null) {

    @Transient var goals = emptyList<BaseRankGoal>()

    private val mandatoryGoalCount get() = goals.count { it.mandatory }

    val requirements: Int get() = requirementsOpt?.plus(mandatoryGoalCount) ?: goals.size

    val allowedIgnores: Int get() = requirementsOpt?.let { req -> goals.count { !it.mandatory } - req } ?: 0

    val difficultyGoals: List<DifficultyClearGoal> get() = goals.mapNotNull { it as? DifficultyClearGoal }
}
