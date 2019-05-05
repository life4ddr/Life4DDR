package com.perrigogames.life4trials.data

import android.content.res.Resources
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.TrialData.Companion.AAA_SCORE
import com.squareup.moshi.Json
import java.io.Serializable

data class GoalSet(val rank: TrialRank,
                   val score: List<Int>? = null,
                   @Json(name="score_indexed") val scoreIndexed: List<Int>? = null,
                   val judge: Int? = null,
                   val miss: Int? = null,
                   @Json(name="ex_missing") val exMissing: Int? = null): Serializable {

    fun generateGoalStrings(res: Resources, trial: Trial): List<String> = mutableListOf<String>().also { list ->
        generateSpecificScoreGoalStrings(res, trial, list)
        generateScoreGoalStrings(res, list)
        miss?.let { list.add(res.getString(R.string.misses_count, it)) }
        judge?.let { list.add(res.getString(R.string.bad_judgments_count, it)) }
        exMissing?.let { list.add(res.getString(R.string.missing_ex_count, it, trial.total_ex - it)) }
        if (list.size == 0) {
            list.add(res.getString(R.string.pass_the_trial))
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

            //DEBUG
//            out.add("${scoreSort.map { scoreString(it) }}, " +
//                    "${scoreCounts.map { "${scoreString(it.key)}->${it.value}" }}, ${scoreString(minimumScore)}")

            if (scoreCounts.size == 1) {
                if (allSongs) {
                    strings.add(onEveryString(res, minimumScore))
                } else {
                    strings.add(onCountString(res, minimumScore, scoreCounts[minimumScore]!!))
                }
            } else {
                scoreCounts.keys.sortedDescending().forEach { score ->
                    if (score == minimumScore && allSongs) {
                        strings.add(onRemainderString(res, score))
                    } else {
                        strings.add(onCountString(res, score, scoreCounts[score]!!))
                    }
                }
            }
        }
    }

    private fun scoreString(score: Int) = "${score / 1000}k"

    private fun onSpecificString(res: Resources, score: Int, song: String) =
        if (score == AAA_SCORE) res.getString(R.string.aaa_specific_song, song)
        else res.getString(R.string.score_specific_song, scoreString(score), song)

    private fun onCountString(res: Resources, score: Int, count: Int) =
        if (score == AAA_SCORE) res.getString(R.string.aaa_songs, count)
        else res.getString(R.string.score_songs, scoreString(score), count)

    private fun onEveryString(res: Resources, score: Int) =
        if (score == AAA_SCORE) res.getString(R.string.aaa_every_song)
        else res.getString(R.string.score_every_song, scoreString(score))

    private fun onRemainderString(res: Resources, score: Int) =
        if (score == AAA_SCORE) res.getString(R.string.aaa_on_remainder)
        else res.getString(R.string.score_on_remainder, scoreString(score))
}