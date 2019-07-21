package com.perrigogames.life4trials.data

import android.content.res.Resources
import com.google.gson.annotations.SerializedName
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.TrialData.Companion.AAA_SCORE
import com.perrigogames.life4trials.data.TrialData.Companion.MAX_SCORE
import java.io.Serializable

data class TrialGoalSet(val rank: TrialRank,
                        val clear: List<ClearType>? = null,
                        @SerializedName("clear_indexed") val clearIndexed: List<ClearType>? = null,
                        val score: List<Int>? = null,
                        @SerializedName("score_indexed") val scoreIndexed: List<Int>? = null,
                        val judge: Int? = null,
                        val miss: Int? = null,
                        @SerializedName("ex_missing") val exMissing: Int? = null): Serializable {

    val goalTypes: List<GoalType>
        get() = mutableListOf<GoalType>().apply {
            if (clear != null || clearIndexed != null) add(GoalType.CLEAR)
            if (score != null || scoreIndexed != null) add(GoalType.SCORE)
            if (exMissing != null) add(GoalType.EX)
            if (judge != null) add(GoalType.BAD_JUDGEMENT)
            if (miss != null) add(GoalType.MISS)
        }

    fun generateSingleGoalString(res: Resources, trial: Trial) = StringBuilder().apply {
        generateGoalStrings(res, trial).forEach { s ->
            append("$s\n")
        }
    }.toString()

    fun generateGoalStrings(res: Resources, trial: Trial): List<String> = mutableListOf<String>().also { list ->
        generateClearGoalStrings(res, trial, list)
        generateSpecificScoreGoalStrings(res, trial, list)
        generateScoreGoalStrings(res, list)
        miss?.let { list.add(missesString(res, it)) }
        judge?.let { list.add(badJudgementsString(res, it)) }
        exMissing?.let { list.add(exScoreString(res, it, trial.total_ex)) }
        if (list.size == 0) {
            list.add(res.getString(R.string.pass_the_trial))
        }
    }

    private fun generateSpecificClearGoalStrings(res: Resources, trial: Trial, strings: MutableList<String>) {
        strings.add("TODO: we don't need this right now") //TODO
    }

    private fun generateClearGoalStrings(res: Resources, trial: Trial, strings: MutableList<String>) {
        clearIndexed?.let { clears ->
            var setType: ClearType? = null
            var chainEnd: Int? = null
            clears.forEachIndexed { idx, type ->
                if (setType == null) { // first non-fail sets the type
                    if (type != ClearType.FAIL) {
                        setType = type
                    } else {
                        return generateSpecificClearGoalStrings(res, trial, strings)
                    }
                } else if (setType != null) {
                    if (type == ClearType.FAIL) { // first fail after the chain starts
                        chainEnd = idx
                    } else if (setType != type || (chainEnd != null && type != ClearType.FAIL)) { // if you clash or have already ended the chain
                        return generateSpecificClearGoalStrings(res, trial, strings)
                    }
                }
            }
            strings.add(res.getString(R.string.clear_first_songs, res.getString(setType!!.clearRes), chainEnd!!))
        }
    }

    private fun generateSpecificScoreGoalStrings(res: Resources, trial: Trial, strings: MutableList<String>) {
        scoreIndexed?.let { scores ->
            val scoreGroups = HashMap<Int, MutableList<Int>>()
            scores.forEachIndexed { index, i ->
                scoreGroups[i] = (scoreGroups[i] ?: mutableListOf()).apply { add(index) }
            }

            val allSongs = scores.size == TrialData.TRIAL_LENGTH

            if (scoreGroups.size == 1 && allSongs) {
                strings.add(onEveryString(res, scores[0]))
            } else {
                scoreGroups.keys.sortedDescending().forEach { score ->
                    if (score != 0) {
                        strings.add(onSpecificString(res, score, StringBuilder().also { builder ->
                            val names = scoreGroups[score]!!.map { trial.songs[it].name }
                            names.forEachIndexed { index, name ->
                                if (index != 0) {
                                    builder.append(if (index == names.lastIndex) ", and " else ", ")
                                }
                                builder.append(name)
                            }
                        }.toString()))
                    }
                }
            }
        }
    }

    private fun generateScoreGoalStrings(res: Resources, strings: MutableList<String>) {
        score?.sortedDescending()?.let { scoreSort ->
            val scoreCounts = HashMap<Int, Int>()
            scoreSort.forEach {
                scoreCounts[it] = (scoreCounts[it] ?: 0) + 1
            }

            val allSongs = scoreSort.size == TrialData.TRIAL_LENGTH
            val minimumScore = scoreSort.last()

            if (scoreCounts.size == 1) {
                strings.add(when {
                    allSongs -> onEveryString(res, minimumScore)
                    else ->onCountString(res, minimumScore, scoreCounts[minimumScore]!!)
                })
            } else {
                scoreCounts.keys.sortedDescending().forEachIndexed { index, score ->
                    strings.add(when {
                        score == minimumScore && allSongs -> onRemainderString(res, score)
                        index == 0 -> onCountString(res, score, scoreCounts[score]!!)
                        else -> onCountOtherString(res, score, scoreCounts[score]!!)
                    })
                }
            }
        }
    }

    private fun scoreString(score: Int) = "${score / 1000}k"

    private fun onSpecificString(res: Resources, score: Int, song: String) = when (score) {
        AAA_SCORE -> res.getString(R.string.aaa_specific_song, song)
        MAX_SCORE -> res.getString(R.string.mfc_specific_song, song)
        else -> res.getString(R.string.score_specific_song, scoreString(score), song)
    }

    private fun onCountString(res: Resources, score: Int, count: Int) = when (score) {
        AAA_SCORE -> res.getString(R.string.aaa_songs, count)
        MAX_SCORE -> res.getString(R.string.mfc_songs, count)
        else -> res.getString(R.string.score_songs, scoreString(score), count)
    }

    private fun onCountOtherString(res: Resources, score: Int, count: Int) = when (score) {
        AAA_SCORE -> res.getString(R.string.aaa_other_songs, count)
        MAX_SCORE -> res.getString(R.string.mfc_other_songs, count)
        else -> res.getString(R.string.score_other_songs, scoreString(score), count)
    }

    private fun onEveryString(res: Resources, score: Int) = when (score) {
        AAA_SCORE -> res.getString(R.string.aaa_every_song)
        MAX_SCORE -> res.getString(R.string.mfc_every_song)
        else -> res.getString(R.string.score_every_song, scoreString(score))
    }

    private fun onRemainderString(res: Resources, score: Int) = when (score) {
        AAA_SCORE -> res.getString(R.string.aaa_on_remainder)
        MAX_SCORE -> res.getString(R.string.mfc_on_remainder)
        else -> res.getString(R.string.score_on_remainder, scoreString(score))
    }

    private fun badJudgementsString(res: Resources, bad: Int) =
        if (bad == 0) res.getString(R.string.no_bad_judgments)
        else res.getString(R.string.bad_judgments_count, bad)

    private fun exScoreString(res: Resources, bad: Int, total: Int?) = when {
        bad == 0 -> res.getString(R.string.no_missing_ex)
        total != null -> res.getString(R.string.missing_ex_count_threshold, bad, total - bad)
        else -> res.getString(R.string.missing_ex_count, bad)
    }

    private fun missesString(res: Resources, misses: Int) =
        if (misses == 0) res.getString(R.string.no_misses)
        else res.getString(R.string.misses_count, misses)

    enum class GoalType {
        CLEAR, SCORE, EX, BAD_JUDGEMENT, MISS
    }
}