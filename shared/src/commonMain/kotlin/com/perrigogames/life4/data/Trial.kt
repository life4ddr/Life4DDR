package com.perrigogames.life4.data

import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.response.TrialGoalSet
import com.soywiz.klock.DateTime
import com.soywiz.klock.until
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TrialData(override val version: Int,
                @SerialName("major_version") override val majorVersion: Int,
                val trials: List<Trial>): MajorVersioned {

    companion object {
        const val HIGHEST_DIFFICULTY = 20
        const val TRIAL_LENGTH = 4
        const val MAX_SCORE = 1000000
        const val SCORE_PENALTY_PERFECT = 10
        const val AAA_SCORE = 990000
    }
}

@Serializable(with = DateTimeIsoSerializer::class)
class Trial(val id: String,
            val name: String,
            val author: String?,
            val type: TrialType,
            @SerialName("placement_rank") val placementRank: PlacementRank?,
            val new: Boolean = false,
            @SerialName("event_start") val eventStart: DateTime?,
            @SerialName("event_end") val eventEnd: DateTime?,
            @SerialName("scoring_groups") val scoringGroups: List<List<TrialRank>>?,
            val difficulty: Int?,
            val goals: List<TrialGoalSet>?,
            @SerialName("total_ex") val total_ex: Int,
            @SerialName("cover_url") val coverUrl: String? = null,
            @SerialName("cover_override") val coverOverride: Boolean = false,
            val songs: List<Song>) {

    val isEvent get() = type == TrialType.EVENT && eventStart != null && eventEnd != null
    val isActiveEvent get() = isEvent && (eventStart!! until eventEnd!!).contains(DateTime.now())

    fun goalSet(rank: TrialRank?): TrialGoalSet? = goals?.find { it.rank == rank }

    fun highestGoal(): TrialGoalSet? = goals?.maxBy { it.rank.stableId }

    /**
     * Return the scoring group for a user with a particular rank.
     */
    fun findScoringGroup(rank: TrialRank) = scoringGroups?.first { it.contains(rank) }

    val isExValid get() = songs.sumBy { it.ex }.let { it == 0 || it == total_ex }
}

@Serializable
class Song(val name: String,
           @SerialName("difficulty") val difficultyNumber: Int,
           @SerialName("difficulty_class") val difficultyClass: DifficultyClass,
           val ex: Int,
           val url: String? = null)

@Serializable
enum class TrialType {
    TRIAL, PLACEMENT, EVENT
}
