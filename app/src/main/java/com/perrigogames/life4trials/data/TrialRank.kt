package com.perrigogames.life4trials.data

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import com.perrigogames.life4trials.R
import java.io.Serializable

/**
 * Enum class representing a Rank that a player can earn in a LIFE4 Trial.
 */
enum class TrialRank(val stableId: Long,
                     @StringRes val nameRes: Int,
                     val parent: LadderRank): Serializable {
    @SerializedName("wood") WOOD(10, R.string.wood, LadderRank.WOOD3),
    @SerializedName("bronze") BRONZE(15, R.string.bronze, LadderRank.BRONZE3),
    @SerializedName("silver") SILVER(20, R.string.silver, LadderRank.SILVER3),
    @SerializedName("gold") GOLD(25, R.string.gold, LadderRank.GOLD3),
    @SerializedName("platinum") PLATINUM(27, R.string.platinum, LadderRank.PLATINUM3),
    @SerializedName("diamond") DIAMOND(30, R.string.diamond, LadderRank.DIAMOND3),
    @SerializedName("cobalt") COBALT(35, R.string.cobalt, LadderRank.COBALT3),
    @SerializedName("amethyst") AMETHYST(40, R.string.amethyst, LadderRank.AMETHYST3),
    @SerializedName("emerald") EMERALD(45, R.string.emerald, LadderRank.EMERALD3);

    val drawableRes: Int get() = parent.drawableRes

    val next get() = when(this) {
        WOOD -> BRONZE
        BRONZE -> SILVER
        SILVER -> GOLD
        GOLD -> DIAMOND // PLATINUM isn't really used for Trials
        PLATINUM -> DIAMOND
        DIAMOND -> COBALT
        COBALT -> AMETHYST
        AMETHYST -> EMERALD
        EMERALD -> EMERALD
    }

    val colorRes get() = parent.colorRes

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

        fun parse(stableId: Long): TrialRank? = values().firstOrNull { it.stableId == stableId }

        fun fromLadderRank(userRank: LadderRank?) = when(userRank) {
            null -> null
            LadderRank.WOOD1, LadderRank.WOOD2, LadderRank.WOOD3 -> WOOD
            LadderRank.BRONZE1, LadderRank.BRONZE2, LadderRank.BRONZE3 -> BRONZE
            LadderRank.SILVER1, LadderRank.SILVER2, LadderRank.SILVER3 -> SILVER
            LadderRank.GOLD1, LadderRank.GOLD2, LadderRank.GOLD3 -> GOLD
            LadderRank.PLATINUM1, LadderRank.PLATINUM2, LadderRank.PLATINUM3 -> PLATINUM
            LadderRank.DIAMOND1, LadderRank.DIAMOND2, LadderRank.DIAMOND3 -> DIAMOND
            LadderRank.COBALT1, LadderRank.COBALT2, LadderRank.COBALT3 -> COBALT
            LadderRank.AMETHYST1, LadderRank.AMETHYST2, LadderRank.AMETHYST3 -> AMETHYST
            LadderRank.EMERALD1, LadderRank.EMERALD2, LadderRank.EMERALD3 -> EMERALD
        }
    }
}