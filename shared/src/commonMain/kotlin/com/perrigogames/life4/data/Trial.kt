@file:UseSerializers(TrialTypeSerializer::class,
    TrialRankSerializer::class,
    DifficultyClassSerializer::class,
    PlayStyleSerializer::class,
    ChartTypeSerializer::class,
    DateTimeIsoSerializer::class)

package com.perrigogames.life4.data

import com.perrigogames.life4.enums.ChartTypeSerializer
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.DifficultyClassSerializer
import com.perrigogames.life4.enums.PlayStyleSerializer
import com.perrigogames.life4.response.TrialGoalSet
import com.soywiz.klock.DateTime
import com.soywiz.klock.until
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

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

@Serializable
class Trial(val id: String,
            val name: String,
            val author: String? = null,
            val type: TrialType,
            @SerialName("placement_rank") val placementRank: PlacementRank? = null,
            val new: Boolean = false,
            @SerialName("event_start") val eventStart: DateTimeWrapper? = null,
            @SerialName("event_end") val eventEnd: DateTimeWrapper? = null,
            @SerialName("scoring_groups") val scoringGroups: List<List<TrialRank>>? = null,
            val difficulty: Int? = null,
            val goals: List<TrialGoalSet>? = null,
            @SerialName("total_ex") val total_ex: Int = 0,
            @SerialName("cover_url") val coverUrl: String? = null,
            @SerialName("cover_override") val coverOverride: Boolean = false,
            val songs: List<Song>) {

    val isEvent get() = type == TrialType.EVENT && eventStart != null && eventEnd != null
    val isActiveEvent get() = isEvent && (eventStart!!.value until eventEnd!!.value).contains(DateTime.now())

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
    @SerialName("trial") TRIAL,
    @SerialName("placement") PLACEMENT,
    @SerialName("event") EVENT
}

@Serializer(forClass = TrialType::class)
object TrialTypeSerializer: KSerializer<TrialType> {
    override val descriptor: SerialDescriptor = StringDescriptor
    override fun deserialize(decoder: Decoder) = TrialType.valueOf(decoder.decodeString().toUpperCase())
    override fun serialize(encoder: Encoder, obj: TrialType) {
        encoder.encodeString(obj.name.toLowerCase())
    }
}
