package com.perrigogames.life4.enums

import com.perrigogames.life4.MR
import com.perrigogames.life4.data.StableId
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

enum class GameVersion(
    override val stableId: Long,
    val uiString: StringDesc,
): StableId {
    UNKNOWN(0, MR.strings.version_unknown.desc()),
    DDR_1ST_MIX(1, MR.strings.version_1st_mix.desc()),
    DDR_2ND_MIX(2, MR.strings.version_2nd_mix.desc()),
    DDR_3RD_MIX(3, MR.strings.version_3rd_mix.desc()),
    DDR_4TH_MIX(4, MR.strings.version_4th_mix.desc()),
    DDR_5TH_MIX(5, MR.strings.version_5th_mix.desc()),
    MAX(6, MR.strings.version_max.desc()),
    MAX2(7, MR.strings.version_max2.desc()),
    EXTREME(8, MR.strings.version_extreme.desc()),
    SUPERNOVA(9, MR.strings.version_supernova.desc()),
    SUPERNOVA2(10, MR.strings.version_supernova.desc()),
    X(11, MR.strings.version_x.desc()),
    DDR_X2(12, MR.strings.version_x_2.desc()),
    DDR_X3_VS_2ND_MIX(13, MR.strings.version_x_3.desc()),
    DDR_2013(14, MR.strings.version_2013.desc()),
    DDR_2014(15, MR.strings.version_2014.desc()),
    DDR_A(16, MR.strings.version_a.desc()),
    DDR_A20(17, MR.strings.version_a20.desc()),
    DDR_A20_PLUS(18, MR.strings.version_a20_plus.desc()),
    DDR_A3(19, MR.strings.version_a3.desc()),
    DDR_WORLD(20, MR.strings.version_world.desc());
    
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
