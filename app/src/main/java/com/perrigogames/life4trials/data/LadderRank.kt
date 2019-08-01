package com.perrigogames.life4trials.data

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.LadderRankGroup.*

/**
 * Enum class representing a Rank that a player can earn in LIFE4.
 */
enum class LadderRank(val stableId: Long,
                      @DrawableRes val drawableRes: Int,
                      @StringRes val nameRes: Int, 
                      val group: LadderRankGroup) {
    @SerializedName("wood1") WOOD1(20, R.drawable.wood_1, R.string.wood_1, WOOD),
    @SerializedName("wood2") WOOD2(21, R.drawable.wood_2, R.string.wood_2, WOOD),
    @SerializedName("wood3") WOOD3(22, R.drawable.wood_3, R.string.wood_3, WOOD),
    @SerializedName("bronze1") BRONZE1(30, R.drawable.bronze_1, R.string.bronze_1, BRONZE),
    @SerializedName("bronze2") BRONZE2(31, R.drawable.bronze_2, R.string.bronze_2, BRONZE),
    @SerializedName("bronze3") BRONZE3(32, R.drawable.bronze_3, R.string.bronze_3, BRONZE),
    @SerializedName("silver1") SILVER1(40, R.drawable.silver_1, R.string.silver_1, SILVER),
    @SerializedName("silver2") SILVER2(41, R.drawable.silver_2, R.string.silver_2, SILVER),
    @SerializedName("silver3") SILVER3(42, R.drawable.silver_3, R.string.silver_3, SILVER),
    @SerializedName("gold1") GOLD1(50, R.drawable.gold_1, R.string.gold_1, GOLD),
    @SerializedName("gold2") GOLD2(51, R.drawable.gold_2, R.string.gold_2, GOLD),
    @SerializedName("gold3") GOLD3(52, R.drawable.gold_3, R.string.gold_3, GOLD),
    @SerializedName("diamond1") DIAMOND1(60, R.drawable.diamond_1, R.string.diamond_1, DIAMOND),
    @SerializedName("diamond2") DIAMOND2(61, R.drawable.diamond_2, R.string.diamond_2, DIAMOND),
    @SerializedName("diamond3") DIAMOND3(62, R.drawable.diamond_3, R.string.diamond_3, DIAMOND),
    @SerializedName("cobalt1") COBALT1(70, R.drawable.cobalt_1, R.string.cobalt_1, COBALT),
    @SerializedName("cobalt2") COBALT2(71, R.drawable.cobalt_2, R.string.cobalt_2, COBALT),
    @SerializedName("cobalt3") COBALT3(72, R.drawable.cobalt_3, R.string.cobalt_3, COBALT),
    @SerializedName("amethyst1") AMETHYST1(80, R.drawable.amethyst_1, R.string.amethyst_1, AMETHYST),
    @SerializedName("amethyst2") AMETHYST2(81, R.drawable.amethyst_2, R.string.amethyst_2, AMETHYST),
    @SerializedName("amethyst3") AMETHYST3(82, R.drawable.amethyst_3, R.string.amethyst_3, AMETHYST),
    @SerializedName("emerald1") EMERALD1(90, R.drawable.emerald_1, R.string.emerald_1, EMERALD),
    @SerializedName("emerald2") EMERALD2(91, R.drawable.emerald_2, R.string.emerald_2, EMERALD),
    @SerializedName("emerald3") EMERALD3(92, R.drawable.emerald_3, R.string.emerald_3, EMERALD);

    val groupNameRes @StringRes get() = group.nameRes
    val colorRes @ColorRes get() = group.colorRes

    companion object {
        fun parse(s: String?): LadderRank? = try {
            s?.let {
                valueOf(it.toUpperCase()
                    .replace(" III", "3")
                    .replace(" II", "2")
                    .replace(" I", "1"))
            }
        } catch (e: IllegalArgumentException) { null }

        fun parse(stableId: Long?): LadderRank? = stableId?.let { id -> values().firstOrNull { it.stableId == id } }
    }
}

enum class LadderRankGroup(@StringRes val nameRes: Int,
                           @ColorRes val colorRes: Int) {
    WOOD(R.string.wood, R.color.wood),
    BRONZE(R.string.bronze, R.color.bronze),
    SILVER(R.string.silver, R.color.silver),
    GOLD(R.string.gold, R.color.gold),
    DIAMOND(R.string.diamond, R.color.diamond),
    COBALT(R.string.cobalt, R.color.cobalt),
    AMETHYST(R.string.amethyst, R.color.amethyst),
    EMERALD(R.string.emerald, R.color.emerald)
}