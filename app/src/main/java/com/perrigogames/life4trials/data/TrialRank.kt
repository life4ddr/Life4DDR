package com.perrigogames.life4trials.data

import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName
import com.perrigogames.life4trials.R
import java.io.Serializable

enum class TrialRank(@DrawableRes val drawableRes: Int): Serializable {
    @SerializedName("silver") SILVER(R.drawable.silver_3),
    @SerializedName("gold") GOLD(R.drawable.gold_3),
    @SerializedName("diamond") DIAMOND(R.drawable.diamond_3),
    @SerializedName("cobalt") COBALT(R.drawable.cobalt_3),
    @SerializedName("amethyst") AMETHYST(R.drawable.amethyst_3);

    val next get() = when(this) {
        SILVER -> GOLD
        GOLD -> DIAMOND
        DIAMOND -> COBALT
        COBALT -> AMETHYST
        AMETHYST -> AMETHYST
    }

    val color get() = when(this) {
        SILVER -> R.color.silver
        GOLD -> R.color.gold
        DIAMOND -> R.color.diamond
        COBALT -> R.color.cobalt
        AMETHYST -> R.color.amethyst
    }

    /**
     * Generates a list of this and all [TrialRank]s that are higher than this.
     */
    val andUp: Array<TrialRank>
        get() = values().let { it.copyOfRange(this.ordinal, it.size) }

    companion object {
        fun parse(s: String?): TrialRank? = when (s) {
            null, "NONE" -> null
            else -> valueOf(s)
        }
    }
}