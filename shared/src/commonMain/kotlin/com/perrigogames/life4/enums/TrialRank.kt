package com.perrigogames.life4.enums

import com.perrigogames.life4.data.StableId
import com.perrigogames.life4.enums.LadderRank.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Enum class representing a Rank that a player can earn in a LIFE4 Trial.
 */
@Serializable
@ExperimentalSerializationApi
enum class TrialRank(override val stableId: Long, val parent: LadderRank): StableId {
    COPPER(10, COPPER5),
    BRONZE(15, BRONZE5),
    SILVER(20, SILVER5),
    GOLD(25, GOLD5),
    PLATINUM(27, PLATINUM5),
    DIAMOND(30, DIAMOND5),
    COBALT(35, COBALT5),
    PEARL(38, PEARL5),
    AMETHYST(40, AMETHYST5),
    EMERALD(45, EMERALD5),
    ONYX(50, ONYX5),
    ;

    /**
     * Generates a list of this and all [TrialRank]s that are higher than this.
     */
    val andUp: Array<TrialRank>
        get() = entries.toTypedArray().let { it.copyOfRange(this.ordinal, it.size) }

    companion object {
        fun parse(s: String?): TrialRank? = when (s) {
            null, "NONE" -> null
            else -> valueOf(s)
        }

        fun parse(stableId: Long): TrialRank? = entries.firstOrNull { it.stableId == stableId }

        fun fromLadderRank(userRank: LadderRank?, parsePlatinum: Boolean) = when(userRank?.group) {
            null -> null
            LadderRankClass.COPPER -> COPPER
            LadderRankClass.BRONZE -> BRONZE
            LadderRankClass.SILVER -> SILVER
            LadderRankClass.GOLD -> GOLD
            LadderRankClass.PLATINUM -> if (parsePlatinum) PLATINUM else GOLD
            LadderRankClass.DIAMOND -> DIAMOND
            LadderRankClass.COBALT -> COBALT
            LadderRankClass.PEARL -> PEARL
            LadderRankClass.AMETHYST -> AMETHYST
            LadderRankClass.EMERALD -> ONYX
            LadderRankClass.ONYX -> ONYX
        }
    }
}

@ExperimentalSerializationApi
object TrialRankSerializer: KSerializer<TrialRank> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("trialRank", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder) = TrialRank.valueOf(decoder.decodeString().uppercase())
    override fun serialize(encoder: Encoder, value: TrialRank) {
        encoder.encodeString(value.name.lowercase())
    }
}
