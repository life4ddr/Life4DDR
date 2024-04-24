package com.perrigogames.life4.enums

import com.perrigogames.life4.data.StableId
import kotlinx.serialization.ExperimentalSerializationApi
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
enum class PlayStyle(override val stableId: Long,
                     val aggregateSuffix: String): StableId {
    SINGLE(1, "SP"),
    DOUBLE(2, "DP");

    fun aggregateString(difficultyClass: DifficultyClass) = "${difficultyClass.aggregatePrefix}$aggregateSuffix"

    companion object {
        fun parse(stableId: Long?) = stableId?.let { id -> entries.firstOrNull { it.stableId == id } }
        fun parse(chartString: String): PlayStyle? = entries.firstOrNull { chartString.endsWith(it.aggregateSuffix) }
    }
}

@ExperimentalSerializationApi
object PlayStyleSerializer: KSerializer<PlayStyle> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("playStyle", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder) = decoder.decodeString().let {
        PlayStyle.parse(it) ?: PlayStyle.valueOf(it.uppercase())
    }
    override fun serialize(encoder: Encoder, value: PlayStyle) {
        encoder.encodeString(value.name.lowercase())
    }
}
