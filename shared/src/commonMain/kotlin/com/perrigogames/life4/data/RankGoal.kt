@file:OptIn(ExperimentalSerializationApi::class)
@file:UseSerializers(
    DifficultyClassSerializer::class,
    PlayStyleSerializer::class,
    ClearTypeSerializer::class,
    LadderRankSerializer::class,
    TrialRankSerializer::class,
    DifficultyClassSetSerializer::class,
    RankGoalUserTypeSerializer::class,
)

package com.perrigogames.life4.data

import com.perrigogames.life4.GameConstants.HIGHEST_DIFFICULTY
import com.perrigogames.life4.PlatformStrings
import com.perrigogames.life4.enums.*
import com.perrigogames.life4.logE
import kotlinx.serialization.*

/**
 * The base rank goal class, describing a single goal of a rank on the LIFE4 ladder.
 * @param type the type of the goal, indicating which subclass it is
 */
@Serializable
sealed class BaseRankGoal {

    @Transient var isMandatory: Boolean = false

    abstract val id: Int
    @SerialName("s") open val playStyle: PlayStyle = PlayStyle.SINGLE

    abstract fun goalString(c: PlatformStrings): String
}

/**
 * An extension of the [BaseRankGoal] that groups similar statuses with one or more "stacked"
 * values, which can stand in for multiple ranks that inherit the base values.
 */
@Serializable
sealed class StackedRankGoal : BaseRankGoal() {

    val stacks: Map<String, List<Double?>> = emptyMap()

    @Transient val expandedGoalCount: Int = stacks.values.maxOf { it.size }

    @Transient open val expandedGoals: List<BaseRankGoal> by lazy {
        List(expandedGoalCount) { idx ->
            StackedRankGoalWrapper(this, idx)
        }
    }

    fun getIntValue(index: Int, key: String) = stacks[key]?.getOrNull(index)?.toInt()
    fun getDoubleValue(index: Int, key: String) = stacks[key]?.getOrNull(index)

    override fun goalString(c: PlatformStrings) = error("Cannot call standard goalString on stacked goal")
    abstract fun goalString(index: Int, c: PlatformStrings): String
}

/**
 * A wrapper around a [StackedRankGoal] to allow a single part of that stack to be utilized in
 * main app functions.
 */
class StackedRankGoalWrapper(
    val mainGoal: StackedRankGoal,
    val index: Int,
) : BaseRankGoal() {

    override val id = mainGoal.id + index

    fun getIntValue(key: String) = mainGoal.getIntValue(index, key)
    fun getDoubleValue(key: String) = mainGoal.getDoubleValue(index, key)

    override fun goalString(c: PlatformStrings) = mainGoal.goalString(index, c)
}

/**
 * A specialized rank goal requiring the player to burn a number of calories in a single session.
 * @param count the number of calories that need to be burned in a single session
 */
@Serializable
@SerialName("calories")
class CaloriesStackedRankGoal(
    override val id: Int,
): StackedRankGoal() {

    override fun goalString(index: Int, c: PlatformStrings) =
        c.rank.getCalorieCountString(getIntValue(index, KEY_CALORIES)!!)

    companion object {
        const val KEY_CALORIES = "calories"
    }
}

/**
 * A specialized rank goal requiring the player to clear a special set of songResults in one full round of play.
 * @param difficulties the set of difficulty numbers (ordered) that must be cleared
 */
@Serializable
@SerialName("set")
class DifficultySetGoal(
    override val id: Int,
    @SerialName("diff_nums") val difficulties: IntArray,
    @SerialName("clear_type") private val mClearType: ClearType? = null,
): BaseRankGoal() {

    val clearType: ClearType
        get() = mClearType ?: ClearType.CLEAR

    override fun goalString(c: PlatformStrings) = c.rank.getSongSetString(clearType, difficulties)
}

/**
 * A specialized rank goal requiring the player to clear a Trial with a certain rank.
 * @param rank the [TrialRank] that the user needs to earn
 * @param count the number of trials that need to be cleared with [rank]
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@SerialName("trial")
class TrialStackedGoal(
    override val id: Int,
    val rank: TrialRank,
    @SerialName("restrict") val restrictDifficulty: Boolean = false,
): StackedRankGoal() {

    override fun goalString(index: Int, c: PlatformStrings) =
        c.rank.getTrialCountString(rank, getIntValue(index, KEY_TRIALS_COUNT)!!)

    companion object {
        const val KEY_TRIALS_COUNT = "count"
    }
}

/**
 * A specialized goal requiring the user to obtain a certain number of "MFC Points"
 * @param points the number of MFC Points the player is required to obtain
 */
@Serializable
@SerialName("mfc_points")
class MFCPointsStackedGoal(
    override val id: Int,
): StackedRankGoal() {

    override fun goalString(index: Int, c: PlatformStrings) =
        c.rank.getMFCPointString(getDoubleValue(index, KEY_MFC_POINTS)!!)

    companion object {
        const val KEY_MFC_POINTS = "mfc_points"
    }
}

/**
 * A specialized rank goal requiring players to clear songResults of a particular difficulty in special ways.
 * For example,
 * - clearing 3 different 12's with LIFE4 enabled
 * - clearing all 17's with 950k or more points
 * - PFC-ing all 15's except for 5 songResults
 */
@Serializable
@SerialName("songs")
class SongsClearGoal(
    override val id: Int,
    @SerialName("user_type") val userType: RankGoalUserType? = null,
    @SerialName("d") val diffNum: Int? = null,
    @SerialName("allow_higher") val allowsHigherDiffNum: Boolean = false,
    @SerialName("diff_class") val diffClassSet: DifficultyClassSet? = null,
    val songs: List<String>? = null,
    private val folder: String? = null,

    @SerialName("folder_count") val folderCount: Int? = null,
    @SerialName("song_count") val songCount: Int? = null,
    val exceptions: Int? = null,
    @SerialName("song_exceptions") val songExceptions: List<String>? = null,

    val score: Int? = null,
    @SerialName("average_score") val averageScore: Int? = null,
    @SerialName("clear_type") private val mClearType: ClearType? = null,
): BaseRankGoal() {

    val safeExceptions: Int = exceptions ?: 0

    @Transient
    val folderType: FolderType? = folder.let {
        val version = GameVersion.parse(folder)
        return@let when {
            it == null -> null
            version != null -> FolderType.Version(version)
            it.length == 1 -> FolderType.Letter(it[0])
            else -> error("Illegal folder type")
        }
    }

    val clearType: ClearType
        get() = mClearType ?: ClearType.CLEAR

    fun validate(): Boolean {
        if (averageScore != null && allowsHigherDiffNum) {
            return false // averages only supported for a single difficulty
        }

        var count = 0
        if (score != null) count += 1
        if (averageScore != null) count += 1
        if (mClearType != null) count += 1
        return count <= 1
    }

    val diffNumRange: IntRange? by lazy {
        if (diffNum == null) null
        else {
            val maxLevel = if (allowsHigherDiffNum) HIGHEST_DIFFICULTY else diffNum
            (diffNum..maxLevel)
        }
    }

    inline fun forEachDiffNum(block: (Int) -> Unit) {
        diffNumRange?.forEachIndexed { _, diff -> block(diff) }
    }

    override fun goalString(c: PlatformStrings): String = when {
        score != null -> c.rank.scoreString(score, songGroupString(c))
        averageScore != null -> c.rank.averageScoreString(averageScore, songGroupString(c))
        else -> c.rank.clearString(clearType, shouldUseLamp, songGroupString(c))
    }

    private val shouldUseLamp =
        diffClassSet != null && folderCount != null

    private fun songGroupString(c: PlatformStrings): String = when {
        folderCount != null -> c.rank.folderString(folderCount)
        folder != null -> c.rank.folderString(folder)
        songs != null -> c.rank.songListString(songs)
        diffNum != null -> if (songCount != null) {
            c.rank.diffNumCount(songCount, diffNum, allowsHigherDiffNum)
        } else {
            c.rank.diffNumAll(diffNum, allowsHigherDiffNum)
        }
        songCount != null -> c.rank.songCountString(songCount)
        else -> {
            logE("GoalValidation", "Goal $id has no song group string")
            "???????"
        }
    }
        .difficultySection(c)
        .exceptionSection(c)

    private fun String.difficultySection(c: PlatformStrings) = when {
        diffClassSet != null -> c.rank.difficultyClassSetModifier(this, diffClassSet, playStyle)
        else -> this
    }

    private fun String.exceptionSection(c: PlatformStrings) = when {
        exceptions != null -> c.rank.exceptionsModifier(this, exceptions)
        songExceptions != null -> c.rank.songExceptionsModifier(this, songExceptions)
        else -> this
    }

    sealed class FolderType {
        class Letter(val letter: Char): FolderType()
        class Version(val version: GameVersion): FolderType()
    }
}

/**
 * A specialized rank goal requiring players to clear songResults of a particular difficulty in special ways.
 * For example,
 * - clearing 3 different 12's with LIFE4 enabled
 * - clearing all 17's with 950k or more points
 * - PFC-ing all 15's except for 5 songResults
 *
 * This is the stacked variant of [SongsClearGoal] and supports stacks for the keys "diff_num", "folder_count",
 * "song_count", "exceptions", "score", and "average_score".
 */
@Serializable
@SerialName("songs_stack")
data class SongsClearStackedGoal(
    override val id: Int,
    @SerialName("user_type") val userType: RankGoalUserType? = null,
    @SerialName("d") val diffNum: Int? = null,
    @SerialName("allow_higher") val allowsHigherDiffNum: Boolean = false,
    @SerialName("diff_class") val diffClassSet: DifficultyClassSet? = null,
    val songs: List<String>? = null,
    private val folder: String? = null,

    @SerialName("folder_count") val folderCount: Int? = null,
    @SerialName("song_count") val songCount: Int? = null,
    val exceptions: Int? = null,
    @SerialName("song_exceptions") val songExceptions: List<String>? = null,

    val score: Int? = null,
    @SerialName("average_score") val averageScore: Int? = null,
    @SerialName("clear_type") private val mClearType: ClearType? = null,
) : StackedRankGoal() {

    override val expandedGoals: List<BaseRankGoal>
        get() = List(expandedGoalCount) { idx ->
            SongsClearGoal(
                id = id + idx,
                userType = userType,
                diffNum = getIntValue(idx, KEY_DIFFICULTY_NUM) ?: diffNum,
                allowsHigherDiffNum,
                diffClassSet,
                songs,
                folder,
                folderCount = getIntValue(idx, KEY_FOLDER_COUNT) ?: folderCount,
                songCount = getIntValue(idx, KEY_SONG_COUNT) ?: songCount,
                exceptions = getIntValue(idx, KEY_EXCEPTIONS) ?: exceptions,
                songExceptions,
                score = getIntValue(idx, KEY_SCORE) ?: score,
                averageScore = getIntValue(idx, KEY_AVERAGE_SCORE) ?: averageScore,
                mClearType,
            )
        }

    override fun goalString(index: Int, c: PlatformStrings) = expandedGoals[index].goalString(c)

    companion object {
        const val KEY_DIFFICULTY_NUM = "diff_num"
        const val KEY_FOLDER_COUNT = "folder_count"
        const val KEY_SONG_COUNT = "song_count"
        const val KEY_EXCEPTIONS = "exceptions"
        const val KEY_SCORE = "score"
        const val KEY_AVERAGE_SCORE = "average_score"
    }
}

/**
 * A composite goal requiring the user to complete one of a small series of goals
 * @param options the goals from which the player is allowed to choose
 */
@Serializable
@SerialName("multiple")
data class MultipleChoiceGoal(
    override val id: Int,
    val options: List<BaseRankGoal>,
): BaseRankGoal() {

    override fun goalString(c: PlatformStrings) = c.toListString(
        options.map { it.goalString(c).replace(".", "") },
        useAnd = false,
        caps = true
    ) + "."
}
