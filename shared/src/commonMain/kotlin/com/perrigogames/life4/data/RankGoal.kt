@file:UseSerializers(DifficultyClassSerializer::class,
    PlayStyleSerializer::class,
    ClearTypeSerializer::class,
    LadderRankSerializer::class,
    TrialRankSerializer::class)

package com.perrigogames.life4.data

import com.perrigogames.life4.PlatformStrings
import com.perrigogames.life4.enums.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * The base rank goal class, describing a single goal of a rank on the LIFE4 ladder.
 * @param type the type of the goal, indicating which subclass it is
 */
@Serializable
sealed class BaseRankGoal {

    open val id: Int = -1
    @SerialName("s") open val playStyle: PlayStyle = PlayStyle.SINGLE
    @SerialName("m") open val mandatory: Boolean = false

    abstract fun goalString(c: PlatformStrings): String

    //FIXME goal progress
//    open fun getGoalProgress(possible: Int, results: List<ILadderResult>): LadderGoalProgress? = null
}

/**
 * A specialized rank goal requiring the player to burn a number of calories in a single session.
 * @param count the number of calories that need to be burned in a single session
 */
@Serializable
@SerialName("calories")
class CaloriesRankGoal(val count: Int): BaseRankGoal() {
    override fun goalString(c: PlatformStrings) = c.rank.getCalorieCountString(count)
}

/**
 * A specialized rank goal requiring the player to clear all of the songResults in a generic folder.
 * @param mClearType the set of difficulty numbers (ordered) that must be cleared, defaults to [ClearType.CLEAR]
 * @param mRequireAllDifficulties whether to require the user to do each of the difficulties or simply one of their choice, defaults to true
 * @param difficulties the difficulties that the folder must be cleared on
 * @param folder the specific folder that needs to be finished, null indicates any folder can be used
 * @param songs the list of specific songResults that need to be completed (this overrides [folder])
 */
@Serializable
@SerialName("songs")
class SongSetClearGoal(@SerialName("clear_type") private val clearType: ClearType = ClearType.CLEAR,
                       @SerialName("require_all_difficulties") private val requireAllDifficulties: Boolean = true,
                       val count: Int? = null,
                       val difficulties: List<DifficultyClass>,
                       val folder: String? = null,
                       val score: Int? = null,
                       val songs: List<String>? = null): BaseRankGoal() {

    override fun goalString(c: PlatformStrings): String = when {
        score != null && songs != null -> c.rank.scoreSpecificSongDifficulty(score, songs, difficultyString(c))
        count == null && songs != null -> c.rank.clearSpecificSongDifficulty(clearType, songs, difficultyString(c))
        count == null -> c.rank.lampDifficulty(clearType, folderName(c), difficultyString(c))
        count == 1 -> c.rank.clearSingle(clearType, difficultyString(c))
        else -> c.rank.clearCount(clearType, count, difficultyString(c))
    }

    private fun difficultyString(c: PlatformStrings): String =
        difficulties.joinToString(separator = difficultySeparator) { (it + playStyle).toString() }

    private val difficultySeparator get() = if (requireAllDifficulties) " + " else " / "

    private fun folderName(c: PlatformStrings) = folder ?: c.rank.anyFullMixOrLetterString
}

/**
 * A specialized rank goal requiring the player to clear a special set of songResults in one full round of play.
 * @param difficulties the set of difficulty numbers (ordered) that must be cleared
 */
@Serializable
@SerialName("set")
class SongSetGoal(@SerialName("difficulty_numbers") val difficulties: IntArray): BaseRankGoal() {
    override fun goalString(c: PlatformStrings) = c.rank.getSongSetString(difficulties)
}

/**
 * A specialized rank goal requiring the player to clear a Trial with a certain rank.
 * @param rank the [TrialRank] that the user needs to earn
 * @param count the number of trials that need to be cleared with [rank]
 */
@Serializable
@SerialName("trial")
class TrialGoal(val rank: TrialRank,
                val count: Int = 1,
                @SerialName("restrict") val restrictDifficulty: Boolean = false): BaseRankGoal() {
    override fun goalString(c: PlatformStrings) = c.rank.getTrialCountString(rank, count)
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
@Serializable
@SerialName("difficulty")
class DifficultyClearGoal(@SerialName("d") val difficulty: Int? = null,
                          @SerialName("difficulty_numbers") private val mDifficultyNumbers: IntArray? = null,
                          @SerialName("clear_type") private val mClearType: ClearType? = null,
                          val count: Int? = null,
                          val songs: List<String>? = null,
                          val score: Int? = null,
                          val exceptions: Int? = null,
                          @SerialName("song_exceptions") val songExceptions: List<String>? = null): BaseRankGoal() {

    val clearType: ClearType get() = mClearType ?: ClearType.CLEAR

    val difficultyNumbers: IntArray get() = when {
        mDifficultyNumbers != null -> mDifficultyNumbers
        difficulty != null -> intArrayOf(difficulty)
        else -> throw IllegalArgumentException("Must implement difficulty or difficulty_numbers")
    }

    override fun goalString(c: PlatformStrings): String {
        return if (count == null) when {
            score != null -> c.rank.scoreAllString(score, clearType, difficulty!!) + exceptionString(c) // All X over Y
            else -> c.rank.folderLamp(clearType, difficulty!!) + exceptionString(c) // Y lamp the X's folder
        } else when {
            score != null -> c.rank.scoreString(score, count, difficultyNumbers) // X Ys over Z
            else -> clearString(c) // Y clear Z X's
        }
    }

//    override fun getGoalProgress(possible: Int, results: List<ILadderResult>): LadderGoalProgress? {
//        return when {
//            results.isEmpty() -> LadderGoalProgress(0, possible)
//            count == null -> {
//                val remaining = when {
//                    score != null -> results.filter { !it.satisfiesClear(clearType) || it.score < score } // All X over Y
//                    else -> results.filter { !it.satisfiesClear(clearType) } // Y lamp the X's folder
//                }.sortedByDescending { it.score }
//                val actualResultsSize = possible - (exceptions ?: 0)
//                LadderGoalProgress(min(results.size - remaining.size, actualResultsSize), actualResultsSize, results = remaining)
//            }
//            else -> {
//                val resultCount = when {
//                    score != null -> results.count { it.score >= score } // X Ys over Z
//                    else -> results.count { it.clearType.ordinal >= clearType.ordinal } // Y clear Z X's
//                }
//                LadderGoalProgress(min(count, resultCount), count)
//            }
//        }
//    }

    private fun clearString(c: PlatformStrings): String =  when {
        exceptions != null -> throw IllegalArgumentException("Cannot combine exceptions with a set number")
        else -> c.rank.clearString(count!!, difficultyNumbers, clearType)
    }

    private fun exceptionString(c: PlatformStrings) = when {
        exceptions != null -> c.rank.exceptions(exceptions)
        songExceptions != null -> c.rank.songExceptions(songExceptions)
        else -> ""
    }
}

/**
 * A specialized goal requiring the user to obtain a certain number of "MFC Points"
 * @param points the number of MFC Points the player is required to obtain
 */
@Serializable
@SerialName("mfc_points")
class MFCPointsGoal(val points: Int): BaseRankGoal() {

    override fun goalString(c: PlatformStrings) = c.rank.getMFCPointString(points)

//    override fun getGoalProgress(possible: Int, results: List<ILadderResult>) = LadderGoalProgress(results.sumBy {
//        GameConstants.mfcPointsForDifficulty(it.chart.target.difficultyNumber)
//    }, points)
}

/**
 * A composite goal requiring the user to complete one of a small series of goals
 * @param options the goals from which the player is allowed to choose
 */
@Serializable
@SerialName("multiple")
class MultipleChoiceGoal(val options: List<BaseRankGoal>): BaseRankGoal() {
    override fun goalString(c: PlatformStrings) = c.toListString(
        options.map { it.goalString(c).replace(".", "") }, true)
}
