package com.perrigogames.life4.feature.trials.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
enum class TrialType {
    @SerialName("trial") TRIAL,
    @SerialName("placement") PLACEMENT,
    @SerialName("event") EVENT
}

object TrialTypeSerializer: KSerializer<TrialType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("trialType", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder) = TrialType.valueOf(decoder.decodeString().uppercase())
    override fun serialize(encoder: Encoder, value: TrialType) {
        encoder.encodeString(value.name.lowercase())
    }
}
