package com.perrigogames.life4trials.data

import android.content.res.Resources
import com.google.gson.annotations.SerializedName
import com.perrigogames.life4trials.R
import java.io.Serializable

data class TrialData(val trials: List<Trial>): Serializable {
    companion object {
        const val TRIAL_LENGTH = 4
        const val AAA_SCORE = 990000
    }
}

data class Trial(val id: String,
                 val name: String,
                 val difficulty: Int,
                 val goals: List<GoalSet>,
                 val total_ex: Int,
                 val songs: List<Song>,
                 val jacket_dir: String? = null,
                 val jacket_name: String? = null): Serializable {

    fun goalSet(rank: TrialRank): GoalSet? = goals.find { it.rank == rank }

    fun jacketUrl(resources: Resources) =
        resources.getString(
            R.string.url_trial_jacket_base,
            jacket_dir, (jacket_name ?: id))

    fun jacketUrl(resources: Resources, size: Int) =
        resources.getString(
            R.string.url_trial_jacket_sized,
            jacket_dir, (jacket_name ?: id), size, size)

    val shouldShowTitle get() = jacket_dir == null && jacket_name == null
}

data class Song(val name: String,
                @SerializedName("difficulty") val difficultyNumber: Int,
                @SerializedName("difficulty_class") val difficultyClass: DifficultyClass,
                val url: String? = null): Serializable

