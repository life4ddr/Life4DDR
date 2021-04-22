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
import com.perrigogames.life4.enums.PlayStyleSerializer
import com.perrigogames.life4.response.TrialGoalSet
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.serializers.InstantIso8601Serializer
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.max

@Serializable
data class TrialData(override val version: Int,
                     @SerialName("major_version") override val majorVersion: Int,
                     val trials: List<Trial>): MajorVersioned {

    companion object {
        const val HIGHEST_DIFFICULTY = 20
        const val TRIAL_LENGTH = 4
        const val MAX_SCORE = 1000000
        const val SCORE_PENALTY_PERFECT = 10
        const val AAA_SCORE = 990000

        const val TRIAL_DATA_REMOTE_VERSION = 3
    }
}

@Serializable
data class Trial(val id: String,
                 val name: String,
                 val author: String? = null,
                 val type: TrialType,
                 @SerialName("placement_rank") val placementRank: PlacementRank? = null,
                 val new: Boolean = false,
                 @SerialName("event_start") val eventStart: Instant? = null,
                 @SerialName("event_end") val eventEnd: Instant? = null,
                 @SerialName("scoring_groups") val scoringGroups: List<List<TrialRank>>? = null,
                 val difficulty: Int? = null,
                 val goals: List<TrialGoalSet>? = null,
                 @SerialName("total_ex") val total_ex: Int = 0,
                 @SerialName("cover_url") val coverUrl: String? = null,
                 @SerialName("cover_override") val coverOverride: Boolean = false,
                 val songs: List<Song>) {

    val isEvent get() = type == TrialType.EVENT && eventStart != null && eventEnd != null
    val isActiveEvent get() = isEvent && (eventStart!!.rangeTo(eventEnd!!)).contains(Clock.System.now())

    fun goalSet(rank: TrialRank?): TrialGoalSet? = goals?.find { it.rank == rank }

    fun highestGoal(): TrialGoalSet? = goals?.maxBy { it.rank.stableId }

    /**
     * Return the scoring group for a user with a particular rank.
     */
    fun findScoringGroup(rank: TrialRank) = scoringGroups?.first { it.contains(rank) }

    val isExValid get() = songs.sumBy { it.ex }.let { it == 0 || it == total_ex }

    fun rankAfter(rank: TrialRank): TrialRank? {
        return goals?.let { goals ->
            val startIdx = goals.indexOfFirst { it.rank == rank }
            val idx = max(
                startIdx + 1,
                goals.size
            )
            return goals[idx].rank
        }
    }
}

@Serializable
data class Song(val name: String,
                @SerialName("difficulty") val difficultyNumber: Int,
                @SerialName("difficulty_class") val difficultyClass: DifficultyClass,
                val ex: Int,
                val url: String? = null)

@Serializable
enum class TrialType {
    @SerialName("trial") TRIAL,
    @SerialName("placement") PLACEMENT,
    @SerialName("event") EVENT
}

@Serializer(forClass = TrialType::class)
object TrialTypeSerializer: KSerializer<TrialType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("trialType", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder) = TrialType.valueOf(decoder.decodeString().toUpperCase())
    override fun serialize(encoder: Encoder, value: TrialType) {
        encoder.encodeString(value.name.toLowerCase())
    }
}
