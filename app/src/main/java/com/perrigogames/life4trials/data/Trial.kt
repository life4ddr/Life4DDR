package com.perrigogames.life4trials.data

import android.content.Context
import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName
import com.perrigogames.life4trials.R
import java.io.Serializable
import java.util.*

class TrialData(override val version: Int,
                @SerializedName("major_version") override val majorVersion: Int,
                val trials: List<Trial>): Serializable, MajorVersioned {

    companion object {
        const val HIGHEST_DIFFICULTY = 20
        const val TRIAL_LENGTH = 4
        const val MAX_SCORE = 1000000
        const val SCORE_PENALTY_PERFECT = 10
        const val AAA_SCORE = 990000
    }
}

class Trial(val id: String,
            val name: String,
            val author: String?,
            val type: TrialType,
            val placement_rank: PlacementRank?,
            val new: Boolean = false,
            val event_start: Date?,
            val event_end: Date?,
            val scoring_groups: List<List<TrialRank>>?,
            val difficulty: Int?,
            val goals: List<TrialGoalSet>?,
            val total_ex: Int,
            val cover_url: String? = null,
            val cover_override: Boolean = false,
            val songs: List<Song>): Serializable {

    val isEvent get() = type == TrialType.EVENT && event_start != null && event_end != null
    val isActiveEvent get() = isEvent && event_start!!.before(Date()) && event_end!!.after(Date())

    fun goalSet(rank: TrialRank?): TrialGoalSet? = goals?.find { it.rank == rank }

    @DrawableRes fun jacketResId(c: Context): Int =
        c.resources.getIdentifier(id, "drawable", c.packageName).let {
            return if (it == 0) R.drawable.trial_default else it
        }

    /**
     * Return the scoring group for a user with a particular rank.
     */
    fun findScoringGroup(rank: TrialRank) = scoring_groups?.first { it.contains(rank) }

    val isExValid get() = songs.sumBy { it.ex }.let { it == 0 || it == total_ex }
}

class Song(val name: String,
           @SerializedName("difficulty") val difficultyNumber: Int,
           @SerializedName("difficulty_class") val difficultyClass: DifficultyClass,
           val ex: Int,
           val url: String? = null): Serializable

enum class TrialType {
    @SerializedName("trial") TRIAL,
    @SerializedName("placement") PLACEMENT,
    @SerializedName("event") EVENT
}