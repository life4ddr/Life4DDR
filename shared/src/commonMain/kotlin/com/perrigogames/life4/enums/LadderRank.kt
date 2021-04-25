package com.perrigogames.life4.enums

import com.perrigogames.life4.enums.LadderRankClass.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Enum class representing a Rank that a player can earn in LIFE4.
 */
@Serializable(with = LadderRankSerializer::class)
enum class LadderRank(val stableId: Long,
                      val group: LadderRankClass
) {
    COPPER1(20, COPPER),
    COPPER2(21, COPPER),
    COPPER3(22, COPPER),
    COPPER4(23, COPPER),
    COPPER5(24, COPPER),
    BRONZE1(30, BRONZE),
    BRONZE2(31, BRONZE),
    BRONZE3(32, BRONZE),
    BRONZE4(33, BRONZE),
    BRONZE5(34, BRONZE),
    SILVER1(40, SILVER),
    SILVER2(41, SILVER),
    SILVER3(42, SILVER),
    SILVER4(43, SILVER),
    SILVER5(44, SILVER),
    GOLD1(50, GOLD),
    GOLD2(51, GOLD),
    GOLD3(52, GOLD),
    GOLD4(53, GOLD),
    GOLD5(54, GOLD),
    PLATINUM1(55, PLATINUM),
    PLATINUM2(56, PLATINUM),
    PLATINUM3(57, PLATINUM),
    PLATINUM4(58, PLATINUM),
    PLATINUM5(59, PLATINUM),
    DIAMOND1(60, DIAMOND),
    DIAMOND2(61, DIAMOND),
    DIAMOND3(62, DIAMOND),
    DIAMOND4(63, DIAMOND),
    DIAMOND5(64, DIAMOND),
    COBALT1(70, COBALT),
    COBALT2(71, COBALT),
    COBALT3(72, COBALT),
    COBALT4(73, COBALT),
    COBALT5(74, COBALT),
    AMETHYST1(80, AMETHYST),
    AMETHYST2(81, AMETHYST),
    AMETHYST3(82, AMETHYST),
    AMETHYST4(83, AMETHYST),
    AMETHYST5(84, AMETHYST),
    EMERALD1(90, EMERALD),
    EMERALD2(91, EMERALD),
    EMERALD3(92, EMERALD),
    EMERALD4(93, EMERALD),
    EMERALD5(94, EMERALD),
    ONYX1(100, ONYX),
    ONYX2(101, ONYX),
    ONYX3(102, ONYX),
    ONYX4(103, ONYX),
    ONYX5(104, ONYX),
    ;

    companion object {
        fun parse(s: String?): LadderRank? = try {
            s?.let {
                valueOf(it.toUpperCase()
                    .replace(" IV", "4")
                    .replace(" V", "5")
                    .replace(" III", "3")
                    .replace(" II", "2")
                    .replace(" I", "1"))
            }
        } catch (e: IllegalArgumentException) { null }

        fun parse(stableId: Long?): LadderRank? = stableId?.let { id -> values().firstOrNull { it.stableId == id } }
    }
}

/**
 * Enum describing the groups that Ranks are put into.
 */
enum class LadderRankClass {
    COPPER, BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, COBALT, AMETHYST, EMERALD, ONYX
}

@Serializer(forClass = LadderRank::class)
object LadderRankSerializer: KSerializer<LadderRank> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ladderRank", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder) = LadderRank.parse(decoder.decodeString())!!
    override fun serialize(encoder: Encoder, value: LadderRank) {
        encoder.encodeString(value.name.toLowerCase())
    }
}
