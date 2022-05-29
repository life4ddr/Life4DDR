package com.perrigogames.life4.data

import com.perrigogames.life4.enums.DifficultyClass
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

data class DifficultyClassSet(
    val set: List<DifficultyClass>,
    val requireAll: Boolean,
) {

    constructor(
        difficulty: DifficultyClass,
        requireAll: Boolean
    ) : this(listOf(difficulty), requireAll)

    fun match(difficultyClass: DifficultyClass) = set.contains(difficultyClass)

    override fun toString(): String =
        set.joinToString(
            separator = "",
            postfix = if (requireAll) "*" else ""
        ) { it.aggregatePrefix }

    companion object {

        fun parse(sourceString: String): DifficultyClassSet {
            var requireAll = false
            val difficulties = sourceString.mapNotNull { ch ->
                when (ch) {
                    'b' -> DifficultyClass.BEGINNER
                    'B' -> DifficultyClass.BASIC
                    'D' -> DifficultyClass.DIFFICULT
                    'E' -> DifficultyClass.EXPERT
                    'C' -> DifficultyClass.CHALLENGE
                    '*' -> {
                        requireAll = true
                        null
                    }
                    else -> throw Exception("Illegal difficulty string character ($ch)")
                }
            }
            return DifficultyClassSet(difficulties, requireAll)
        }
    }
}

@ExperimentalSerializationApi
@Serializer(forClass = DifficultyClassSet::class)
object DifficultyClassSetSerializer: KSerializer<DifficultyClassSet> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "difficultyClassSet",
        kind = PrimitiveKind.STRING,
    )
    override fun deserialize(decoder: Decoder) = DifficultyClassSet.parse(decoder.decodeString())
    override fun serialize(encoder: Encoder, value: DifficultyClassSet) {
        encoder.encodeString(value.toString())
    }
}