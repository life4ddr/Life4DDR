package com.perrigogames.life4.data

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

enum class PlacementRank(val stableId: Long, val parent: LadderRankClass) {
    WOOD(20, LadderRankClass.WOOD),
    BRONZE(25, LadderRankClass.BRONZE),
    SILVER(20, LadderRankClass.SILVER),
    GOLD(25, LadderRankClass.GOLD);

    fun toLadderRank() = when(this) {
        WOOD -> LadderRank.WOOD3
        BRONZE -> LadderRank.BRONZE3
        SILVER -> LadderRank.SILVER3
        GOLD -> LadderRank.GOLD3
    }
}

@Serializer(forClass = PlacementRank::class)
object PlacementRankSerializer: KSerializer<PlacementRank> {
    override val descriptor: SerialDescriptor = StringDescriptor
    override fun deserialize(decoder: Decoder) = PlacementRank.valueOf(decoder.decodeString().toUpperCase())
    override fun serialize(encoder: Encoder, obj: PlacementRank) {
        encoder.encodeString(obj.name.toLowerCase())
    }
}
