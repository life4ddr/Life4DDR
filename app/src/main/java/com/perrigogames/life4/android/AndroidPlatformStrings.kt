package com.perrigogames.life4.android

import android.content.Context
import android.net.Uri
import com.perrigogames.life4.NotificationStrings
import com.perrigogames.life4.PlatformStrings
import com.perrigogames.life4.RankStrings
import com.perrigogames.life4.TrialStrings
import com.perrigogames.life4.data.*
import com.perrigogames.life4.*
import com.perrigogames.life4.data.*
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle
import org.koin.core.KoinComponent
import org.koin.core.inject

class AndroidPlatformStrings: PlatformStrings, KoinComponent {

    private val c: Context by inject()

    override fun nameString(rank: LadderRank) = c.getString(rank.nameRes)
    override fun groupNameString(rank: LadderRank) = c.getString(rank.groupNameRes)
    override fun nameString(rank: TrialRank) = c.getString(rank.nameRes)
    override fun nameString(rank: PlacementRank) = c.getString(rank.nameRes)
    override fun nameString(clazz: LadderRankClass) = c.getString(clazz.nameRes)
    override fun lampString(ct: ClearType) = c.getString(ct.lampRes)
    override fun clearString(ct: ClearType) = c.getString(ct.clearRes)
    override fun clearStringShort(ct: ClearType) = c.getString(ct.clearResShort)

    override fun toListString(list: List<String>, useAnd: Boolean, caps: Boolean): String = list.toListString(c, useAnd, caps)

    override val rank = object: RankStrings {

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
            c.getString(R.string.score_specific_song_difficulty, score.longNumberString(), songs.toListString(c, useAnd = true, caps = false), difficultyString)

        override fun clearSpecificSongDifficulty(clearType: ClearType, songs: List<String>, difficultyString: String): String =
            c.getString(R.string.rank_goal_clear_specific, c.getString(clearType.clearResShort), songs.toListString(c, useAnd = true, caps = false), difficultyString)

        override fun lampDifficulty(clearType: ClearType, folderName: String, difficultyString: String): String =
            c.getString(R.string.rank_goal_lamp, c.getString(clearType.lampRes), folderName, difficultyString)

        override fun clearSingle(clearType: ClearType, difficultyString: String): String =
            c.getString(R.string.rank_goal_clear_count_single, c.getString(clearType.clearResShort), difficultyString)

        override fun clearCount(clearType: ClearType, count: Int, difficultyString: String): String =
            c.getString(R.string.rank_goal_clear_count, c.getString(clearType.clearResShort), count, difficultyString)

        override val anyFullMixOrLetterString: String
            get() = c.getString(R.string.any_full_mix_or_letter)

        override fun difficultyString(difficultyNumbers: IntArray, plural: Boolean, useAnd: Boolean): String =
            difficultyNumbers.map { d -> pluralNumber(d, plural) }.toListString(c, useAnd, caps = false)

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
            c.getString(R.string.exceptions_songs, songExceptions.toListString(c, useAnd = true, caps = false))

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

    override val trial = object: TrialStrings {

        override fun scoreSingleSong(score: Int, song: String) = when (score) {
            TrialData.AAA_SCORE -> "• " + c.getString(R.string.aaa_specific_song, song)
            TrialData.MAX_SCORE -> "• " + c.getString(R.string.mfc_specific_song, song)
            else -> "• " + c.getString(R.string.score_specific_song, scoreString(score), song)
        }

        override fun scoreCountSongs(score: Int, count: Int) = when (score) {
            TrialData.AAA_SCORE -> "• " + c.getString(R.string.aaa_songs, count)
            TrialData.MAX_SCORE -> "• " + c.getString(R.string.mfc_songs, count)
            else -> "• " + c.getString(R.string.score_songs, scoreString(score), count)
        }

        override fun scoreCountOtherSongs(score: Int, count: Int) = when (score) {
            TrialData.AAA_SCORE -> "• " + c.getString(R.string.aaa_other_songs, count)
            TrialData.MAX_SCORE -> "• " + c.getString(R.string.mfc_other_songs, count)
            else -> "• " + c.getString(R.string.score_other_songs, scoreString(score), count)
        }

        override fun scoreEverySong(score: Int) = when (score) {
            TrialData.AAA_SCORE -> "• " + c.getString(R.string.aaa_every_song)
            TrialData.MAX_SCORE -> "• " + c.getString(R.string.mfc_every_song)
            else -> "• " + c.getString(R.string.score_every_song, scoreString(score))
        }

        override fun scoreEveryOtherSong(score: Int) = when (score) {
            TrialData.AAA_SCORE -> "• " + c.getString(R.string.aaa_on_remainder)
            TrialData.MAX_SCORE -> "• " + c.getString(R.string.mfc_on_remainder)
            else -> "• " + c.getString(R.string.score_on_remainder, scoreString(score))
        }

        override fun allowedBadJudgments(bad: Int) =
            if (bad == 0) "• " + c.getString(R.string.no_bad_judgments)
            else "• " + c.getString(R.string.bad_judgments_count, bad)

        override fun allowedMissingExScore(bad: Int, total: Int?) = when {
            bad == 0 -> "• " + c.getString(R.string.no_missing_ex)
            total != null -> "• " + c.getString(R.string.missing_ex_count_threshold, bad, total - bad)
            else -> "• " + c.getString(R.string.missing_ex_count, bad)
        }

        override fun allowedTotalMisses(misses: Int) =
            if (misses == 0) "• " + c.getString(R.string.no_misses)
            else "• " + c.getString(R.string.misses_count, misses)

        override fun allowedSongMisses(misses: Int) =
            if (misses == 0) "• " + c.getString(R.string.no_misses)
            else "• " + c.getString(R.string.misses_each_count, misses)

        override fun clearFirstCountSongs(clearType: ClearType, songs: Int) =
            "• " + c.getString(R.string.clear_first_songs, c.getString(clearType.clearRes), songs)

        override fun clearEverySong(clearType: ClearType) =
            "• " + c.getString(R.string.clear_every_song, c.getString(clearType.clearRes))

        override fun clearTrial() = "• " + c.getString(R.string.pass_the_trial)
    }

    override val notification = object: NotificationStrings {
        override val mainChannelTitle: String get() = c.getString(R.string.user_info_notifications)
        override val mainChannelDescription: String get() = c.getString(R.string.user_info_notifications_description)

        override val rivalCodeTitle: String get() = c.getString(R.string.rival_code)
        override val exScoreTitle: String get() = c.getString(R.string.ex_score)
        override val twitterNameTitle: String get() = c.getString(R.string.rival_code)
        override val discordNameTitle: String get() = c.getString(R.string.rival_code)
    }
}

fun List<String>.toListString(c: Context, useAnd: Boolean, caps: Boolean): String = StringBuilder().apply {
    this@toListString.forEachIndexed { index, d ->
        append(when {
            this@toListString.size == 1 -> d
            index == this@toListString.lastIndex -> c.getString(when {
                useAnd -> if (caps) R.string.and_s_caps else R.string.and_s
                else -> if (caps) R.string.or_s_caps else R.string.or_s
            }, d)
            index == this@toListString.lastIndex - 1 -> "$d "
            else -> "$d, "
        })
    }
}.toString()

var InProgressTrialSession.finalPhotoUri: Uri
    get() = Uri.parse(finalPhotoUriString)
    set(value) { finalPhotoUriString = value.toString() }

var SongResult.photoUri: Uri
    get() = Uri.parse(photoUriString)
    set(value) { photoUriString = value.toString() }
