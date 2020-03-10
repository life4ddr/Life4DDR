package com.perrigogames.life4.data

import com.perrigogames.life4.data.LadderRankClass.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

/**
 * Enum class representing a Rank that a player can earn in LIFE4.
 */
enum class LadderRank(val stableId: Long,
                      val group: LadderRankClass) {
    WOOD1(20, WOOD),
    WOOD2(21, WOOD),
    WOOD3(22, WOOD),
    BRONZE1(30, BRONZE),
    BRONZE2(31, BRONZE),
    BRONZE3(32, BRONZE),
    SILVER1(40, SILVER),
    SILVER2(41, SILVER),
    SILVER3(42, SILVER),
    GOLD1(50, GOLD),
    GOLD2(51, GOLD),
    GOLD3(52, GOLD),
    PLATINUM1(55, PLATINUM),
    PLATINUM2(56, PLATINUM),
    PLATINUM3(57, PLATINUM),
    DIAMOND1(60, DIAMOND),
    DIAMOND2(61, DIAMOND),
    DIAMOND3(62, DIAMOND),
    COBALT1(70, COBALT),
    COBALT2(71, COBALT),
    COBALT3(72, COBALT),
    AMETHYST1(80, AMETHYST),
    AMETHYST2(81, AMETHYST),
    AMETHYST3(82, AMETHYST),
    EMERALD1(90, EMERALD),
    EMERALD2(91, EMERALD),
    EMERALD3(92, EMERALD);

    companion object {
        fun parse(s: String?): LadderRank? = try {
            s?.let {
                valueOf(it.toUpperCase()
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
    WOOD, BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, COBALT, AMETHYST, EMERALD
}

@Serializer(forClass = LadderRank::class)
object LadderRankSerializer: KSerializer<LadderRank> {
    override val descriptor: SerialDescriptor = StringDescriptor
    override fun deserialize(decoder: Decoder) = LadderRank.parse(decoder.decodeString())!!
    override fun serialize(encoder: Encoder, obj: LadderRank) {
        encoder.encodeString(obj.name.toLowerCase())
    }
}
