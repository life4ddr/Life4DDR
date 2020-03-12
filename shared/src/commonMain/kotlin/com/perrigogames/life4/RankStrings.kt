package com.perrigogames.life4

import com.perrigogames.life4.data.ClearType
import com.perrigogames.life4.data.DifficultyClass
import com.perrigogames.life4.data.PlayStyle
import com.perrigogames.life4.data.TrialRank

interface RankStrings {
    fun getCalorieCountString(count: Int): String
    fun getSongSetString(difficulties: IntArray): String
    fun getTrialCountString(rank: TrialRank, count: Int): String
    fun getMFCPointString(count: Int): String

    // Song Set
    fun difficultyClassString(playStyle: PlayStyle, difficulties: List<DifficultyClass>, requireAll: Boolean): String
    fun scoreSpecificSongDifficulty(score: Int, songs: List<String>, difficultyString: String): String
    fun clearSpecificSongDifficulty(clearType: ClearType, songs: List<String>, difficultyString: String): String
    fun lampDifficulty(clearType: ClearType, folderName: String, difficultyString: String): String
    fun clearSingle(clearType: ClearType, difficultyString: String): String
    fun clearCount(clearType: ClearType, count: Int, difficultyString: String): String
    val anyFullMixOrLetterString: String

    // Difficulty Clear
    fun difficultyString(difficulty: Int, plural: Boolean): String =
        difficultyString(intArrayOf(difficulty), plural)
    fun difficultyString(difficultyNumbers: IntArray, plural: Boolean): String
    fun scoreString(score: Int, count: Int, difficulty: Int): String
    fun scoreAllString(score: Int, clearType: ClearType, difficulty: Int?): String
    fun folderLamp(clearType: ClearType, difficulty: Int): String
    fun clearSingleDifficulty(clearType: ClearType, difficulty: Int): String
    fun scoreSingleDifficulty(score: Int, difficulty: Int): String
    fun singleDifficultyAOrAn(leftText: String, difficulty: Int): String
    fun difficultyClear(text: String, count: Int, difficultyNumbers: IntArray): String
    fun exceptions(exceptions: Int): String
    fun songExceptions(songExceptions: List<String>): String
    fun pluralNumber(number: Int, plural: Boolean): String
}
