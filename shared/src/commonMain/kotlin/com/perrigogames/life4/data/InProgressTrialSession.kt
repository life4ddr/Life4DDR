package com.perrigogames.life4.data

import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.ClearType.*
import com.perrigogames.life4.enums.TrialRank
import com.perrigogames.life4.feature.trials.data.TrialGoalSet
import com.perrigogames.life4.util.hasCascade
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class InProgressTrialSession(
    val trial: Trial,
    var targetRank: TrialRank? = null,
    val results: Array<SongResult?> = arrayOfNulls(trial.songs.size),
    var finalPhotoUriString: String? = null,
) {

    @Transient var goalObtained: Boolean = false

    val hasStarted: Boolean
        get() = results.filterNotNull().any { it.score != null }

    val hasFinalPhoto: Boolean
        get() = finalPhotoUriString != null

    val availableRanks: Array<TrialRank>
        get() = trial.goals?.map { it.rank }?.toTypedArray() ?: emptyArray()

    val shouldShowAdvancedSongDetails: Boolean
        get() = (if (!hasStarted) trial.highestGoal() else trialGoalSet)
            ?.let { it.miss != null || it.judge != null } ?: false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as InProgressTrialSession

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
        get() = trial.goalSet(targetRank)

    /**
     * Calculates the number of combined misses in the current session.
     */
    val currentMisses: Int get() = results.filterNotNull().sumOf { it.misses ?: 0 }

    /**
     * Calculates the number of combined bad judgments in the current session.
     */
    val currentBadJudgments: Int get() = results.filterNotNull().sumOf { it.badJudges ?: it.misses ?: 0 }

    /**
     * Calculates the number of combined misses in the current session. This
     * function will return null if ANY of the results lacks a misses value.
     */
    val currentValidatedMisses: Int?
        get() = results.filterNotNull().let { current ->
            if (current.any { it.misses == null }) {
                null
            } else currentMisses
        }

    /**
     * Calculates the number of combined bad judgments in the current session. This
     * function will return null if ANY of the results lacks a bad judgments value.
     */
    val currentValidatedBadJudgments: Int?
        get() = results.filterNotNull().let { current ->
            if (current.any { it.badJudges == null }) {
                null
            } else currentBadJudgments
        }

    val progress: TrialEXProgress
        get() = TrialEXProgress(
            currentExScore = currentTotalExScore,
            currentMaxExScore = currentMaxExScore,
            maxExScore = trial.totalEx,
        )

    /** Calculates the current total EX the player has obtained for this session */
    private val currentTotalExScore: Int
        get() = results.filterNotNull().sumOf { it.exScore!! }

    /** Calculates the highest EX that a player could obtain on the songs that have been currently completed */
    private val currentMaxExScore: Int
        get() = trial.songs.mapIndexed { idx, item ->
            results[idx]?.exScore ?: item.ex
        }.sumOf { it }

    /** Calculates the amount of EX that is missing, which only counts the songs that have been completed */
    private val missingExScore: Int
        get() = currentMaxExScore - currentTotalExScore

    val highestPossibleRank: TrialRank?
        get() {
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
            satisfied = currentBadJudgments <= goal.judge
        }
        if (satisfied && goal.miss != null) {
            satisfied = currentMisses <= goal.miss
        }
        if (satisfied && goal.missEach != null) {
            satisfied = currentMisses <= goal.missEach
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

@Serializable
data class SongResult(
    val song: TrialSong,
    var photoUriString: String? = null,
    var score: Int? = null,
    var exScore: Int? = null,
    var misses: Int? = null,
    var goods: Int? = null,
    var greats: Int? = null,
    var perfects: Int? = null,
    var passed: Boolean = true,
) {

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
