package com.perrigogames.life4trials.data

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import com.perrigogames.life4trials.R
import io.objectbox.converter.PropertyConverter

/**
 * Enum to describe the possible ways to finish a song, and consequently an
 * entire folder.
 * @param lampRes the text for describing a lamp ("Green lamp (GFC)")
 * @param clearRes the text for describing a clear ("Great Full Combo")
 * @param clearResShort the text for describing a clear as an abbreviation ("GFC", "PFC")
 */
enum class ClearType(val stableId: Long,
                     @StringRes val lampRes: Int?,
                     @StringRes val clearRes: Int,
                     @StringRes val clearResShort: Int,
                     @ColorRes val colorRes: Int,
                     val passing: Boolean = true) {
    @SerializedName(NO_PLAY_KEY) NO_PLAY(0, null, R.string.not_played, R.string.not_played, R.color.no_play, false),
    @SerializedName(FAIL_KEY) FAIL(1, null, R.string.fail, R.string.fail, R.color.fail, false),
    @SerializedName(CLEAR_KEY) CLEAR(2, R.string.lamp_clear, R.string.clear, R.string.clear, R.color.clear),
    @SerializedName(LIFE4C_KEY, alternate = [LIFE4_KEY]) LIFE4_CLEAR(3, R.string.lamp_life4, R.string.clear_life4, R.string.clear_life4, R.color.life4),
    @SerializedName(GOOD_KEY, alternate = [FC_KEY]) GOOD_FULL_COMBO(4, R.string.lamp_fc, R.string.clear_fc, R.string.clear_fc_short, R.color.good),
    @SerializedName(GREAT_KEY, alternate = [GFC_KEY]) GREAT_FULL_COMBO(5, R.string.lamp_gfc, R.string.clear_gfc, R.string.clear_gfc_short, R.color.great),
    @SerializedName(PERFECT_KEY, alternate = [PFC_KEY]) PERFECT_FULL_COMBO(6, R.string.lamp_pfc, R.string.clear_pfc, R.string.clear_pfc_short, R.color.perfect),
    @SerializedName(MARVELOUS_KEY, alternate = [MFC_KEY]) MARVELOUS_FULL_COMBO(7, R.string.lamp_mfc, R.string.clear_mfc, R.string.clear_mfc_short, R.color.marvelous);

    companion object {
        const val NO_PLAY_KEY = "no_play"
        const val FAIL_KEY = "fail"
        const val CLEAR_KEY = "clear"
        const val LIFE4C_KEY = "life4_clear"
        const val LIFE4_KEY = "life4"
        const val GOOD_KEY = "good"
        const val FC_KEY = "fc"
        const val GREAT_KEY = "great"
        const val GFC_KEY = "gfc"
        const val PERFECT_KEY = "perfect"
        const val PFC_KEY = "pfc"
        const val MARVELOUS_KEY = "marvelous"
        const val MFC_KEY = "mfc"

        fun parse(stableId: Long?) = stableId?.let { id -> values().firstOrNull { it.stableId == id } }

        fun parse(v: String) = when(v) {
            FAIL_KEY -> FAIL
            CLEAR_KEY -> CLEAR
            LIFE4_KEY -> LIFE4_CLEAR
            FC_KEY,
            GOOD_KEY -> GOOD_FULL_COMBO
            GFC_KEY,
            GREAT_KEY -> GREAT_FULL_COMBO
            PFC_KEY,
            PERFECT_KEY -> PERFECT_FULL_COMBO
            MFC_KEY,
            MARVELOUS_KEY -> MARVELOUS_FULL_COMBO
            else -> null
        }
    }
}

class ClearTypeConverter: PropertyConverter<ClearType, Long> {
    override fun convertToDatabaseValue(property: ClearType?): Long = (property ?: ClearType.NO_PLAY).stableId
    override fun convertToEntityProperty(id: Long?): ClearType? = ClearType.parse(id)
}