package com.perrigogames.life4.enums

import com.perrigogames.life4.MR
import com.perrigogames.life4.data.StableId
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Enum to describe the possible ways to finish a song, and consequently an
 * entire folder.
 * @param lampRes the text for describing a lamp ("Green lamp (GFC)")
 * @param clearRes the text for describing a clear ("Great Full Combo")
 * @param clearResShort the text for describing a clear as an abbreviation ("GFC", "PFC")
 */
enum class ClearType(
    override val stableId: Long,
    val serialized: List<String>,
    val uiName: StringDesc,
    val passing: Boolean = true,
): StableId {
    NO_PLAY(0, "no_play", StringDesc.Resource(MR.strings.not_played), false),
    FAIL(1, "fail", StringDesc.Resource(MR.strings.fail), false),
    CLEAR(2, "clear", StringDesc.Resource(MR.strings.clear)),
    LIFE4_CLEAR(3, listOf("life4", "life4_clear"), StringDesc.Resource(MR.strings.clear_life4)),
    GOOD_FULL_COMBO(4, listOf("fc", "good"), StringDesc.Resource(MR.strings.clear_fc)),
    GREAT_FULL_COMBO(5, listOf("gfc", "great"), StringDesc.Resource(MR.strings.clear_gfc)),
    PERFECT_FULL_COMBO(6, listOf("pfc", "perfect"), StringDesc.Resource(MR.strings.clear_pfc)),
    MARVELOUS_FULL_COMBO(7, listOf("mfc", "marvelous"), StringDesc.Resource(MR.strings.clear_mfc));

    constructor(
        stableId: Long,
        serialized: String,
        uiName: StringDesc,
        passing: Boolean = true
    ): this(stableId, listOf(serialized), uiName, passing)

    companion object {
        fun parse(stableId: Long?) = stableId?.let { id -> entries.firstOrNull { it.stableId == id } }

        fun parse(v: String) = entries.firstOrNull { it.serialized.contains(v) }

        /**
         * Parse the info received from a ScoreAttack update into a proper clear type
         */
        fun parseSA(grade: String, fullCombo: String): ClearType {
            return when {
                grade == "NoPlay" -> NO_PLAY
                fullCombo == "MerverousFullCombo" -> MARVELOUS_FULL_COMBO
                fullCombo == "PerfectFullCombo" -> PERFECT_FULL_COMBO
                fullCombo == "FullCombo" -> GREAT_FULL_COMBO
                fullCombo == "GoodFullCombo"-> GOOD_FULL_COMBO
                fullCombo == "Life4"-> LIFE4_CLEAR
                else -> CLEAR
            }
        }
    }
}

object ClearTypeSerializer: KSerializer<ClearType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("clearType", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder) = ClearType.parse(
        decoder.decodeString()
    )!!
    override fun serialize(encoder: Encoder, value: ClearType) {
        encoder.encodeString(value.serialized.first())
    }
}
