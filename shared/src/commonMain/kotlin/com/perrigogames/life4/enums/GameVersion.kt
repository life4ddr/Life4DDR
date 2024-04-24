package com.perrigogames.life4.enums

import com.perrigogames.life4.data.StableId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

enum class GameVersion(override val stableId: Long): StableId {
    UNKNOWN(0),
    DDR_1ST_MIX(1),
    DDR_2ND_MIX(2),
    DDR_3RD_MIX(3),
    DDR_4TH_MIX(4),
    DDR_5TH_MIX(5),
    MAX(6),
    MAX2(7),
    EXTREME(8),
    SUPERNOVA(9),
    SUPERNOVA2(10),
    X(11),
    DDR_X2(12),
    DDR_X3_VS_2ND_MIX(13),
    DDR_2013(14),
    DDR_2014(15),
    DDR_A(16),
    DDR_A20(17),
    DDR_A20_PLUS(18),
    DDR_A3(19);
    
    val printName = name.replace("_", " ")

    companion object {
        fun parse(stableId: Long?) = stableId?.let { id ->
            entries.firstOrNull {
                it.stableId == id
            }
        }
        fun parse(name: String?) = name?.let { versionName ->
            entries.firstOrNull {
                versionName.lowercase().replace(" ", "_") == it.name.lowercase()
            }
        }
    }
}

object GameVersionSerializer: KSerializer<GameVersion> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("gameVersion", PrimitiveKind.LONG)
    override fun deserialize(decoder: Decoder) = GameVersion.parse(
        decoder.decodeLong()
    )!!
    override fun serialize(encoder: Encoder, value: GameVersion) {
        encoder.encodeLong(value.stableId)
    }
}
