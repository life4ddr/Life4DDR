package com.perrigogames.life4trials.data

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import com.perrigogames.life4trials.R

/**
 * Enum to describe the possible ways to finish a song, and consequently an
 * entire folder.
 * @param lampRes the text for describing a lamp ("Green lamp (GFC)")
 * @param clearRes the text for describing a clear ("Great Full Combo")
 * @param clearResShort the text for describing a clear as an abbreviation ("GFC", "PFC")
 */
enum class ClearType(@StringRes val lampRes: Int?,
                     @StringRes val clearRes: Int,
                     @StringRes val clearResShort: Int,
                     val passing: Boolean = true) {
    @SerializedName("fail") FAIL(null, R.string.fail, R.string.fail, false),
    @SerializedName("clear") CLEAR(R.string.lamp_clear, R.string.clear, R.string.clear),
    @SerializedName("life4_clear", alternate = ["life4"]) LIFE4_CLEAR(R.string.lamp_life4, R.string.clear_life4, R.string.clear_life4),
    @SerializedName("good") GOOD_FULL_COMBO(R.string.lamp_fc, R.string.clear_fc, R.string.clear_fc_short),
    @SerializedName("great") GREAT_FULL_COMBO(R.string.lamp_gfc, R.string.clear_gfc, R.string.clear_gfc_short),
    @SerializedName("perfect") PERFECT_FULL_COMBO(R.string.lamp_pfc, R.string.clear_pfc, R.string.clear_pfc_short),
    @SerializedName("marvelous") MARVELOUS_FULL_COMBO(R.string.lamp_mfc, R.string.clear_mfc, R.string.clear_mfc_short)
}