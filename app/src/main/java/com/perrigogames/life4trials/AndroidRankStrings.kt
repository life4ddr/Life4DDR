package com.perrigogames.life4trials

import android.content.Context
import com.perrigogames.life4.RankStrings
import com.perrigogames.life4.data.TrialData
import com.perrigogames.life4.data.TrialRank
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.longNumberString
import com.perrigogames.life4trials.util.clearResShort
import com.perrigogames.life4trials.util.lampRes
import com.perrigogames.life4trials.util.nameRes
import com.perrigogames.life4trials.util.toListString

class AndroidRankStrings(private val c: Context): RankStrings {

    override fun getCalorieCountString(count: Int): String = c.getString(R.string.rank_goal_calories, count)

    override fun getSongSetString(difficulties: IntArray): String {
        return if (difficulties.all { it == difficulties[0] }) {
            c.getString(R.string.rank_goal_set_sequential,
                c.getString(R.string.set_numbers_multiple_same_format, difficulties.size, difficulties[0]))
        } else {
            c.getString(R.string.rank_goal_set_different,
                c.getString(R.string.set_numbers_3_format, difficulties[0], difficulties[1], difficulties[2]))
        }
    }

    override fun getTrialCountString(rank: TrialRank, count: Int): String {
        return if (count == 1) c.getString(R.string.rank_goal_clear_trial_single, c.getString(rank.nameRes))
        else c.getString(R.string.rank_goal_clear_trial, c.getString(rank.nameRes), count)
    }

    override fun getMFCPointString(count: Int): String = c.getString(R.string.rank_goal_mfc_points, count)

    override fun scoreSpecificSongDifficulty(score: Int, songs: List<String>, difficultyString: String): String =
        c.getString(R.string.score_specific_song_difficulty, score.longNumberString(), songs.toListString(c, R.string.and_s), difficultyString)

    override fun clearSpecificSongDifficulty(clearType: ClearType, songs: List<String>, difficultyString: String): String =
        c.getString(R.string.rank_goal_clear_specific, c.getString(clearType.clearResShort), songs.toListString(c, R.string.and_s), difficultyString)

    override fun lampDifficulty(clearType: ClearType, folderName: String, difficultyString: String): String =
        c.getString(R.string.rank_goal_lamp, c.getString(clearType.lampRes), folderName, difficultyString)

    override fun clearSingle(clearType: ClearType, difficultyString: String): String =
        c.getString(R.string.rank_goal_clear_count_single, c.getString(clearType.clearResShort), difficultyString)

    override fun clearCount(clearType: ClearType, count: Int, difficultyString: String): String =
        c.getString(R.string.rank_goal_clear_count, c.getString(clearType.clearResShort), count, difficultyString)

    override val anyFullMixOrLetterString: String
        get() = c.getString(R.string.any_full_mix_or_letter)

    override fun difficultyString(difficultyNumbers: IntArray, plural: Boolean, useAnd: Boolean): String =
        difficultyNumbers.map { d -> pluralNumber(d, plural) }.toListString(c, if (useAnd) R.string.and_s else R.string.or_s)

    override fun difficultyAOrAn(leftText: String, difficulties: IntArray): String = when(difficulties[0]) {
        8, 11, 18 -> c.getString(R.string.rank_goal_difficulty_clear_single_an, leftText, difficultyString(difficulties, false))
        else -> c.getString(R.string.rank_goal_difficulty_clear_single_a, leftText, difficultyString(difficulties, false))
    }

    override fun scoreString(score: Int, count: Int, difficulties: IntArray): String = when {
        score == TrialData.MAX_SCORE -> throw IllegalArgumentException("Use 'marvelous' clear type instead of specifying 1000000")
        score == TrialData.AAA_SCORE -> clearString(count, difficulties, c.getString(R.string.clear_aaa))
        count == 1 -> scoreSingleDifficulty(score, difficulties)
        else -> c.getString(R.string.rank_goal_difficulty_score, score.longNumberString(), count, difficultyString(difficulties, true))
    }

    override fun scoreAllString(score: Int, clearType: ClearType, difficulty: Int): String = when (score) {
        TrialData.MAX_SCORE -> throw IllegalArgumentException("Use 'marvelous' clear type instead of specifying 1000000")
        TrialData.AAA_SCORE -> when (clearType) {
            ClearType.CLEAR -> c.getString(R.string.rank_goal_difficulty_aaa_all, difficulty)
            else -> c.getString(R.string.rank_goal_difficulty_aaa_all_lamp, difficulty, c.getString(clearType.lampRes))
        }
        else -> when (clearType) {
            ClearType.CLEAR -> c.getString(R.string.rank_goal_difficulty_score_all, difficultyString(difficulty, true), score.longNumberString())
            else -> c.getString(R.string.rank_goal_difficulty_score_all_lamp, difficultyString(difficulty, true), score.longNumberString(), c.getString(clearType.lampRes))
        }
    }

    override fun folderLamp(clearType: ClearType, difficulty: Int): String =
        c.getString(R.string.rank_goal_difficulty_lamp, c.getString(clearType.lampRes), difficulty)

    override fun clearSingleDifficulty(clearType: ClearType, difficulties: IntArray): String =
        difficultyAOrAn(c.getString(clearType.clearResShort), difficulties)

    override fun scoreSingleDifficulty(score: Int, difficulties: IntArray): String =
        difficultyAOrAn(score.longNumberString(), difficulties)

    override fun exceptions(exceptions: Int): String =
        c.getString(R.string.exceptions, exceptions)

    override fun difficultyClear(text: String, count: Int, difficultyNumbers: IntArray): String =
        c.getString(R.string.rank_goal_difficulty_clear, text, count, difficultyString(difficultyNumbers, true))

    override fun songExceptions(songExceptions: List<String>): String =
        c.getString(R.string.exceptions_songs, songExceptions.toListString(c))

    override fun pluralNumber(number: Int, plural: Boolean): String =
        if (plural) c.getString(R.string.plural_number, number) else number.toString()

    override fun difficultyClassString(playStyle: PlayStyle, difficulties: List<DifficultyClass>, requireAll: Boolean): String =
        difficulties.joinToString(separator = if (requireAll) " + " else " / ") { it.aggregateString(playStyle) }

    override fun clearString(count: Int, difficulties: IntArray, clearType: ClearType): String =
        clearString(count, difficulties, c.getString(clearType.clearResShort))

    override fun clearString(count: Int, difficulties: IntArray, text: String): String =
        if (count == 1) difficultyAOrAn(text, difficulties)
        else c.getString(R.string.rank_goal_difficulty_clear, text, count, difficultyString(difficulties, true))
}
