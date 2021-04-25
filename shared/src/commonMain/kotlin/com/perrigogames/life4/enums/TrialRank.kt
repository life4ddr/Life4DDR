package com.perrigogames.life4.enums

import com.perrigogames.life4.data.StableId
import com.perrigogames.life4.enums.LadderRank.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Enum class representing a Rank that a player can earn in a LIFE4 Trial.
 */
@Serializable
enum class TrialRank(override val stableId: Long, val parent: LadderRank): StableId {
    COPPER(10, COPPER3),
    BRONZE(15, BRONZE3),
    SILVER(20, SILVER3),
    GOLD(25, GOLD3),
    PLATINUM(27, PLATINUM3),
    DIAMOND(30, DIAMOND3),
    COBALT(35, COBALT3),
    AMETHYST(40, AMETHYST3),
    EMERALD(45, EMERALD3),
    ONYX(50, ONYX3),
    ;

    /**
     * Generates a list of this and all [TrialRank]s that are higher than this.
     */
    val andUp: Array<TrialRank>
        get() = values().let { it.copyOfRange(this.ordinal, it.size) }

    companion object {
        fun parse(s: String?): TrialRank? = when (s) {
            null, "NONE" -> null
            else -> valueOf(s)
        }

        fun parse(stableId: Long): TrialRank? = values().firstOrNull { it.stableId == stableId }

        fun fromLadderRank(userRank: LadderRank?, parsePlatinum: Boolean) = when(userRank?.group) {
            null -> null
            LadderRankClass.COPPER -> COPPER
            LadderRankClass.BRONZE -> BRONZE
            LadderRankClass.SILVER -> SILVER
            LadderRankClass.GOLD -> GOLD
            LadderRankClass.PLATINUM -> if (parsePlatinum) PLATINUM else GOLD
            LadderRankClass.DIAMOND -> DIAMOND
            LadderRankClass.COBALT -> COBALT
            LadderRankClass.AMETHYST -> AMETHYST
            LadderRankClass.EMERALD -> ONYX
            LadderRankClass.ONYX -> ONYX
        }
    }
}

@Serializer(forClass = TrialRank::class)
object TrialRankSerializer: KSerializer<TrialRank> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("trialRank", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder) = TrialRank.valueOf(decoder.decodeString().toUpperCase())
    override fun serialize(encoder: Encoder, value: TrialRank) {
        encoder.encodeString(value.name.toLowerCase())
    }
}
