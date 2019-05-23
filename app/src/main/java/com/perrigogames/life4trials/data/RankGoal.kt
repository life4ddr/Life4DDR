package com.perrigogames.life4trials.data

import android.content.Context
import com.google.gson.annotations.SerializedName
import com.perrigogames.life4trials.R
import java.io.Serializable

/**
 * The base rank goal class, describing a single goal of a rank on the LIFE4 ladder.
 * @param type the type of the goal, indicating which subclass it is
 */
abstract class BaseRankGoal(@SerializedName("type") val type: String): Serializable {

    abstract fun goalString(context: Context): String
}

/**
 * A specialized rank goal requiring the player to burn a number of calories in a single session.
 * @param calories the number of calories that need to be burned in a single session
 */
class CaloriesRankGoal(type: String,
                       @SerializedName("calories") val calories: Int): BaseRankGoal(type) {

    override fun goalString(context: Context): String =
            context.getString(R.string.rank_goal_calories, calories)

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
                      @SerializedName("difficulties") val difficulties: List<DifficultyClass>,
                      @SerializedName("folder") val folder: String?): BaseRankGoal(type) {

    override fun goalString(context: Context): String =
            context.getString(R.string.rank_goal_lamp)

    companion object {
        const val TYPE_STRING = "folder_clear"
    }
}

/**
 * A specialized rank goal requiring the player to clear a special set of songs in one full round of play.
 * @param difficulties the set of difficulty numbers (ordered) that must be cleared
 */
class SongSetGoal(type: String,
                  @SerializedName("difficulty_numbers") val difficulties: IntArray): BaseRankGoal(type) {

    override fun goalString(context: Context): String {
        //TODO: "Set of 3 X's"
        return context.getString(R.string.rank_goal_set_different,
            context.getString(R.string.set_numbers_3, difficulties[0],
                difficulties[1], difficulties[2]))
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
 * @param difficultyNumber the difficulty number to be judged
 * @param clearType the [ClearType] that needs to be obtained for each song, defaults to [ClearType.CLEAR]
 * @param count the number of songs that need to be cleared from the difficulty, null indicates the
 *   entire folder must be cleared. Cannot be used with [songName]
 * @param songs specifies specific songs that must be cleared. Overrides [count]
 * @param score the score that must be obtained by each song, null indicates that only [clearType] is
 *   necessary
 * @param exceptions if [count] is null, indicates the number of songs in the folder that do not need to
 *   meet these requirements
 */
class DifficultyClearGoal(type: String,
                          @SerializedName("difficulty_number") val difficultyNumber: Int,
                          @SerializedName("clear_type") val clearType: ClearType? = ClearType.CLEAR,
                          @SerializedName("count") val count: Int? = null,
                          @SerializedName("songs") val songs: List<String>? = null,
                          @SerializedName("score") val score: Int? = null,
                          @SerializedName("exceptions") val exceptions: Int? = null): BaseRankGoal(type) {

    override fun goalString(context: Context): String {
        // specific songs
        // multiple songs
        return "" //TODO
    }

    companion object {
        const val TYPE_STRING = "difficulty"
    }
}
