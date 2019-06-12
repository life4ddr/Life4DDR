package com.perrigogames.life4trials.data

import android.content.Context
import com.google.gson.annotations.SerializedName
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.view.longNumberString
import java.io.Serializable

/**
 * The base rank goal class, describing a single goal of a rank on the LIFE4 ladder.
 * @param type the type of the goal, indicating which subclass it is
 */
abstract class BaseRankGoal(val type: String,
                            val mandatory: Boolean = false): Serializable {

    abstract fun goalString(context: Context): String
}

/**
 * A specialized rank goal requiring the player to burn a number of calories in a single session.
 * @param count the number of calories that need to be burned in a single session
 */
class CaloriesRankGoal(type: String,
                       val count: Int): BaseRankGoal(type) {

    override fun goalString(context: Context): String =
            context.getString(R.string.rank_goal_calories, count)

    companion object {
        const val TYPE_STRING = "calories"
    }
}

/**
 * A specialized rank goal requiring the player to clear all of the songs in a generic folder.
 * @param clearType the set of difficulty numbers (ordered) that must be cleared, defaults to [ClearType.CLEAR]
 * @param difficulties the difficulties that the folder must be cleared on
 * @param folder the specific folder that needs to be finished, null indicates any folder can be used
 */
class FolderClearGoal(type: String,
                      @SerializedName("clear_type") val clearType: ClearType = ClearType.CLEAR,
                      @SerializedName("require_all_difficulties") val requireAllDifficulties: Boolean = true,
                      val difficulties: List<DifficultyClass>,
                      val folder: String?): BaseRankGoal(type) {

    override fun goalString(context: Context): String =
            context.getString(R.string.rank_goal_lamp)

    companion object {
        const val TYPE_STRING = "lamp"
    }
}

/**
 * A specialized rank goal requiring the player to clear a special set of songs in one full round of play.
 * @param difficulties the set of difficulty numbers (ordered) that must be cleared
 */
class SongSetGoal(type: String,
                  @SerializedName("difficulty_numbers") val difficulties: IntArray): BaseRankGoal(type) {

    override fun goalString(context: Context): String {
        return context.getString(
            R.string.rank_goal_set_different,
            if (difficulties.all { it == difficulties[0] }) {
                context.getString(R.string.set_numbers_multiple_same_format,
                    difficulties.size, difficulties[0])
            } else {
                context.getString(R.string.set_numbers_3_format,
                    difficulties[0], difficulties[1], difficulties[2])
            })
    }

    companion object {
        const val TYPE_STRING = "set"
    }
}

/**
 * A specialized rank goal requiring players to clear songs of a particular difficulty in special ways.
 * For example,
 * - clearing 3 different 12's with LIFE4 enabled
 * - clearing all 17's with 950k or more points
 * - PFC-ing all 15's with the exception of 5 songs
 * -
 * @param difficulty the difficulty number to be judged
 * @param clearType the [ClearType] that needs to be obtained for each song, defaults to [ClearType.CLEAR]
 * @param count the number of songs that need to be cleared from the difficulty, null indicates the
 *   entire folder must be cleared. Cannot be used with [songs]
 * @param songs specifies specific songs that must be cleared. Overrides [count]
 * @param score the score that must be obtained by each song, null indicates that only [clearType] is
 *   necessary
 * @param exceptions if [count] is null, indicates the number of songs in the folder that do not need to
 *   meet these requirements
 */
class DifficultyClearGoal(type: String,
                          val difficulty: Int,
                          @SerializedName("clear_type") val clearType: ClearType = ClearType.CLEAR,
                          val count: Int?,
                          val songs: List<String>? = null,
                          val score: Int? = null,
                          val exceptions: Int? = null,
                          @SerializedName("song_exceptions") val songExceptions: List<String>? = null): BaseRankGoal(type) {

    override fun goalString(context: Context): String {
        return if (count == null) when {
            score != null -> scoreAllString(context) // All X over Y
            else -> lampString(context) // Y lamp the X's folder
        } else when {
            score != null -> scoreString(context) // All X over Y
            else -> clearString(context) // Y lamp the X's folder
//            throw IllegalArgumentException("Improper difficulty goal content")
        }
    }

    private fun scoreString(c: Context): String = when (score) {
            TrialData.AAA_SCORE -> clearString(c, c.getString(R.string.clear_aaa))
            TrialData.MAX_SCORE -> throw IllegalArgumentException("Use 'marvelous' clear type instead of specifying 1000000")
            else -> c.getString(R.string.rank_goal_difficulty_score_all, difficulty, score!!.longNumberString())
        }

    private fun scoreAllString(c: Context): String = when (score) {
        TrialData.AAA_SCORE -> c.getString(R.string.rank_goal_difficulty_aaa_all, difficulty)
        TrialData.MAX_SCORE -> throw IllegalArgumentException("Use 'marvelous' clear type instead of specifying 1000000")
        else -> c.getString(R.string.rank_goal_difficulty_score_all, difficulty, score!!.longNumberString())
    }

    private fun clearString(c: Context, text: String): String = with(c) {
        return if (count == 1) getString(R.string.rank_goal_difficulty_clear_single, text, difficulty)
        else getString(R.string.rank_goal_difficulty_clear, text, count, difficulty)
    }

    private fun clearString(c: Context): String =  when {
        exceptions != null -> throw IllegalArgumentException("Cannot combine exceptions with a set number")
        clearType.lampRes == null -> throw IllegalArgumentException("Invalid clear type: $clearType")
        count == 1 -> c.getString(R.string.rank_goal_difficulty_clear_single, c.getString(clearType.clearRes), difficulty)
        else -> clearString(c, c.getString(clearType.clearRes))
    }

    private fun lampString(c: Context): String = with(c) {
        return (when {
            clearType.lampRes == null -> throw IllegalArgumentException("Invalid clear type: $clearType")
            else -> getString(R.string.rank_goal_difficulty_lamp, getString(clearType.lampRes), difficulty)
        }) + exceptionString(c)
    }

    private fun exceptionString(c: Context) = " ${c.getString(R.string.exceptions, exceptions)}"

    companion object {
        const val TYPE_STRING = "difficulty"
    }
}
