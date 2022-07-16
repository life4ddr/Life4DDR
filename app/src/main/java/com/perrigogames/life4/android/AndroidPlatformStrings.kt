package com.perrigogames.life4.android

import android.content.Context
import android.net.Uri
import com.perrigogames.life4.*
import com.perrigogames.life4.data.*
import com.perrigogames.life4.enums.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

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

        override fun getSongSetString(clearType: ClearType, difficulties: IntArray): String {
            return if (difficulties.all { it == difficulties[0] }) {
                c.getString(
                    R.string.rank_goal_set_sequential,
                    clearString(clearType, false),
                    c.getString(
                        R.string.set_numbers_multiple_same_format,
                        difficulties.size,
                        difficulties[0]
                    )
                )
            } else {
                c.getString(
                    R.string.rank_goal_set_different,
                    clearString(clearType, false),
                    c.getString(
                        R.string.set_numbers_3_format,
                        difficulties[0],
                        difficulties[1],
                        difficulties[2]
                    )
                )
            }
        }

        override fun getTrialCountString(rank: TrialRank, count: Int): String {
            return if (count == 1) c.getString(R.string.rank_goal_clear_trial_single, c.getString(rank.nameRes))
            else c.getString(R.string.rank_goal_clear_trial, c.getString(rank.nameRes), count)
        }

        override fun getMFCPointString(count: Double): String = c.getString(R.string.rank_goal_mfc_points, count)

        override fun folderString(
            folderCount: Int,
        ): String = when (folderCount) {
            1 -> c.getString(
                R.string.any_folder,
            )
            else -> c.getString(
                R.string.any_folder_plural,
                folderCount,
            )
        }

        override fun folderString(
            folderName: String,
        ): String = c.getString(
            R.string.specific_folder,
            folderName,
        )

        override fun songListString(songs: List<String>): String =
            songs.toListString(c, useAnd = true, caps = false)

        override fun songCountString(songCount: Int): String = c.getString(
            R.string.song_count,
            songCount,
        )

        override fun diffNumSingle(diffNum: Int, allowsHigherDiffNum: Boolean): String = c.getString(
            R.string.a_item,
            "L$diffNum${higherDiffNumSuffix(allowsHigherDiffNum)}",
        )

        override fun diffNumCount(count: Int, diffNum: Int, allowsHigherDiffNum: Boolean): String = when(count) {
            1 -> diffNumSingle(diffNum, allowsHigherDiffNum)
            else -> c.getString(
                R.string.num_items,
                count,
                "L$diffNum${higherDiffNumSuffix(allowsHigherDiffNum)}",
            )
        }

        override fun diffNumAll(diffNum: Int, allowsHigherDiffNum: Boolean): String = c.getString(
            R.string.all_items,
            "L$diffNum${higherDiffNumSuffix(allowsHigherDiffNum)}",
        )

        override fun scoreString(score: Int, groupString: String): String = when (score) {
            TrialData.MAX_SCORE -> throw IllegalArgumentException("Use MFC clear type")
            TrialData.AAA_SCORE -> c.getString(
                R.string.rank_goal_diff_score_aaa,
                groupString,
            )
            else -> c.getString(
                R.string.rank_goal_diff_score,
                score.longNumberString(),
                groupString,
            )
        }

        override fun averageScoreString(averageScore: Int, groupString: String): String =
            c.getString(
                R.string.rank_goal_diff_average,
                averageScore.longNumberString(),
                groupString,
            )

        override fun clearString(clearType: ClearType, useLamp: Boolean, groupString: String): String =
            c.getString(
                R.string.rank_goal_diff_clear_single,
                clearString(clearType, useLamp),
                groupString,
            )

        override fun clearTypeString(clearType: ClearType): String = c.getString(when(clearType) {
            ClearType.CLEAR -> R.string.clear
            ClearType.LIFE4_CLEAR -> R.string.clear_life4
            ClearType.GOOD_FULL_COMBO -> R.string.clear_fc
            ClearType.GREAT_FULL_COMBO -> R.string.clear_gfc
            ClearType.PERFECT_FULL_COMBO -> R.string.clear_pfc
            ClearType.MARVELOUS_FULL_COMBO -> R.string.clear_mfc
            else -> throw IllegalArgumentException("Illegal clear lamp $clearType")
        })

        override fun clearLampString(clearType: ClearType): String = c.getString(when(clearType) {
            ClearType.CLEAR -> R.string.lamp_clear
            ClearType.LIFE4_CLEAR -> R.string.lamp_life4
            ClearType.GOOD_FULL_COMBO -> R.string.lamp_fc
            ClearType.GREAT_FULL_COMBO -> R.string.lamp_gfc
            ClearType.PERFECT_FULL_COMBO -> R.string.lamp_pfc
            ClearType.MARVELOUS_FULL_COMBO -> R.string.lamp_mfc
            else -> throw IllegalArgumentException("Illegal clear lamp $clearType")
        })

        override fun difficultyClassSetModifier(
            groupString: String,
            diffClassSet: DifficultyClassSet,
            playStyle: PlayStyle
        ): String = c.getString(
            R.string.difficulty_modifier,
            groupString,
            diffClassSet.set
                .map { it.aggregateString(playStyle) }
                .toListString(c, diffClassSet.requireAll, false),
        )

        override fun exceptionsModifier(groupString: String, exceptions: Int): String = c.getString(
            R.string.exceptions_num_modifier,
            groupString,
            exceptions,
        )

        override fun songExceptionsModifier(groupString: String, songExceptions: List<String>): String = c.getString(
            R.string.exceptions_songs_modifier,
            groupString,
            songExceptions.toListString(c, useAnd = true, caps = false),
        )
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
