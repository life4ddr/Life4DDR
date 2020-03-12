package com.perrigogames.life4.data

import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.ClearType.*
import com.perrigogames.life4.response.TrialGoalSet
import com.perrigogames.life4.util.hasCascade

data class TrialSession(val trial: Trial,
                        var goalRank: TrialRank?,
                        val results: Array<SongResult?> = arrayOfNulls(TrialData.TRIAL_LENGTH),
                        var finalPhotoUriString: String? = null) {

    val hasStarted: Boolean get() = results.filterNotNull().any { it.score != null }

    var goalObtained: Boolean = false

    val hasFinalPhoto get() = finalPhotoUriString != null

    val availableRanks: Array<TrialRank>? = trial.goals?.map { it.rank }?.toTypedArray()

    val shouldShowAdvancedSongDetails: Boolean
        get() = (if (!hasStarted) trial.highestGoal() else trialGoalSet)
            ?.let { it.miss != null || it.judge != null } ?: false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

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
    val currentMaxExScore: Int get() = trial.songs.mapIndexed { idx, item -> if (results[idx] != null) item.ex else 0  }.sumBy { it }

    /** Calculates the amount of EX that is missing, which only counts the songs that have been completed */
    val missingExScore: Int get() = currentMaxExScore - currentTotalExScore

    /** Calculates a player's rough projected EX score, assuming they get the same percentage of EX on the rest of the set as
     * they have on their already completed songs. */
    val projectedExScore: Int
        get() {
            val projectedMaxPercent = currentTotalExScore.toDouble() / currentMaxExScore.toDouble()
            return (trial.totalEx * projectedMaxPercent).toInt()
        }

    /** Calculates the amount of EX still available on songs that haven't been played */
    val remainingExScore: Int get() = currentTotalExScore - trial.totalEx

    val highestPossibleRank: TrialRank? get() {
        val availableRanks = trial.goals?.map { it.rank }?.sortedBy { it.stableId }
        return availableRanks?.lastOrNull { isRankSatisfied(it) }
    }

    fun isRankSatisfied(rank: TrialRank): Boolean {
        var satisfied = true
        val goal = trial.goalSet(rank) ?: return false
        if (goal.exMissing != null) {
            satisfied = (missingExScore <= goal.exMissing)
        }
        if (satisfied && goal.judge != null) {
            satisfied = currentBadJudgments?.let { it <= goal.judge } ?: true
        }
        if (satisfied && goal.miss != null) {
            satisfied = currentMisses?.let { it <= goal.miss } ?: true
        }
        if (satisfied && goal.missEach != null) {
            satisfied = currentMisses?.let { it <= goal.missEach } ?: true
        }

        val scores = results.map { it?.score }
        if (satisfied && goal.score != null && !goal.score.hasCascade(scores.filterNotNull())) {
            satisfied = false
        }
        if (satisfied && goal.scoreIndexed != null) {
            scores.forEachIndexed { idx, score ->
                if (score != null && score < goal.scoreIndexed[idx]) {
                    return false
                }
            }
        }

        val clears = results.map { it?.clearType?.stableId?.toInt() }

        // No clear requirements, just make sure everything is a pass
        if (satisfied && goal.clear == null && goal.clearIndexed == null) {
            satisfied = clears.none { it == FAIL.stableId.toInt() }
        }

        if (satisfied && goal.clear != null && !goal.clear.map { it.stableId.toInt() }.hasCascade(clears.filterNotNull())) {
            satisfied = false
        }
        if (satisfied && goal.clearIndexed != null) {
            clears.forEachIndexed { idx, clear ->
                if (clear != null && clear < goal.clearIndexed[idx].stableId) {
                    return false
                }
            }
        }
        return satisfied
    }
}

data class SongResult(var song: Song,
                      var photoUriString: String? = null,
                      var score: Int? = null,
                      var exScore: Int? = null,
                      var misses: Int? = null,
                      var goods: Int? = null,
                      var greats: Int? = null,
                      var perfects: Int? = null,
                      var passed: Boolean = true) {

    val badJudges get() = if (hasAdvancedStats) misses!! + goods!! + greats!! else null

    val hasAdvancedStats: Boolean get() = misses != null && goods != null && greats != null && perfects != null

    val clearType: ClearType
        get() = when {
        !passed -> FAIL
        exScore == song.ex -> MARVELOUS_FULL_COMBO
        perfects != null && badJudges != null -> when {
            perfects == 0 && badJudges == 0 -> MARVELOUS_FULL_COMBO
            badJudges == 0 -> PERFECT_FULL_COMBO
            misses != null -> when {
                misses == 0 -> GREAT_FULL_COMBO
                misses!! < 4 -> LIFE4_CLEAR
                else -> CLEAR
            }
            else -> CLEAR
        }
        misses != null -> when {
            misses == 0 -> GREAT_FULL_COMBO
            misses!! < 4 -> LIFE4_CLEAR
            else -> CLEAR
        }
        else -> CLEAR
    }

    fun randomize() {
        score = (930000..1000000).random()
        exScore = song.ex - (0..100).random()
    }
}
