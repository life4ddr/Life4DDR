package com.perrigogames.life4.enums

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

data class ChartType(val style: PlayStyle,
                     val difficulty: DifficultyClass
) {
    override fun toString(): String = difficulty.aggregatePrefix + style.aggregateSuffix
}

operator fun PlayStyle.plus(difficulty: DifficultyClass) = ChartType(this, difficulty)
operator fun DifficultyClass.plus(style: PlayStyle) = ChartType(style, this)

object ChartTypeSerializer: KSerializer<ChartType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("chartType", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): ChartType {
        val input = decoder.decodeString()
        return ChartType(PlayStyle.parse(input)!!, DifficultyClass.parse(input)!!)
    }
    override fun serialize(encoder: Encoder, value: ChartType) {
        encoder.encodeString("${value.difficulty.aggregatePrefix}${value.style.aggregateSuffix}")
    }
}
