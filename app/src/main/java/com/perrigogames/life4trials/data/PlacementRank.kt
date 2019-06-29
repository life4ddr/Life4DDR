package com.perrigogames.life4trials.data

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import com.perrigogames.life4trials.R

enum class PlacementRank(val stableId: Long,
                         @StringRes val nameRes: Int,
                         val parent: LadderRank) {

    @SerializedName("wood") WOOD(20, R.string.wood, LadderRank.WOOD3),
    @SerializedName("bronze") BRONZE(25, R.string.bronze, LadderRank.BRONZE3),
    @SerializedName("silver") SILVER(20, R.string.silver, LadderRank.SILVER3),
    @SerializedName("gold") GOLD(25, R.string.gold, LadderRank.GOLD3),
}