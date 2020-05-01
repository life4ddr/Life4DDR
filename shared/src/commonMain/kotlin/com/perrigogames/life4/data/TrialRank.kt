package com.perrigogames.life4.data

import com.perrigogames.life4.data.LadderRank.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

/**
 * Enum class representing a Rank that a player can earn in a LIFE4 Trial.
 */
@Serializable
enum class TrialRank(override val stableId: Long, val parent: LadderRank): StableId {
    WOOD(10, WOOD3),
    BRONZE(15, BRONZE3),
    SILVER(20, SILVER3),
    GOLD(25, GOLD3),
    PLATINUM(27, PLATINUM3),
    DIAMOND(30, DIAMOND3),
    COBALT(35, COBALT3),
    AMETHYST(40, AMETHYST3),
    EMERALD(45, EMERALD3);

    val next get() = when(this) {
        WOOD -> BRONZE
        BRONZE -> SILVER
        SILVER -> GOLD
        GOLD -> DIAMOND // PLATINUM isn't really used for Trials
        PLATINUM -> DIAMOND
        DIAMOND -> COBALT
        COBALT -> AMETHYST
        AMETHYST -> EMERALD
        EMERALD -> EMERALD
    }

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

        fun fromLadderRank(userRank: LadderRank?, parsePlatinum: Boolean) = when(userRank) {
            null -> null
            WOOD1, WOOD2, WOOD3 -> WOOD
            BRONZE1, BRONZE2, BRONZE3 -> BRONZE
            SILVER1, SILVER2, SILVER3 -> SILVER
            GOLD1, GOLD2, GOLD3 -> GOLD
            PLATINUM1, PLATINUM2, PLATINUM3 -> if (parsePlatinum) PLATINUM else GOLD
            DIAMOND1, DIAMOND2, DIAMOND3 -> DIAMOND
            COBALT1, COBALT2, COBALT3 -> COBALT
            AMETHYST1, AMETHYST2, AMETHYST3 -> AMETHYST
            EMERALD1, EMERALD2, EMERALD3 -> EMERALD
        }
    }
}

@Serializer(forClass = TrialRank::class)
object TrialRankSerializer: KSerializer<TrialRank> {
    override val descriptor: SerialDescriptor = StringDescriptor
    override fun deserialize(decoder: Decoder) = TrialRank.valueOf(decoder.decodeString().toUpperCase())
    override fun serialize(encoder: Encoder, obj: TrialRank) {
        encoder.encodeString(obj.name.toLowerCase())
    }
}
