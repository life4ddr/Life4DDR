package com.perrigogames.life4.enums

import com.perrigogames.life4.enums.LadderRankClass.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Enum class representing a Rank that a player can earn in LIFE4.
 */
@Serializable(with = LadderRankSerializer::class)
enum class LadderRank(
    val stableId: Long,
    val group: LadderRankClass,
    val classPosition: Int,
) {
    COPPER1(stableId = 20, group = COPPER, classPosition = 1),
    COPPER2(stableId = 21, group = COPPER, classPosition = 2),
    COPPER3(stableId = 22, group = COPPER, classPosition = 3),
    COPPER4(stableId = 23, group = COPPER, classPosition = 4),
    COPPER5(stableId = 24, group = COPPER, classPosition = 5),
    BRONZE1(stableId = 30, group = BRONZE, classPosition = 1),
    BRONZE2(stableId = 31, group = BRONZE, classPosition = 2),
    BRONZE3(stableId = 32, group = BRONZE, classPosition = 3),
    BRONZE4(stableId = 33, group = BRONZE, classPosition = 4),
    BRONZE5(stableId = 34, group = BRONZE, classPosition = 5),
    SILVER1(stableId = 40, group = SILVER, classPosition = 1),
    SILVER2(stableId = 41, group = SILVER, classPosition = 2),
    SILVER3(stableId = 42, group = SILVER, classPosition = 3),
    SILVER4(stableId = 43, group = SILVER, classPosition = 4),
    SILVER5(stableId = 44, group = SILVER, classPosition = 5),
    GOLD1(stableId = 50, group = GOLD, classPosition = 1),
    GOLD2(stableId = 51, group = GOLD, classPosition = 2),
    GOLD3(stableId = 52, group = GOLD, classPosition = 3),
    GOLD4(stableId = 53, group = GOLD, classPosition = 4),
    GOLD5(stableId = 54, group = GOLD, classPosition = 5),
    PLATINUM1(stableId = 55, group = PLATINUM, classPosition = 1),
    PLATINUM2(stableId = 56, group = PLATINUM, classPosition = 2),
    PLATINUM3(stableId = 57, group = PLATINUM, classPosition = 3),
    PLATINUM4(stableId = 58, group = PLATINUM, classPosition = 4),
    PLATINUM5(stableId = 59, group = PLATINUM, classPosition = 5),
    DIAMOND1(stableId = 60, group = DIAMOND, classPosition = 1),
    DIAMOND2(stableId = 61, group = DIAMOND, classPosition = 2),
    DIAMOND3(stableId = 62, group = DIAMOND, classPosition = 3),
    DIAMOND4(stableId = 63, group = DIAMOND, classPosition = 4),
    DIAMOND5(stableId = 64, group = DIAMOND, classPosition = 5),
    COBALT1(stableId = 70, group = COBALT, classPosition = 1),
    COBALT2(stableId = 71, group = COBALT, classPosition = 2),
    COBALT3(stableId = 72, group = COBALT, classPosition = 3),
    COBALT4(stableId = 73, group = COBALT, classPosition = 4),
    COBALT5(stableId = 74, group = COBALT, classPosition = 5),
    PEARL1(stableId = 75, group = PEARL, classPosition = 1),
    PEARL2(stableId = 76, group = PEARL, classPosition = 2),
    PEARL3(stableId = 77, group = PEARL, classPosition = 3),
    PEARL4(stableId = 78, group = PEARL, classPosition = 4),
    PEARL5(stableId = 79, group = PEARL, classPosition = 5),
    AMETHYST1(stableId = 80, group = AMETHYST, classPosition = 1),
    AMETHYST2(stableId = 81, group = AMETHYST, classPosition = 2),
    AMETHYST3(stableId = 82, group = AMETHYST, classPosition = 3),
    AMETHYST4(stableId = 83, group = AMETHYST, classPosition = 4),
    AMETHYST5(stableId = 84, group = AMETHYST, classPosition = 5),
    EMERALD1(stableId = 90, group = EMERALD, classPosition = 1),
    EMERALD2(stableId = 91, group = EMERALD, classPosition = 2),
    EMERALD3(stableId = 92, group = EMERALD, classPosition = 3),
    EMERALD4(stableId = 93, group = EMERALD, classPosition = 4),
    EMERALD5(stableId = 94, group = EMERALD, classPosition = 5),
    ONYX1(stableId = 100, group = ONYX, classPosition = 1),
    ONYX2(stableId = 101, group = ONYX, classPosition = 2),
    ONYX3(stableId = 102, group = ONYX, classPosition = 3),
    ONYX4(stableId = 103, group = ONYX, classPosition = 4),
    ONYX5(stableId = 104, group = ONYX, classPosition = 5),
    ;

    val next: LadderRank? get() = entries.getOrNull(ordinal + 1)

    companion object {
        fun parse(s: String?): LadderRank? = try {
            s?.let {
                valueOf(it.uppercase()
                    .replace(" IV", "4")
                    .replace(" V", "5")
                    .replace(" III", "3")
                    .replace(" II", "2")
                    .replace(" I", "1"))
            }
        } catch (e: IllegalArgumentException) { null }

        fun parse(stableId: Long?): LadderRank? = stableId?.let { id -> entries.firstOrNull { it.stableId == id } }
    }
}

val LadderRank?.nullableNext: LadderRank?
    get() = if (this == null) {
        LadderRank.entries.first()
    } else { this.next }

/**
 * Enum describing the groups that Ranks are put into.
 */
enum class LadderRankClass {
    COPPER, BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, COBALT, PEARL, AMETHYST, EMERALD, ONYX;

    val ranks by lazy {
        LadderRank.entries
            .filter { it.group == this }
            .sortedBy { it.classPosition }
    }

    /**
     * @param index the index of the rank inside this group, indexed at 0 (index 0 = COPPER 1)
     */
    fun rankAtIndex(index: Int) = ranks[index]

    fun toLadderRank() = ranks.last()
}

object LadderRankSerializer: KSerializer<LadderRank> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ladderRank", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder) = LadderRank.parse(decoder.decodeString())!!
    override fun serialize(encoder: Encoder, value: LadderRank) {
        encoder.encodeString(value.name.lowercase())
    }
}
