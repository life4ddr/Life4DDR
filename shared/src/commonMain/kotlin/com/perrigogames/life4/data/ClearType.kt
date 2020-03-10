package com.perrigogames.life4.data

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

/**
 * Enum to describe the possible ways to finish a song, and consequently an
 * entire folder.
 * @param lampRes the text for describing a lamp ("Green lamp (GFC)")
 * @param clearRes the text for describing a clear ("Great Full Combo")
 * @param clearResShort the text for describing a clear as an abbreviation ("GFC", "PFC")
 */
enum class ClearType(val stableId: Long, val serialized: List<String>, val passing: Boolean = true) {
    NO_PLAY(0, "no_play", false),
    FAIL(1, "fail", false),
    CLEAR(2, "clear"),
    LIFE4_CLEAR(3, listOf("life4", "life4_clear")),
    GOOD_FULL_COMBO(4, listOf("fc", "good")),
    GREAT_FULL_COMBO(5, listOf("gfc", "great")),
    PERFECT_FULL_COMBO(6, listOf("pfc", "perfect")),
    MARVELOUS_FULL_COMBO(7, listOf("mfc", "marvelous"));

    constructor(stableId: Long, serialized: String, passing: Boolean = true): this(stableId, listOf(serialized), passing)

    companion object {
        fun parse(stableId: Long?) = stableId?.let { id -> values().firstOrNull { it.stableId == id } }

        fun parse(v: String) = values().firstOrNull { it.serialized.contains(v) }
    }
}

@Serializer(forClass = ClearType::class)
object ClearTypeSerializer: KSerializer<ClearType> {
    override val descriptor: SerialDescriptor = StringDescriptor
    override fun deserialize(decoder: Decoder) = ClearType.parse(decoder.decodeString())!!
    override fun serialize(encoder: Encoder, obj: ClearType) {
        encoder.encodeString(obj.serialized.first())
    }
}
