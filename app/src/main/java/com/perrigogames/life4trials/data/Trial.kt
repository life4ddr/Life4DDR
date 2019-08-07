package com.perrigogames.life4trials.data

import android.content.Context
import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName
import com.perrigogames.life4trials.R
import java.io.Serializable

class TrialData(val version: Int,
                val trials: List<Trial>): Serializable {

    companion object {
        const val HIGHEST_DIFFICULTY = 20
        const val TRIAL_LENGTH = 4
        const val MAX_SCORE = 1000000
        const val AAA_SCORE = 990000
    }
}

class Trial(val id: String,
            val name: String,
            val author: String?,
            val type: TrialType,
            val placement_rank: PlacementRank?,
            val new: Boolean = false,
            val difficulty: Int?,
            val goals: List<TrialGoalSet>?,
            val total_ex: Int?,
            val cover_url: String? = null,
            val cover_override: Boolean = false,
            val songs: List<Song>): Serializable {

    fun goalSet(rank: TrialRank): TrialGoalSet? = goals?.find { it.rank == rank }

    @DrawableRes fun jacketResId(c: Context): Int =
        c.resources.getIdentifier(id, "drawable", c.packageName).let {
            return if (it == 0) R.drawable.trial_default else it
        }
}

class Song(val name: String,
           @SerializedName("difficulty") val difficultyNumber: Int,
           @SerializedName("difficulty_class") val difficultyClass: DifficultyClass,
           val ex: Int?,
           val url: String? = null): Serializable

enum class TrialType {
    @SerializedName("trial") TRIAL,
    @SerializedName("placement") PLACEMENT
}