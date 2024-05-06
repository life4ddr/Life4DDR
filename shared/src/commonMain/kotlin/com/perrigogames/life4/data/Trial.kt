@file:UseSerializers(
    TrialTypeSerializer::class,
    TrialRankSerializer::class,
    PlacementRankSerializer::class,
    DifficultyClassSerializer::class,
    PlayStyleSerializer::class,
    ChartTypeSerializer::class,
    InstantIso8601Serializer::class,
)

package com.perrigogames.life4.data

import com.perrigogames.life4.enums.ChartTypeSerializer
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.DifficultyClassSerializer
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.enums.PlayStyleSerializer
import com.perrigogames.life4.enums.TrialRank
import com.perrigogames.life4.enums.TrialRankSerializer
import com.perrigogames.life4.enums.TrialType
import com.perrigogames.life4.enums.TrialTypeSerializer
import com.perrigogames.life4.feature.trialsession.TrialGoalSet
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.serializers.InstantIso8601Serializer
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.math.min

@Serializable
data class TrialData(
    override val version: Int,
    @SerialName("major_version") override val majorVersion: Int,
    val trials: List<Trial>,
) : MajorVersioned {
    companion object {
        const val TRIAL_DATA_REMOTE_VERSION = 3
    }
}

@Serializable
data class Trial(
    val id: String,
    val name: String,
    val author: String? = null,
    val state: TrialState = TrialState.ACTIVE,
    val type: TrialType,
    @SerialName("placement_rank") val placementRank: PlacementRank? = null,
    val songs: List<Song>,
    @SerialName("play_style") val playStyle: PlayStyle = PlayStyle.SINGLE,
    @SerialName("event_start") val eventStart: LocalDateTime? = null,
    @SerialName("event_end") val eventEnd: LocalDateTime? = null,
    @SerialName("scoring_groups") val scoringGroups: List<List<TrialRank>>? = null,
    val difficulty: Int? = null,
    val goals: List<TrialGoalSet>? = null,
    @SerialName("total_ex") val totalEx: Int = 0,
    @SerialName("cover_url") val coverUrl: String? = null,
    @SerialName("cover_override") val coverOverride: Boolean = false,
) {
    val isRetired: Boolean = state == TrialState.RETIRED
    val isEvent: Boolean = type == TrialType.EVENT && eventStart != null && eventEnd != null
    val isActiveEvent: Boolean
        get() =
            isEvent &&
                (eventStart!!.rangeTo(eventEnd!!)).contains(
                    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                )
    val new = state == TrialState.NEW

    fun goalSet(rank: TrialRank?): TrialGoalSet? = goals?.find { it.rank == rank }

    fun highestGoal(): TrialGoalSet? = goals?.maxByOrNull { it.rank.stableId }

    /**
     * Return the scoring group for a user with a particular rank.
     */
    fun findScoringGroup(rank: TrialRank) = scoringGroups?.first { it.contains(rank) }

    val isExValid get() = songs.sumOf { it.ex }.let { it == 0 || it == totalEx }

    fun rankAfter(rank: TrialRank): TrialRank? {
        return goals?.let { goals ->
            val startIdx = goals.indexOfFirst { it.rank == rank }
            val idx =
                min(
                    startIdx + 1,
                    goals.size - 1,
                )
            return goals[idx].rank
        }
    }
}

@Serializable
data class Song(
    val name: String,
    val skillId: String = "FIXME",
    @SerialName("play_style") val playStyle: PlayStyle = PlayStyle.SINGLE,
    @SerialName("difficulty") val difficultyNumber: Int,
    @SerialName("difficulty_class") val difficultyClass: DifficultyClass,
    val ex: Int,
    val url: String? = null,
)
