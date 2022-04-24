@file:OptIn(ExperimentalSerializationApi::class)
@file:UseSerializers(
    TrialTypeSerializer::class,
    TrialRankSerializer::class,
    DifficultyClassSerializer::class,
    PlayStyleSerializer::class,
    ChartTypeSerializer::class,
    ClearTypeSerializer::class)

package com.perrigogames.life4.response

import com.perrigogames.life4.TrialStrings
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.data.TrialData
import com.perrigogames.life4.enums.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class TrialGoalSet(
    val rank: TrialRank,
    val clear: List<ClearType>? = null,
    @SerialName("clear_indexed") val clearIndexed: List<ClearType>? = null,
    val score: List<Int>? = null,
    @SerialName("score_indexed") val scoreIndexed: List<Int>? = null,
    val judge: Int? = null,
    val miss: Int? = null,
    @SerialName("miss_each") val missEach: Int? = null,
    @SerialName("ex_missing") val exMissing: Int? = null,
) {

    val goalTypes: List<GoalType>
        get() = mutableListOf<GoalType>().apply {
            if (clear != null || clearIndexed != null) add(GoalType.CLEAR)
            if (score != null || scoreIndexed != null) add(GoalType.SCORE)
            if (exMissing != null) add(GoalType.EX)
            if (judge != null) add(GoalType.BAD_JUDGEMENT)
            if (miss != null) add(GoalType.MISS)
        }

    fun generateSingleGoalString(s: TrialStrings, trial: Trial) = StringBuilder().apply {
        generateGoalStrings(s, trial).forEachIndexed { idx, s ->
            append(if (idx == 0) s else "\n$s")
        }
    }.toString()

    fun generateGoalStrings(s: TrialStrings, trial: Trial): List<String> = mutableListOf<String>().also { list ->
        generateClearGoalStrings(s, trial, list)
        generateSpecificScoreGoalStrings(s, trial, list)
        generateScoreGoalStrings(s, list)
        miss?.let { list.add(s.allowedTotalMisses(it)) }
        missEach?.let { list.add(s.allowedSongMisses(it)) }
        judge?.let { list.add(s.allowedBadJudgments(it)) }
        exMissing?.let { list.add(s.allowedMissingExScore(it, trial.totalEx)) }
        if (list.size == 0) {
            list.add(s.clearTrial())
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun generateSpecificClearGoalStrings(s: TrialStrings, trial: Trial, strings: MutableList<String>) {
        strings.add("TODO: we don't need this right now") //TODO
    }

    private fun generateClearGoalStrings(s: TrialStrings, trial: Trial, strings: MutableList<String>) {
        clearIndexed?.let { clears ->
            var setType: ClearType? = null
            var chainEnd: Int? = null
            if (clears.all { it == clears[0] }) {
                strings.add(s.clearEverySong(clears[0]))
            } else {
                clears.forEachIndexed { idx, type ->
                    if (setType == null) { // first non-fail sets the type
                        if (type != ClearType.FAIL) {
                            setType = type
                        } else {
                            return generateSpecificClearGoalStrings(s, trial, strings)
                        }
                    } else if (setType != null) {
                        if (type == ClearType.FAIL) { // first fail after the chain starts
                            chainEnd = idx
                        } else if (setType != type || (chainEnd != null && type != ClearType.FAIL)) { // if you clash or have already ended the chain
                            return generateSpecificClearGoalStrings(s, trial, strings)
                        }
                    }
                }
                strings.add(s.clearFirstCountSongs(setType!!, chainEnd!!))
            }
        }
    }

    private fun generateSpecificScoreGoalStrings(s: TrialStrings, trial: Trial, strings: MutableList<String>) {
        scoreIndexed?.let { scores ->
            val scoreGroups = HashMap<Int, MutableList<Int>>()
            scores.forEachIndexed { index, i ->
                scoreGroups[i] = (scoreGroups[i] ?: mutableListOf()).apply { add(index) }
            }

            val allSongs = scores.size == TrialData.TRIAL_LENGTH

            if (scoreGroups.size == 1 && allSongs) {
                strings.add(s.scoreEverySong(scores[0]))
            } else {
                scoreGroups.keys.sortedDescending().forEach { score ->
                    if (score != 0) {
                        strings.add(s.scoreSingleSong(score, StringBuilder().also { builder ->
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

    private fun generateScoreGoalStrings(s: TrialStrings, strings: MutableList<String>) {
        score?.sortedDescending()?.let { scoreSort ->
            val scoreCounts = HashMap<Int, Int>()
            scoreSort.forEach {
                scoreCounts[it] = (scoreCounts[it] ?: 0) + 1
            }

            val allSongs = scoreSort.size == TrialData.TRIAL_LENGTH
            val minimumScore = scoreSort.last()

            if (scoreCounts.size == 1) {
                strings.add(when {
                    allSongs -> s.scoreEverySong(minimumScore)
                    else -> s.scoreCountSongs(minimumScore, scoreCounts[minimumScore]!!)
                })
            } else {
                scoreCounts.keys.sortedDescending().forEachIndexed { index, score ->
                    strings.add(when {
                        score == minimumScore && allSongs -> s.scoreEveryOtherSong(score)
                        index == 0 -> s.scoreCountSongs(score, scoreCounts[score]!!)
                        else -> s.scoreCountOtherSongs(score, scoreCounts[score]!!)
                    })
                }
            }
        }
    }

    @Serializable
    enum class GoalType {
        CLEAR, SCORE, EX, BAD_JUDGEMENT, MISS
    }
}
