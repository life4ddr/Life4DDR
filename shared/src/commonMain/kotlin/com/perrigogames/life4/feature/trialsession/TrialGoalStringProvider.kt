package com.perrigogames.life4.feature.trialsession

import com.perrigogames.life4.GameConstants
import com.perrigogames.life4.TrialStrings
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.model.BaseModel
import org.koin.core.component.inject

class TrialGoalStringProvider: BaseModel() {

    private val strings: TrialStrings by inject()

    fun generateSingleGoalString(goalSet: TrialGoalSet, trial: Trial) = StringBuilder().apply {
        generateGoalStrings(goalSet, trial).forEachIndexed { idx, s ->
            append(if (idx == 0) s else "\n$s")
        }
    }.toString()

    fun generateGoalStrings(goalSet: TrialGoalSet, trial: Trial): List<String> = mutableListOf<String>().also { list ->
        generateClearGoalStrings(goalSet, trial, list)
        generateSpecificScoreGoalStrings(goalSet, trial, list)
        generateScoreGoalStrings(goalSet, list)
        goalSet.miss?.let { list.add(strings.allowedTotalMisses(it)) }
        goalSet.missEach?.let { list.add(strings.allowedSongMisses(it)) }
        goalSet.judge?.let { list.add(strings.allowedBadJudgments(it)) }
        goalSet.exMissing?.let { list.add(strings.allowedMissingExScore(it, trial.totalEx)) }
        if (list.size == 0) {
            list.add(strings.clearTrial())
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun generateSpecificClearGoalStrings(goalSet: TrialGoalSet, trial: Trial, output: MutableList<String>) {
        output.add("TODO: we don't need this right now") //TODO
    }

    private fun generateClearGoalStrings(goalSet: TrialGoalSet, trial: Trial, output: MutableList<String>) {
        goalSet.clearIndexed?.let { clears ->
            var setType: ClearType? = null
            var chainEnd: Int? = null
            if (clears.all { it == clears[0] }) {
                output.add(strings.clearEverySong(clears[0]))
            } else {
                clears.forEachIndexed { idx, type ->
                    if (setType == null) { // first non-fail sets the type
                        if (type != ClearType.FAIL) {
                            setType = type
                        } else {
                            return generateSpecificClearGoalStrings(goalSet, trial, output)
                        }
                    } else if (setType != null) {
                        if (type == ClearType.FAIL) { // first fail after the chain starts
                            chainEnd = idx
                        } else if (setType != type || (chainEnd != null && type != ClearType.FAIL)) { // if you clash or have already ended the chain
                            return generateSpecificClearGoalStrings(goalSet, trial, output)
                        }
                    }
                }
                output.add(strings.clearFirstCountSongs(setType!!, chainEnd!!))
            }
        }
    }

    private fun generateSpecificScoreGoalStrings(goalSet: TrialGoalSet, trial: Trial, output: MutableList<String>) {
        goalSet.scoreIndexed?.let { scores ->
            val scoreGroups = HashMap<Int, MutableList<Int>>()
            scores.forEachIndexed { index, i ->
                scoreGroups[i] = (scoreGroups[i] ?: mutableListOf()).apply { add(index) }
            }

            val allSongs = scores.size == GameConstants.TRIAL_LENGTH

            if (scoreGroups.size == 1 && allSongs) {
                output.add(strings.scoreEverySong(scores[0]))
            } else {
                scoreGroups.keys.sortedDescending().forEach { score ->
                    if (score != 0) {
                        output.add(strings.scoreSingleSong(score, StringBuilder().also { builder ->
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

    private fun generateScoreGoalStrings(goalSet: TrialGoalSet, output: MutableList<String>) {
        goalSet.score?.sortedDescending()?.let { scoreSort ->
            val scoreCounts = HashMap<Int, Int>()
            scoreSort.forEach {
                scoreCounts[it] = (scoreCounts[it] ?: 0) + 1
            }

            val allSongs = scoreSort.size == GameConstants.TRIAL_LENGTH
            val minimumScore = scoreSort.last()

            if (scoreCounts.size == 1) {
                output.add(when {
                    allSongs -> strings.scoreEverySong(minimumScore)
                    else -> strings.scoreCountSongs(minimumScore, scoreCounts[minimumScore]!!)
                })
            } else {
                scoreCounts.keys.sortedDescending().forEachIndexed { index, score ->
                    output.add(when {
                        score == minimumScore && allSongs -> strings.scoreEveryOtherSong(score)
                        index == 0 -> strings.scoreCountSongs(score, scoreCounts[score]!!)
                        else -> strings.scoreCountOtherSongs(score, scoreCounts[score]!!)
                    })
                }
            }
        }
    }
}