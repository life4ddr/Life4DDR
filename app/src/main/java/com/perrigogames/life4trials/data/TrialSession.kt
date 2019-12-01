package com.perrigogames.life4trials.data

import android.net.Uri
import com.perrigogames.life4trials.data.TrialGoalSet.GoalType.*
import java.io.Serializable

data class TrialSession(val trial: Trial,
                        var goalRank: TrialRank?,
                        val results: Array<SongResult?> = arrayOfNulls(TrialData.TRIAL_LENGTH),
                        var finalPhotoUriString: String? = null): Serializable {

    var goalObtained: Boolean = false

    var finalPhotoUri: Uri
        get() = Uri.parse(finalPhotoUriString)
        set(value) { finalPhotoUriString = value.toString() }

    val hasFinalPhoto get() = finalPhotoUriString != null

    val availableRanks: Array<TrialRank>? = trial.goals?.map { it.rank }?.toTypedArray()

    val shouldShowAdvancedSongDetails
        get() = trialGoalSet?.let { it.miss != null || it.judge != null } ?: false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrialSession

        if (trial != other.trial) return false
        if (!results.contentEquals(other.results)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = trial.hashCode()
        result = 31 * result + results.contentHashCode()
        return result
    }

    val trialGoalSet: TrialGoalSet?
        get() = trial.goalSet(goalRank)

    /**
     * Calculates the number of combined misses in the current session.
     */
    val currentMisses: Int? get() = results.filterNotNull().sumBy { it.misses ?: 0 }

    /**
     * Calculates the number of combined bad judgments in the current session.
     */
    val currentBadJudgments: Int? get() = results.filterNotNull().sumBy { it.badJudges ?: 0 }

    /**
     * Calculates the number of combined misses in the current session. This
     * function will return null if ANY of the results lacks a misses value.
     */
    val currentValidatedMisses: Int? get() = results.filterNotNull().let { current ->
        if (current.any { it.misses == null }) {
            null
        } else currentMisses
    }

    /**
     * Calculates the number of combined bad judgments in the current session. This
     * function will return null if ANY of the results lacks a bad judgments value.
     */
    val currentValidatedBadJudgments: Int? get() = results.filterNotNull().let { current ->
        if (current.any { it.badJudges == null }) {
            null
        } else currentBadJudgments
    }

    /** Calculates the current total EX the player has obtained for this session */
    val currentTotalExScore: Int get() = results.filterNotNull().sumBy { it.exScore!! }

    /** Calculates the highest EX that a player could obtain on the songs that have been currently completed */
    val currentMaxExScore: Int get() = trial.songs.mapIndexed { idx, item -> if (results[idx] != null) item.ex!! else 0  }.sumBy { it }

    /** Calculates the amount of EX that is missing, which only counts the songs that have been completed */
    val missingExScore: Int get() = currentMaxExScore - currentTotalExScore

    /** Calculates a player's rough projected EX score, assuming they get the same percentage of EX on the rest of the set as
     * they have on their already completed songs. */
    val projectedExScore: Int
        get() {
            val projectedMaxPercent = currentTotalExScore.toDouble() / currentMaxExScore.toDouble()
            return (trial.total_ex!! * projectedMaxPercent).toInt()
        }

    /** Calculates the amount of EX still available on songs that haven't been played */
    val remainingExScore: Int get() = currentTotalExScore - trial.total_ex!!

    val highestPossibleRank: TrialRank = TrialRank.values().last { rank ->
        trial.goals?.firstOrNull { it.rank == rank }?.let { goal ->
            val goalTypes = goal.goalTypes
            if (goalTypes.isNullOrEmpty()) {
                true
            } else {
                goalTypes.all { type -> when(type) {
                    EX -> missingExScore < goal.exMissing!!
                    BAD_JUDGEMENT -> currentBadJudgments?.let { it < goal.judge!! } ?: true
                    MISS -> currentMisses?.let { it < goal.judge!! } ?: true
                    //FIXME finish this
                    else -> true
//                    CLEAR -> {
//
//                    }
//                    SCORE -> {
//
//                    }
                } }
            }
        } ?: true
    }
}

data class SongResult(var song: Song,
                      var photoUriString: String? = null,
                      var score: Int? = null,
                      var exScore: Int? = null,
                      var misses: Int? = null,
                      var badJudges: Int? = null,
                      var perfects: Int? = null,
                      var passed: Boolean = true): Serializable {

    val hasAdvancedStats: Boolean get() = misses != null || badJudges != null

    var photoUri: Uri
        get() = Uri.parse(photoUriString)
        set(value) { photoUriString = value.toString() }

    fun randomize() {
        score = (Math.random() * 70000).toInt() + 930000
        exScore = (song.ex ?: 1000) - (Math.random() * 100).toInt()
    }
}