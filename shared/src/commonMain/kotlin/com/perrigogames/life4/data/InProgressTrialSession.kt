package com.perrigogames.life4.data

import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.ClearType.*
import com.perrigogames.life4.feature.trials.data.Trial
import com.perrigogames.life4.feature.trials.data.TrialEXProgress
import com.perrigogames.life4.feature.trials.data.TrialGoalSet
import com.perrigogames.life4.feature.trials.data.TrialSong
import com.perrigogames.life4.feature.trials.enums.TrialRank
import com.perrigogames.life4.util.hasCascade
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class InProgressTrialSession(
    val trial: Trial,
    val results: Array<SongResult?> = arrayOfNulls(trial.songs.size),
    val finalPhotoUriString: String? = null,
) {

    @Transient var goalObtained: Boolean = false

    val hasStarted: Boolean
        get() = results.filterNotNull().any { it.score != null }

    fun hasResult(index: Int): Boolean = results[index] != null

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

    fun createOrUpdateSongResult(index: Int, photoUri: String) = copy(
        results = results.copyOf().also {
            it[index] = it[index]?.copy(photoUriString = photoUri)
                ?: SongResult(
                    song = trial.songs[index],
                    photoUriString = photoUri,
                )
        }
    )

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
        get() = results.filterNotNull().sumOf { it.exScore ?: 0 }

    /** Calculates the highest EX that a player could obtain on the songs that have been currently completed */
    private val currentMaxExScore: Int
        get() = trial.songs.mapIndexed { idx, item ->
            results[idx]?.exScore ?: item.ex
        }.sumOf { it }

    /** Calculates the amount of EX that is missing, which only counts the songs that have been completed */
    private val missingExScore: Int?
        get() = results.let { results ->
            if (results.any { it != null && it.exScore == null }) {
                null
            } else {
                results
                    .mapIndexed { idx, result -> trial.songs[idx].ex to result }
                    .sumOf { (songEx, result) ->
                        result?.let { songEx - it.exScore!! } ?: 0
                    }
            }
        }

    /**
     * Checks to see if the specified [TrialRank] goals would be satisfied under the current conditions.
     * Returns true or false if it can reliably be concluded that the requirements are or are not met, or
     * null if there's not enough information to make the determination.
     */
    fun isRankSatisfied(rank: TrialRank): Boolean? {
        val goal = trial.goalSet(rank) ?: return false
        val presentResults = results.filterNotNull()
        val scores = results.mapNotNull { it?.score }
        val clears = results.mapNotNull { it?.clearType?.stableId?.toInt() }

        fun exMissingSatisfied(): Boolean? = evaluateGoalCheck(goal.exMissing, missingExScore)

        fun judgeMissingSatisfied(): Boolean? = evaluateGoalCheck(goal.judge, currentValidatedBadJudgments)

        fun missTotalSatisfied(): Boolean? = evaluateGoalCheck(goal.miss, currentValidatedMisses)

        fun missEachSatisfied(): Boolean? = if (goal.missEach == null) {
            true
        } else {
            presentResults.map { (it.misses ?: return@map null) <= goal.missEach }.minimumResult()
        }

        fun scoresSatisfied(): Boolean? = when {
            goal.score == null -> true
            presentResults.any { it.score == null } -> null
            else -> goal.score.hasCascade(scores)
        }

        fun scoresIndexedSatisfied(): Boolean? = when {
            goal.scoreIndexed == null -> true
            presentResults.any { it.score == null } -> null
            else -> {
                trial.songs.mapIndexed { idx, song ->
                    (results[idx] ?: return@mapIndexed true).score!! == goal.scoreIndexed[idx]
                }.minimumResult()
            }
        }

        fun clearsSatisfied(): Boolean? = goal.clear
            ?.map { it.stableId.toInt() }
            ?.let { it.hasCascade(clears.filterNotNull()) }
            ?: true

        fun clearsIndexedSatisfied(): Boolean? = when {
            goal.clearIndexed == null -> true
            presentResults.any { it.clearType == null } -> null
            else -> {
                trial.songs.mapIndexed { idx, song ->
                    (results[idx] ?: return@mapIndexed true).clearType.stableId == goal.clearIndexed[idx].stableId
                }.minimumResult()
            }
        }

        return listOf(
            "EX Missing" to exMissingSatisfied(),
            "Bad Judgments" to judgeMissingSatisfied(),
            "Misses" to missTotalSatisfied(),
            "Miss Each" to missEachSatisfied(),
            "Scores" to scoresSatisfied(),
            "Score Idx" to scoresIndexedSatisfied(),
            "Clears" to clearsSatisfied(),
            "Clear Idx" to clearsIndexedSatisfied(),
        ).map { (name, result) ->
            when (result) {
                false -> println("$name not satisfied for ${rank.name}")
                null -> println("$name unknown for ${rank.name}")
                else -> {}
            }
            result
        }.minimumResult()
    }

    private fun evaluateGoalCheck(target: Int?, actual: Int?): Boolean? = when {
        target == null -> true
        actual == null -> null
        else -> actual <= target
    }
}

fun List<Boolean?>.minimumResult(): Boolean? = when {
    this.any { it == false } -> false
    this.any { it == null } -> null
    else -> true
}

@Serializable
data class SongResult(
    val song: TrialSong,
    val photoUriString: String? = null,
    val score: Int? = null,
    val exScore: Int? = null,
    val misses: Int? = null,
    val goods: Int? = null,
    val greats: Int? = null,
    val perfects: Int? = null,
    val passed: Boolean = true,
) {

    val badJudges get() = when {
        misses == null -> null
        goods == null -> null
        greats == null -> null
        else -> misses + goods + greats
    }

    val clearType: ClearType
        get() = when {
        !passed -> FAIL
        exScore == song.ex -> MARVELOUS_FULL_COMBO
        else -> {
            var highestClear = MARVELOUS_FULL_COMBO
            if (perfects == null || perfects > 0) {
                highestClear = PERFECT_FULL_COMBO
            }
            if (greats == null || greats > 0) {
                highestClear = GREAT_FULL_COMBO
            }
            if (goods == null || goods > 0) {
                highestClear = GOOD_FULL_COMBO
            }
            when {
                misses == null -> highestClear = CLEAR
                misses >= 4 -> highestClear = CLEAR
                misses > 0 -> highestClear = LIFE4_CLEAR
            }
            highestClear
        }
    }
}
