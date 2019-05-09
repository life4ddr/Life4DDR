package com.perrigogames.life4trials.data

import android.content.res.Resources
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

    fun goalSet(rank: TrialRank): GoalSet? = goals.find { it.rank == rank }

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

