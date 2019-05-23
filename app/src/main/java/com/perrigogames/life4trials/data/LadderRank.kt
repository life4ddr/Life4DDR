package com.perrigogames.life4trials.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import com.perrigogames.life4trials.R

/**
 * Enum class representing a Rank that a player can earn in LIFE4.
 */
enum class LadderRank(@DrawableRes val drawableRes: Int,
                      @StringRes val nameRes: Int) {
    @SerializedName("wood1") WOOD1(R.drawable.wood_1, R.string.wood_1),
    @SerializedName("wood2") WOOD2(R.drawable.wood_2, R.string.wood_2),
    @SerializedName("wood3") WOOD3(R.drawable.wood_3, R.string.wood_3),
    @SerializedName("bronze1") BRONZE1(R.drawable.bronze_1, R.string.bronze_1),
    @SerializedName("bronze2") BRONZE2(R.drawable.bronze_2, R.string.bronze_2),
    @SerializedName("bronze3") BRONZE3(R.drawable.bronze_3, R.string.bronze_3),
    @SerializedName("silver1") SILVER1(R.drawable.silver_1, R.string.silver_1),
    @SerializedName("silver2") SILVER2(R.drawable.silver_2, R.string.silver_2),
    @SerializedName("silver3") SILVER3(R.drawable.silver_3, R.string.silver_3),
    @SerializedName("gold1") GOLD1(R.drawable.gold_1, R.string.gold_1),
    @SerializedName("gold2") GOLD2(R.drawable.gold_2, R.string.gold_2),
    @SerializedName("gold3") GOLD3(R.drawable.gold_3, R.string.gold_3),
    @SerializedName("diamond1") DIAMOND1(R.drawable.diamond_1, R.string.diamond_1),
    @SerializedName("diamond2") DIAMOND2(R.drawable.diamond_2, R.string.diamond_2),
    @SerializedName("diamond3") DIAMOND3(R.drawable.diamond_3, R.string.diamond_3),
    @SerializedName("cobalt1") COBALT1(R.drawable.cobalt_1, R.string.cobalt_1),
    @SerializedName("cobalt2") COBALT2(R.drawable.cobalt_2, R.string.cobalt_2),
    @SerializedName("cobalt3") COBALT3(R.drawable.cobalt_3, R.string.cobalt_3),
    @SerializedName("amethyst1") AMETHYST1(R.drawable.amethyst_1, R.string.amethyst_1),
    @SerializedName("amethyst2") AMETHYST2(R.drawable.amethyst_2, R.string.amethyst_2),
    @SerializedName("amethyst3") AMETHYST3(R.drawable.amethyst_3, R.string.amethyst_3);

    val color get() = when(this) {
        WOOD1, WOOD2, WOOD3 -> R.color.wood
        BRONZE1, BRONZE2, BRONZE3 -> R.color.bronze
        SILVER1, SILVER2, SILVER3 -> R.color.silver
        GOLD1, GOLD2, GOLD3 -> R.color.gold
        DIAMOND1, DIAMOND2, DIAMOND3 -> R.color.diamond
        COBALT1, COBALT2, COBALT3 -> R.color.cobalt
        AMETHYST1, AMETHYST2, AMETHYST3 -> R.color.amethyst
    }

    companion object {
        fun parse(s: String?): LadderRank? = when (s) {
            null, "NONE" -> null
            else -> valueOf(s)
        }
    }
}