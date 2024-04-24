package com.perrigogames.life4.data

import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.LadderRankClass
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

enum class PlacementRank(val stableId: Long, val parent: LadderRankClass) {
    COPPER(20, LadderRankClass.COPPER),
    BRONZE(25, LadderRankClass.BRONZE),
    SILVER(30, LadderRankClass.SILVER),
    GOLD(35, LadderRankClass.GOLD);

    @ExperimentalSerializationApi
    fun toLadderRank() = when(this) {
        COPPER -> LadderRank.COPPER3
        BRONZE -> LadderRank.BRONZE3
        SILVER -> LadderRank.SILVER3
        GOLD -> LadderRank.GOLD3
    }
}

@ExperimentalSerializationApi
object PlacementRankSerializer: KSerializer<PlacementRank> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("placementRank", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder) = PlacementRank.valueOf(decoder.decodeString().uppercase())
    override fun serialize(encoder: Encoder, value: PlacementRank) {
        encoder.encodeString(value.name.lowercase())
    }
}
