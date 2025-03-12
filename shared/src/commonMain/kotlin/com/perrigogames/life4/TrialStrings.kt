package com.perrigogames.life4

import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.clearRes
import dev.icerock.moko.resources.desc.Composition
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc

object TrialStrings {
    fun scoreSingleSong(score: Int, song: String) = StringDesc.Composition(
        args = listOf(
            StringDesc.Raw("• "),
            when (score) {
                GameConstants.AAA_SCORE -> StringDesc.ResourceFormatted(MR.strings.aaa_specific_song, song)
                GameConstants.MAX_SCORE -> StringDesc.ResourceFormatted(MR.strings.mfc_specific_song, song)
                else -> StringDesc.ResourceFormatted(MR.strings.score_specific_song, scoreString(score), song)
            }
        )
    )

    fun scoreCountSongs(score: Int, count: Int) = StringDesc.Composition(
        args = listOf(
            StringDesc.Raw("• "),
            when (score) {
                GameConstants.AAA_SCORE -> StringDesc.ResourceFormatted(MR.strings.aaa_songs, count)
                GameConstants.MAX_SCORE -> StringDesc.ResourceFormatted(MR.strings.mfc_songs, count)
                else -> StringDesc.ResourceFormatted(MR.strings.score_songs, scoreString(score), count)
            }
        )
    )

    fun scoreCountOtherSongs(score: Int, count: Int) = StringDesc.Composition(
        args = listOf(
            StringDesc.Raw("• "),
            when (score) {
                GameConstants.AAA_SCORE -> StringDesc.ResourceFormatted(MR.strings.aaa_other_songs, count)
                GameConstants.MAX_SCORE -> StringDesc.ResourceFormatted(MR.strings.mfc_other_songs, count)
                else -> StringDesc.ResourceFormatted(MR.strings.score_other_songs, scoreString(score), count)
            }
        )
    )

    fun scoreEverySong(score: Int) = StringDesc.Composition(
        args = listOf(
            StringDesc.Raw("• "),
            when (score) {
                GameConstants.AAA_SCORE -> StringDesc.ResourceFormatted(MR.strings.aaa_every_song)
                GameConstants.MAX_SCORE -> StringDesc.ResourceFormatted(MR.strings.mfc_every_song)
                else -> StringDesc.ResourceFormatted(MR.strings.score_every_song, scoreString(score))
            }
        )
    )

    fun scoreEveryOtherSong(score: Int) = StringDesc.Composition(
        args = listOf(
            StringDesc.Raw("• "),
            when (score) {
                GameConstants.AAA_SCORE -> StringDesc.ResourceFormatted(MR.strings.aaa_on_remainder)
                GameConstants.MAX_SCORE -> StringDesc.ResourceFormatted(MR.strings.mfc_on_remainder)
                else -> StringDesc.ResourceFormatted(MR.strings.score_on_remainder, scoreString(score))
            }
        )
    )

    fun allowedBadJudgments(bad: Int) = StringDesc.Composition(
        args = listOf(
            StringDesc.Raw("• "),
            if (bad == 0) {
                StringDesc.ResourceFormatted(MR.strings.no_bad_judgments)
            } else {
                StringDesc.ResourceFormatted(MR.strings.bad_judgments_count, bad)
            }
        )
    )

    fun allowedMissingExScore(bad: Int, total: Int?) = StringDesc.Composition(
        args = listOf(
            StringDesc.Raw("• "),
            when {
                bad == 0 -> StringDesc.ResourceFormatted(MR.strings.no_missing_ex)
                total != null -> StringDesc.ResourceFormatted(MR.strings.missing_ex_count_threshold, bad, total - bad)
                else -> StringDesc.ResourceFormatted(MR.strings.missing_ex_count, bad)
            }
        )
    )

    fun allowedTotalMisses(misses: Int) = StringDesc.Composition(
        args = listOf(
            StringDesc.Raw("• "),
            if (misses == 0) {
                StringDesc.ResourceFormatted(MR.strings.no_misses)
            } else {
                StringDesc.ResourceFormatted(MR.strings.misses_count, misses)
            }
        )
    )

    fun allowedSongMisses(misses: Int) = StringDesc.Composition(
        args = listOf(
            StringDesc.Raw("• "),
            if (misses == 0) {
                StringDesc.ResourceFormatted(MR.strings.no_misses)
            } else {
                StringDesc.ResourceFormatted(MR.strings.misses_each_count, misses)
            }
        )
    )

    fun clearFirstCountSongs(clearType: ClearType, songs: Int) = StringDesc.Composition(
        args = listOf(
            StringDesc.Raw("• "),
            StringDesc.ResourceFormatted(MR.strings.clear_first_songs, StringDesc.ResourceFormatted(clearType.clearRes), songs)
        )
    )

    fun clearEverySong(clearType: ClearType) = StringDesc.Composition(
        args = listOf(
            StringDesc.Raw("• "),
            StringDesc.ResourceFormatted(MR.strings.clear_every_song, StringDesc.ResourceFormatted(clearType.clearRes))
        )
    )

    fun clearTrial() = StringDesc.Composition(
        args = listOf(
            StringDesc.Raw("• "),
            StringDesc.ResourceFormatted(MR.strings.pass_the_trial)
        )
    )

    fun scoreString(score: Int) = "${score / 1000}k"
}
