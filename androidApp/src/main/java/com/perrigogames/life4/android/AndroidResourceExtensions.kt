
package com.perrigogames.life4.android

import androidx.annotation.DrawableRes
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.trials.enums.TrialRank

val LadderRank.drawableRes @DrawableRes get() = when(this) {
    LadderRank.COPPER1 -> R.drawable.copper_1
    LadderRank.COPPER2 -> R.drawable.copper_2
    LadderRank.COPPER3 -> R.drawable.copper_3
    LadderRank.COPPER4 -> R.drawable.copper_4
    LadderRank.COPPER5 -> R.drawable.copper_5
    LadderRank.BRONZE1 -> R.drawable.bronze_1
    LadderRank.BRONZE2 -> R.drawable.bronze_2
    LadderRank.BRONZE3 -> R.drawable.bronze_3
    LadderRank.BRONZE4 -> R.drawable.bronze_4
    LadderRank.BRONZE5 -> R.drawable.bronze_5
    LadderRank.SILVER1 -> R.drawable.silver_1
    LadderRank.SILVER2 -> R.drawable.silver_2
    LadderRank.SILVER3 -> R.drawable.silver_3
    LadderRank.SILVER4 -> R.drawable.silver_4
    LadderRank.SILVER5 -> R.drawable.silver_5
    LadderRank.GOLD1 -> R.drawable.gold_1
    LadderRank.GOLD2 -> R.drawable.gold_2
    LadderRank.GOLD3 -> R.drawable.gold_3
    LadderRank.GOLD4 -> R.drawable.gold_4
    LadderRank.GOLD5 -> R.drawable.gold_5
    LadderRank.PLATINUM1 -> R.drawable.platinum_1
    LadderRank.PLATINUM2 -> R.drawable.platinum_2
    LadderRank.PLATINUM3 -> R.drawable.platinum_3
    LadderRank.PLATINUM4 -> R.drawable.platinum_4
    LadderRank.PLATINUM5 -> R.drawable.platinum_5
    LadderRank.DIAMOND1 -> R.drawable.diamond_1
    LadderRank.DIAMOND2 -> R.drawable.diamond_2
    LadderRank.DIAMOND3 -> R.drawable.diamond_3
    LadderRank.DIAMOND4 -> R.drawable.diamond_4
    LadderRank.DIAMOND5 -> R.drawable.diamond_5
    LadderRank.COBALT1 -> R.drawable.cobalt_1
    LadderRank.COBALT2 -> R.drawable.cobalt_2
    LadderRank.COBALT3 -> R.drawable.cobalt_3
    LadderRank.COBALT4 -> R.drawable.cobalt_4
    LadderRank.COBALT5 -> R.drawable.cobalt_5
    LadderRank.PEARL1 -> R.drawable.pearl_1
    LadderRank.PEARL2 -> R.drawable.pearl_2
    LadderRank.PEARL3 -> R.drawable.pearl_3
    LadderRank.PEARL4 -> R.drawable.pearl_4
    LadderRank.PEARL5 -> R.drawable.pearl_5
    LadderRank.AMETHYST1 -> R.drawable.amethyst_1
    LadderRank.AMETHYST2 -> R.drawable.amethyst_2
    LadderRank.AMETHYST3 -> R.drawable.amethyst_3
    LadderRank.AMETHYST4 -> R.drawable.amethyst_4
    LadderRank.AMETHYST5 -> R.drawable.amethyst_5
    LadderRank.EMERALD1 -> R.drawable.emerald_1
    LadderRank.EMERALD2 -> R.drawable.emerald_2
    LadderRank.EMERALD3 -> R.drawable.emerald_3
    LadderRank.EMERALD4 -> R.drawable.emerald_4
    LadderRank.EMERALD5 -> R.drawable.emerald_5
    LadderRank.ONYX1 -> R.drawable.onyx_1
    LadderRank.ONYX2 -> R.drawable.onyx_2
    LadderRank.ONYX3 -> R.drawable.onyx_3
    LadderRank.ONYX4 -> R.drawable.onyx_4
    LadderRank.ONYX5 -> R.drawable.onyx_5
}

val TrialRank.drawableRes @DrawableRes get() = when(this) {
    TrialRank.COPPER -> R.drawable.copper_3
    TrialRank.BRONZE -> R.drawable.bronze_3
    TrialRank.SILVER -> R.drawable.silver_3
    TrialRank.GOLD -> R.drawable.gold_3
    TrialRank.PLATINUM -> R.drawable.platinum_3
    TrialRank.DIAMOND -> R.drawable.diamond_3
    TrialRank.COBALT -> R.drawable.cobalt_3
    TrialRank.PEARL -> R.drawable.pearl_3
    TrialRank.AMETHYST -> R.drawable.amethyst_3
    TrialRank.EMERALD -> R.drawable.emerald_3
    TrialRank.ONYX -> R.drawable.onyx_3
}
