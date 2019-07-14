package com.perrigogames.life4trials.data

import android.content.Context
import com.google.gson.annotations.SerializedName
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.db.LadderResultDB
import com.perrigogames.life4trials.util.locale
import com.perrigogames.life4trials.util.toListString
import com.perrigogames.life4trials.view.longNumberString
import java.io.Serializable
import kotlin.math.min

/**
 * The base rank goal class, describing a single goal of a rank on the LIFE4 ladder.
 * @param type the type of the goal, indicating which subclass it is
 */
abstract class BaseRankGoal(val id: Int,
                            val type: String,
                            val mandatory: Boolean = false): Serializable {

    abstract fun goalString(c: Context): String

    open fun getGoalProgress(results: List<LadderResultDB>): LadderGoalProgress? = null
}

/**
 * A specialized rank goal requiring the player to burn a number of calories in a single session.
 * @param count the number of calories that need to be burned in a single session
 */
class CaloriesRankGoal(id: Int,
                       type: String,
                       val count: Int): BaseRankGoal(id, type) {

    override fun goalString(c: Context): String =
            c.getString(R.string.rank_goal_calories, count)

    companion object {
        const val TYPE_STRING = "calories"
    }
}

/**
 * A specialized rank goal requiring the player to clear all of the songResults in a generic folder.
 * @param mClearType the set of difficulty numbers (ordered) that must be cleared, defaults to [ClearType.CLEAR]
 * @param mRequireAllDifficulties whether to require the user to do each of the difficulties or simply one of their choice, defaults to true
 * @param difficulties the difficulties that the folder must be cleared on
 * @param folder the specific folder that needs to be finished, null indicates any folder can be used
 * @param songs the list of specific songResults that need to be completed (this overrides [folder])
 */
class SongSetClearGoal(id: Int,
                       type: String,
                       mandatory: Boolean,
                       @SerializedName("clear_type") private val mClearType: ClearType?,
                       @SerializedName("require_all_difficulties") private val mRequireAllDifficulties: Boolean?,
                       val count: Int?,
                       val difficulties: List<DifficultyClass>,
                       val folder: String?,
                       val score: Int?,
                       val songs: List<String>?): BaseRankGoal(id, type, mandatory) {

    val requireAllDifficulties: Boolean get() = mRequireAllDifficulties ?: true

    val clearType: ClearType get() = mClearType ?: ClearType.CLEAR

    override fun goalString(c: Context): String = when {
        score != null && songs != null -> c.getString(R.string.score_specific_song_difficulty,
            score.longNumberString(), songs.toListString(c, R.string.and_s), difficultyString(c))
        count == null && songs != null -> c.getString(R.string.rank_goal_clear_specific,
            c.getString(clearType.clearResShort), songs.toListString(c, R.string.and_s), difficultyString(c))
        count == null -> c.getString(R.string.rank_goal_lamp,
            c.getString(clearType.lampRes!!), folderName(c), difficultyString(c))
        count == 1 -> c.getString(R.string.rank_goal_clear_count_single,
            c.getString(clearType.clearResShort), difficultyString(c))
        else -> c.getString(R.string.rank_goal_clear_count, c.getString(clearType.clearResShort), count, difficultyString(c))
    }

    private fun difficultyString(c: Context): String =
        difficulties.joinToString(separator = difficultySeparator) { c.getString(it.abbreviationRes) }

    private val difficultySeparator get() = if (requireAllDifficulties) " + " else " / "

    private fun folderName(c: Context) = folder ?: c.getString(R.string.any_full_mix_or_letter)

    companion object {
        const val TYPE_STRING = "songs"
    }
}

/**
 * A specialized rank goal requiring the player to clear a special set of songResults in one full round of play.
 * @param difficulties the set of difficulty numbers (ordered) that must be cleared
 */
class SongSetGoal(id: Int,
                  type: String,
                  mandatory: Boolean,
                  @SerializedName("difficulty_numbers") val difficulties: IntArray): BaseRankGoal(id, type, mandatory) {

    override fun goalString(c: Context): String {
        return c.getString(
            R.string.rank_goal_set_different,
            if (difficulties.all { it == difficulties[0] }) {
                c.getString(R.string.set_numbers_multiple_same_format,
                    difficulties.size, difficulties[0])
            } else {
                c.getString(R.string.set_numbers_3_format,
                    difficulties[0], difficulties[1], difficulties[2])
            })
    }

    companion object {
        const val TYPE_STRING = "set"
    }
}

/**
 * A specialized rank goal requiring the player to clear a Trial with a certain rank.
 * @param rank the [TrialRank] that the user needs to earn
 * @param count the number of trials that need to be cleared with [rank]
 */
class TrialGoal(id: Int,
                type: String,
                mandatory: Boolean,
                val rank: TrialRank,
                val count: Int = 1): BaseRankGoal(id, type, mandatory) {

    override fun goalString(c: Context): String {
        return if (count == 1) c.getString(R.string.rank_goal_clear_trial_single, c.getString(rank.nameRes))
        else c.getString(R.string.rank_goal_clear_trial, c.getString(rank.nameRes), count)
    }

    companion object {
        const val TYPE_STRING = "trial"
    }
}

/**
 * A specialized rank goal requiring players to clear songResults of a particular difficulty in special ways.
 * For example,
 * - clearing 3 different 12's with LIFE4 enabled
 * - clearing all 17's with 950k or more points
 * - PFC-ing all 15's with the exception of 5 songResults
 * @param difficulty the difficulty number to be judged
 * @param mDifficultyNumbers the set of difficulty numbers to be judged. This overrides [difficulty] and is
 *   intended to be different options (PFC 3 9s or 10s to indicate a user could do a mix of difficulties)
 * @param mClearType the [ClearType] that needs to be obtained for each song, defaults to [ClearType.CLEAR]
 * @param count the number of songResults that need to be cleared from the difficulty, null indicates the
 *   entire folder must be cleared. Cannot be used with [songs]
 * @param songs specifies specific songResults that must be cleared. Overrides [count]
 * @param score the score that must be obtained by each song, null indicates that only [clearType] is
 *   necessary
 * @param exceptions if [count] is null, indicates the number of songResults in the folder that do not need to
 *   meet these requirements
 */
class DifficultyClearGoal(id: Int,
                          type: String,
                          mandatory: Boolean,
                          @SerializedName("difficulty") val difficulty: Int?,
                          @SerializedName("difficulty_numbers") private val mDifficultyNumbers: IntArray?,
                          @SerializedName("clear_type") private val mClearType: ClearType?,
                          val count: Int?,
                          val songs: List<String>? = null,
                          val score: Int?,
                          val exceptions: Int?,
                          @SerializedName("song_exceptions") val songExceptions: List<String>? = null): BaseRankGoal(id, type, mandatory) {

    val clearType: ClearType get() = mClearType ?: ClearType.CLEAR

    val difficultyNumbers: IntArray get() = when {
        mDifficultyNumbers != null -> mDifficultyNumbers
        difficulty != null -> intArrayOf(difficulty)
        else -> throw IllegalArgumentException("Must implement difficulty or difficulty_numbers")
    }

    override fun goalString(c: Context): String {
        return if (count == null) when {
            score != null -> scoreAllString(c) + exceptionString(c) // All X over Y
            else -> lampString(c) + exceptionString(c) // Y lamp the X's folder
        } else when {
            score != null -> scoreString(c) // X Ys over Z
            else -> clearString(c) // Y clear Z X's
        }
    }

    override fun getGoalProgress(results: List<LadderResultDB>): LadderGoalProgress? {
        return when {
            results.isEmpty() -> null
            count == null -> {
                val remaining = when {
                    score != null -> results.filter { it.score < score } // All X over Y
                    else -> results.filter { it.clearType.ordinal < clearType.ordinal } // Y lamp the X's folder
                }
                val actualResultsSize = results.size - (exceptions ?: 0)
                LadderGoalProgress(min(results.size - remaining.size, actualResultsSize), actualResultsSize, remaining)
            }
            else -> {
                val resultCount = when {
                    score != null -> results.count { it.score >= score } // X Ys over Z
                    else -> results.count { it.clearType.ordinal >= clearType.ordinal } // Y clear Z X's
                }
                LadderGoalProgress(min(count, resultCount), count)
            }
        }
    }

    fun matrixString(c: Context): String {
        return if (score != null && clearType == ClearType.CLEAR) {
            score.longNumberString()
        } else {
            val lampString = c.getString(clearType.clearResShort).toUpperCase(c.locale)
            if (score == null) {
                lampString
            } else {
                "$lampString\n${score.longNumberString()}"
            }
        }
    }

    private fun scoreString(c: Context): String = when {
        score == null -> throw IllegalArgumentException("Score must exist when making a score string")
        score == TrialData.MAX_SCORE -> throw IllegalArgumentException("Use 'marvelous' clear type instead of specifying 1000000")
        score == TrialData.AAA_SCORE -> clearString(c, c.getString(R.string.clear_aaa))
        count == 1 -> when(difficulty) {
            8, 11, 18 -> c.getString(R.string.rank_goal_difficulty_clear_single_an, score.longNumberString(), difficultyString(c, false))
            else -> c.getString(R.string.rank_goal_difficulty_clear_single_a, score.longNumberString(), difficultyString(c, false))
        }
        else -> c.getString(R.string.rank_goal_difficulty_score, score.longNumberString(), count, difficultyString(c, true))
    }

    private fun scoreAllString(c: Context): String = when (score) {
        TrialData.MAX_SCORE -> throw IllegalArgumentException("Use 'marvelous' clear type instead of specifying 1000000")
        TrialData.AAA_SCORE -> when (clearType) {
            ClearType.CLEAR -> c.getString(R.string.rank_goal_difficulty_aaa_all, difficulty)
            else -> c.getString(R.string.rank_goal_difficulty_aaa_all_lamp, difficulty, c.getString(clearType.lampRes!!))
        }
        else -> when (clearType) {
            ClearType.CLEAR -> c.getString(R.string.rank_goal_difficulty_score_all, difficultyString(c, true), score!!.longNumberString())
            else -> c.getString(R.string.rank_goal_difficulty_score_all_lamp, difficultyString(c, true), score!!.longNumberString(), c.getString(clearType.lampRes!!))
        }
    }

    private fun clearString(c: Context, text: String): String = with(c) {
        return if (count == 1) when(difficulty) {
            8, 11, 18 -> getString(R.string.rank_goal_difficulty_clear_single_an, text, difficultyString(c, false))
            else -> getString(R.string.rank_goal_difficulty_clear_single_a, text, difficultyString(c, false))
        } else getString(R.string.rank_goal_difficulty_clear, text, count, difficultyString(c, true))
    }

    private fun clearString(c: Context): String =  when {
        exceptions != null -> throw IllegalArgumentException("Cannot combine exceptions with a set number")
        clearType.lampRes == null -> throw IllegalArgumentException("Invalid clear type: $clearType")
        count == 1 -> when(difficulty) {
            8, 11, 18 -> c.getString(R.string.rank_goal_difficulty_clear_single_an, c.getString(clearType.clearResShort), difficultyString(c, false))
            else -> c.getString(R.string.rank_goal_difficulty_clear_single_a, c.getString(clearType.clearResShort), difficultyString(c, false))
        }
        else -> clearString(c, c.getString(clearType.clearResShort))
    }

    private fun lampString(c: Context): String = with(c) {
        return (when {
            clearType.lampRes == null -> throw IllegalArgumentException("Invalid clear type: $clearType")
            else -> getString(R.string.rank_goal_difficulty_lamp, getString(clearType.lampRes!!), difficulty)
        })
    }

    private fun difficultyString(c: Context, plural: Boolean): String =
        difficultyNumbers.map { d -> pluralNumber(c, d, plural) }.toListString(c)

    private fun pluralNumber(c: Context, number: Int, plural: Boolean) =
        if (plural) c.getString(R.string.plural_number, number) else number.toString()

    private fun exceptionString(c: Context) = when {
        exceptions != null -> c.getString(R.string.exceptions, exceptions)
        songExceptions != null -> c.getString(R.string.exceptions_songs, songExceptions.toListString(c))
        else -> ""
    }

    companion object {
        const val TYPE_STRING = "difficulty"
    }
}

/**
 * A specialized goal requiring the user to obtain a certain number of "MFC Points"
 * @param points the number of MFC Points the player is required to obtain
 */
class MFCPointsGoal(id: Int,
                    type: String,
                    mandatory: Boolean,
                    val points: Int): BaseRankGoal(id, type, mandatory) {

    override fun goalString(c: Context): String {
        return c.getString(R.string.rank_goal_ex_points, points)
    }

    companion object {
        const val TYPE_STRING = "mfc_points"
    }
}

/**
 * A composite goal requiring the user to complete one of a small series of goals
 * @param options the goals from which the player is allowed to choose
 */
class MultipleChoiceGoal(id: Int,
                         type: String,
                         mandatory: Boolean,
                         val options: List<BaseRankGoal>): BaseRankGoal(id, type, mandatory) {

    override fun goalString(c: Context): String {
        return options.map { it.goalString(c).replace(".", "") }.toListString(c, R.string.or_s_caps)
    }

    companion object {
        const val TYPE_STRING = "multiple"
    }
}