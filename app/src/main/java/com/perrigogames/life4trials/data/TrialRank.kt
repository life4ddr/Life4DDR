package com.perrigogames.life4trials.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Enum class representing a Rank that a player can earn in a LIFE4 Trial.
 */
enum class TrialRank(val stableId: Int,
                     val parent: LadderRank): Serializable {
    @SerializedName("silver") SILVER(20, LadderRank.SILVER3),
    @SerializedName("gold") GOLD(25, LadderRank.GOLD3),
    @SerializedName("diamond") DIAMOND(30, LadderRank.DIAMOND3),
    @SerializedName("cobalt") COBALT(35, LadderRank.COBALT3),
    @SerializedName("amethyst") AMETHYST(40, LadderRank.AMETHYST3);

    val drawableRes: Int get() = parent.drawableRes

    val next get() = when(this) {
        SILVER -> GOLD
        GOLD -> DIAMOND
        DIAMOND -> COBALT
        COBALT -> AMETHYST
        AMETHYST -> AMETHYST
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