package com.perrigogames.life4.enums

import com.perrigogames.life4.data.StableId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Enum to describe a class of difficulty, inside which more specific difficulties
 * are more or less in the same range as each other.
 */
@Serializable
enum class DifficultyClass(
    override val stableId: Long,
    val aggregatePrefix: String
): StableId {
    @SerialName("beginner") BEGINNER(1, "b"),
    @SerialName("basic") BASIC(2, "B"),
    @SerialName("difficult") DIFFICULT(3, "D"),
    @SerialName("expert") EXPERT(4, "E"),
    @SerialName("challenge") CHALLENGE(5, "C");

    fun aggregateString(playStyle: PlayStyle) = playStyle.aggregateString(this)

    companion object {
        fun parse(stableId: Long?) = stableId?.let { id -> entries.firstOrNull { it.stableId == id } }
        fun parse(chartString: String): DifficultyClass? = entries.firstOrNull { chartString.startsWith(it.aggregatePrefix) }
    }
}

//object DifficultyClassSerializer: KSerializer<DifficultyClass> {
//    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("difficultyClass", PrimitiveKind.STRING)
//    override fun deserialize(decoder: Decoder) = decoder.decodeString().let {
//        DifficultyClass.parse(it) ?: DifficultyClass.valueOf(it.uppercase())
//    }
//    override fun serialize(encoder: Encoder, value: DifficultyClass) {
//        encoder.encodeString(value.name.lowercase())
//    }
//}
