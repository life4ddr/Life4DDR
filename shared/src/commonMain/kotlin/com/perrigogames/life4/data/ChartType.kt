package com.perrigogames.life4.data

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

/**
 * Enum to describe a class of difficulty, inside which more specific difficulties
 * are more or less in the same range as each other.
 */
enum class DifficultyClass(val stableId: Long,
                           val aggregatePrefix: String) {
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

/**
 * Enum to describe a style of play, distinguishing between charts that use one pad
 * versus charts that use both.
 */
enum class PlayStyle(val stableId: Long,
                     val aggregateSuffix: String) {
    SINGLE(1, "SP"),
    DOUBLE(2, "DP");

    fun aggregateString(difficultyClass: DifficultyClass) = "${difficultyClass.aggregatePrefix}$aggregateSuffix"

    companion object {
        fun parse(stableId: Long?) = stableId?.let { id -> values().firstOrNull { it.stableId == id } }
        fun parse(chartString: String): PlayStyle? = values().firstOrNull { chartString.endsWith(it.aggregateSuffix) }
    }
}

data class ChartType(val style: PlayStyle,
                     val difficulty: DifficultyClass)

@Serializer(forClass = DifficultyClass::class)
object DifficultyClassSerializer: KSerializer<DifficultyClass> {
    override val descriptor: SerialDescriptor = StringDescriptor
    override fun deserialize(decoder: Decoder) = DifficultyClass.parse(decoder.decodeString())!!
    override fun serialize(encoder: Encoder, obj: DifficultyClass) {
        encoder.encodeString(obj.name.toLowerCase())
    }
}

@Serializer(forClass = PlayStyle::class)
object PlayStyleSerializer: KSerializer<PlayStyle> {
    override val descriptor: SerialDescriptor = StringDescriptor
    override fun deserialize(decoder: Decoder) = PlayStyle.parse(decoder.decodeString())!!
    override fun serialize(encoder: Encoder, obj: PlayStyle) {
        encoder.encodeString(obj.name.toLowerCase())
    }
}

@Serializer(forClass = ChartType::class)
object ChartTypeSerializer: KSerializer<ChartType> {
    override val descriptor: SerialDescriptor = StringDescriptor
    override fun deserialize(decoder: Decoder): ChartType {
        val input = decoder.decodeString()
        return ChartType(PlayStyle.parse(input)!!,
            DifficultyClass.parse(input)!!)
    }
    override fun serialize(encoder: Encoder, obj: ChartType) {
        encoder.encodeString("${obj.difficulty.aggregatePrefix}${obj.style.aggregateSuffix}")
    }
}
