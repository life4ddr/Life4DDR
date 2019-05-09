package com.perrigogames.life4trials.data

import androidx.annotation.DrawableRes
import com.perrigogames.life4trials.R
import com.squareup.moshi.Json

enum class TrialRank(@DrawableRes val drawableRes: Int) {
    @Json(name="silver") SILVER(R.drawable.silver_3),
    @Json(name="gold") GOLD(R.drawable.gold_3),
    @Json(name="diamond") DIAMOND(R.drawable.diamond_3),
    @Json(name="cobalt") COBALT(R.drawable.cobalt_3),
    @Json(name="amethyst") AMETHYST(R.drawable.amethyst_3);

    val next get() = when(this) {
        SILVER -> GOLD
        GOLD -> DIAMOND
        DIAMOND -> COBALT
        COBALT -> AMETHYST
        AMETHYST -> AMETHYST
    }
}