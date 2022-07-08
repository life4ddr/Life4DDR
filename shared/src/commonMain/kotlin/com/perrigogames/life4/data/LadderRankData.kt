@file:OptIn(ExperimentalSerializationApi::class)
@file:UseSerializers(
    PlayStyleSerializer::class,
    LadderRankSerializer::class)

package com.perrigogames.life4.data

import com.perrigogames.life4.enums.*
import com.perrigogames.life4.logE
import kotlinx.serialization.*

/**
 * Data class for deserializing the ranks_v2_v2.json file. Describes all of the ranks_v2 that can
 * be earned in LIFE4 and the goals required to obtain each.
 */
@Serializable
data class LadderRankData(
    override val version: Int,
    @SerialName("major_version") override val majorVersion: Int,
    @SerialName("goals") val goals: List<BaseRankGoal>,
    @SerialName("game_versions") val gameVersions: Map<GameVersion, LadderVersion>,
): MajorVersioned {

    init {
        validate()
        gameVersions.values.flatMap { it.rankRequirements }.forEach {  entry ->
            entry.goalIds?.let { entry.goals = mapIdsToGoals(it) }
            entry.mandatoryGoalIds?.let { entry.mandatoryGoals = mapIdsToGoals(it) }
        }
    }

    private fun validate() {
        goals.forEach { goal ->
            (goal as? SongsClearGoal)?.validate()?.let { valid ->
                if (!valid) {
                    logE("GoalValidation", "Goal ${goal.id} is invalid")
                }
            }
        }
    }

    private fun mapIdsToGoals(ids: List<Int>): List<BaseRankGoal> {
        return ids.map { id ->
            goals.firstOrNull { it.id == id } ?:
            error("ID not found: $id")
        }
    }

    companion object {
        const val LADDER_RANK_MAJOR_VERSION = 3
    }
}

@Serializable
data class LadderVersion(
    @SerialName("unlock_requirement") val unlockRequirement: LadderRank,
    @SerialName("rank_requirements") val rankRequirements: List<RankEntry>,
)

/**
 * Describes a single rank in [LadderRankData] and the goals required to obtain it.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class RankEntry(
    val rank: LadderRank,
    @SerialName("play_style") val playStyle: PlayStyle,
    @SerialName("goal_ids") val goalIds: List<Int>? = null,
    @SerialName("mandatory_goal_ids") val mandatoryGoalIds: List<Int>? = null,
    val substitutions: List<Int>? = null,
    @SerialName("requirements") private val requirementsOpt: Int? = null,
) {

    @Transient var goals = emptyList<BaseRankGoal>()
    @Transient var mandatoryGoals = emptyList<BaseRankGoal>()

    private val mandatoryGoalCount
        get() = mandatoryGoals.count()

    val requirements: Int
        get() = requirementsOpt?.plus(mandatoryGoalCount) ?: (goals.size + mandatoryGoals.size)

    val allowedIgnores: Int
        get() = requirementsOpt?.let { req -> goals.size - req } ?: 0
}
