package com.perrigogames.life4trials

import android.content.Context
import com.perrigogames.life4.TrialStrings
import com.perrigogames.life4.data.ClearType
import com.perrigogames.life4.data.TrialData
import com.perrigogames.life4trials.util.clearRes

class AndroidTrialStrings(private val c: Context): TrialStrings {

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
