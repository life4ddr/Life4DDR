package com.perrigogames.life4.enums

import com.perrigogames.life4.MR
import com.perrigogames.life4.data.StableId
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Enum to describe a style of play, distinguishing between charts that use one pad
 * versus charts that use both.
 */
enum class PlayStyle(
    override val stableId: Long,
    val aggregateSuffix: String,
    val uiName: StringDesc
): StableId {
    SINGLE(1, "SP", StringDesc.Resource(MR.strings.play_style_single)),
    DOUBLE(2, "DP", StringDesc.Resource(MR.strings.play_style_double));

    fun aggregateString(difficultyClass: DifficultyClass) = "${difficultyClass.aggregatePrefix}$aggregateSuffix"

    companion object {
        fun parse(stableId: Long?) = stableId?.let { id -> entries.firstOrNull { it.stableId == id } }
        fun parse(chartString: String): PlayStyle? = entries.firstOrNull { chartString.endsWith(it.aggregateSuffix) }
    }
}

object PlayStyleSerializer: KSerializer<PlayStyle> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("playStyle", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder) = decoder.decodeString().let {
        PlayStyle.parse(it) ?: PlayStyle.valueOf(it.uppercase())
    }
    override fun serialize(encoder: Encoder, value: PlayStyle) {
        encoder.encodeString(value.name.lowercase())
    }
}
