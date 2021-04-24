package com.perrigogames.life4

import com.perrigogames.life4.data.TrialRank
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle

interface RankStrings {

    /** Burn X calories in one day. */
    fun getCalorieCountString(count: Int): String

    /**
     * Complete X different DIFF's in a row.
     * Complete a set of DIFF, DIFF, and DIFF.
     */
    fun getSongSetString(difficulties: IntArray): String

    /**
     * Earn RANK on any Trial.
     * Earn RANK on X Trials.
     */
    fun getTrialCountString(rank: TrialRank, count: Int): String

    /** Earn X MFC Points. */
    fun getMFCPointString(count: Int): String

    // Song Set

    /** TYPE, TYPE, and/or TYPE */
    fun difficultyClassString(playStyle: PlayStyle, difficulties: List<DifficultyClass>, requireAll: Boolean): String
    /** Score X on Y (Z). */
    fun scoreSpecificSongDifficulty(score: Int, songs: List<String>, difficultyString: String): String
    /** X Y on Z. */
    fun clearSpecificSongDifficulty(clearType: ClearType, songs: List<String>, difficultyString: String): String
    /** Earn a X on Y (Z). */
    fun lampDifficulty(clearType: ClearType, folderName: String, difficultyString: String): String
    /** X a song on Y. */
    fun clearSingle(clearType: ClearType, difficultyString: String): String
    /** X Y songs on Z. */
    fun clearCount(clearType: ClearType, count: Int, difficultyString: String): String
    /** any full mix or letter folder */
    val anyFullMixOrLetterString: String

    // Difficulty Clear
    fun difficultyString(difficulty: Int, plural: Boolean, useAnd: Boolean = false): String =
        difficultyString(intArrayOf(difficulty), plural, useAnd)
    fun difficultyString(difficultyNumbers: IntArray, plural: Boolean, useAnd: Boolean = false): String

    /**
     * %s a/an DIFF.
     */
    fun difficultyAOrAn(leftText: String, difficulty: Int): String =
        difficultyAOrAn(leftText, intArrayOf(difficulty))
    fun difficultyAOrAn(leftText: String, difficulties: IntArray): String

    fun scoreString(score: Int, averageScore: Int?, count: Int, difficulties: IntArray): String
    fun scoreAllString(score: Int, averageScore: Int?, clearType: ClearType, difficulty: Int): String
    fun folderLamp(clearType: ClearType, difficulty: Int, averageScore: Int?): String
    fun clearSingleDifficulty(clearType: ClearType, difficulties: IntArray): String
    fun scoreSingleDifficulty(score: Int, difficulties: IntArray): String
    fun difficultyClear(text: String, count: Int, difficultyNumbers: IntArray): String
    fun exceptions(exceptions: Int): String
    fun songExceptions(songExceptions: List<String>): String
    fun pluralNumber(number: Int, plural: Boolean): String
    fun averageSuffix(average: Int?, useAnd: Boolean): String

    /**
     * %s %d different %s.
     */
    fun clearString(count: Int, difficulties: IntArray, clearType: ClearType): String
    fun clearString(count: Int, difficulties: IntArray, text: String): String
}
