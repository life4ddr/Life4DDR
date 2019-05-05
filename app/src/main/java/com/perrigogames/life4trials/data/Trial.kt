package com.perrigogames.life4trials.data

import android.content.res.Resources
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.perrigogames.life4trials.R
import com.squareup.moshi.Json
import java.io.Serializable

data class TrialData(val trials: List<Trial>): Serializable {
    companion object {
        const val TRIAL_LENGTH = 4
        const val AAA_SCORE = 990000
    }
}

data class Trial(val name: String,
                 val difficulty: Int,
                 val goals: List<GoalSet>,
                 val total_ex: Int,
                 val songs: List<Song>,
                 val jacket_dir: String,
                 val jacket_name: String? = null): Serializable {

    fun jacketUrl(resources: Resources) =
        resources.getString(
            R.string.url_trial_jacket_base,
            jacket_dir, (jacket_name ?: name.toLowerCase()))

    fun jacketUrl(resources: Resources, size: Int) =
        resources.getString(
            R.string.url_trial_jacket_sized,
            jacket_dir, (jacket_name ?: name.toLowerCase()), size, size)
}

data class Song(val name: String,
                @Json(name="difficulty") val difficultyNumber: Int,
                @Json(name="difficulty_class") val difficultyClass: DifficultyClass,
                val url: String? = null): Serializable

enum class TrialRank(@DrawableRes val drawableRes: Int) {
    @Json(name="silver") SILVER(R.drawable.silver_3),
    @Json(name="gold") GOLD(R.drawable.gold_3),
    @Json(name="diamond") DIAMOND(R.drawable.diamond_3),
    @Json(name="cobalt") COBALT(R.drawable.cobalt_3),
    @Json(name="amethyst") AMETHYST(R.drawable.amethyst_3)
}

enum class DifficultyClass(@ColorRes val colorRes: Int) {
    @Json(name="beginner") BEGINNER(R.color.difficultyBeginner),
    @Json(name="basic") BASIC(R.color.difficultyBasic),
    @Json(name="difficult") DIFFICULT(R.color.difficultyDifficult),
    @Json(name="expert") EXPERT(R.color.difficultyExpert),
    @Json(name="challenge") CHALLENGE(R.color.difficultyChallenge)
}