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
 * Enum to describe a class of difficulty, inside which more specific difficulties
 * are more or less in the same range as each other.
 */
enum class DifficultyClass(override val stableId: Long,
                           val aggregatePrefix: String): StableId {
    BEGINNER(1, "b"),
    BASIC(2, "B"),
    DIFFICULT(3, "D"),
    EXPERT(4, "E"),
    CHALLENGE(5, "C");

    fun aggregateString(playStyle: PlayStyle) = playStyle.aggregateString(this)

    companion object {
        fun parse(stableId: Long?) = stableId?.let { id -> values().firstOrNull { it.stableId == id } }
        fun parse(chartString: String): DifficultyClass? = values().firstOrNull { chartString.startsWith(it.aggregatePrefix) }
    }
}

@ExperimentalSerializationApi
object DifficultyClassSerializer: KSerializer<DifficultyClass> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("difficultyClass", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder) = decoder.decodeString().let {
        DifficultyClass.parse(it) ?: DifficultyClass.valueOf(it.uppercase())
    }
    override fun serialize(encoder: Encoder, value: DifficultyClass) {
        encoder.encodeString(value.name.lowercase())
    }
}
