package com.perrigogames.life4.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = TrialStateSerializer::class)
enum class TrialState(val jsonName: String) {
    NEW("new"),
    EVENT("event"),
    RETIRED("retired"),
    ACTIVE("active"),
    ;

    companion object {
        fun parse(string: String) = values().firstOrNull { it.jsonName == string }
    }
}

object TrialStateSerializer: KSerializer<TrialState> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ladderRank", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder) = TrialState.parse(decoder.decodeString())!!
    override fun serialize(encoder: Encoder, value: TrialState) {
        encoder.encodeString(value.name.lowercase())
    }
}
