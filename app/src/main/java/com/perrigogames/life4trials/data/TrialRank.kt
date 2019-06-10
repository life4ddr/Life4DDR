package com.perrigogames.life4trials.data

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import com.perrigogames.life4trials.R
import java.io.Serializable

/**
 * Enum class representing a Rank that a player can earn in a LIFE4 Trial.
 */
enum class TrialRank(val stableId: Int,
                     @StringRes val nameRes: Int,
                     val parent: LadderRank): Serializable {
    @SerializedName("silver") SILVER(20, R.string.trial_silver, LadderRank.SILVER3),
    @SerializedName("gold") GOLD(25, R.string.trial_gold, LadderRank.GOLD3),
    @SerializedName("diamond") DIAMOND(30, R.string.trial_diamond, LadderRank.DIAMOND3),
    @SerializedName("cobalt") COBALT(35, R.string.trial_cobalt, LadderRank.COBALT3),
    @SerializedName("amethyst") AMETHYST(40, R.string.trial_amethyst, LadderRank.AMETHYST3),
    @SerializedName("emerald") EMERALD(45, R.string.trial_emerald, LadderRank.EMERALD3);

    val drawableRes: Int get() = parent.drawableRes

    val next get() = when(this) {
        SILVER -> GOLD
        GOLD -> DIAMOND
        DIAMOND -> COBALT
        COBALT -> AMETHYST
        AMETHYST -> EMERALD
        EMERALD -> EMERALD
    }

    val color get() = parent.color

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

        fun parse(stableId: Int): TrialRank? = values().firstOrNull { it.stableId == stableId }
    }
}