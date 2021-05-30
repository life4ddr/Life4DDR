package com.perrigogames.life4

import com.perrigogames.life4.enums.ClearType

interface TrialStrings {
    fun scoreSingleSong(score: Int, song: String): String
    fun scoreCountSongs(score: Int, count: Int): String
    fun scoreCountOtherSongs(score: Int, count: Int): String
    fun scoreEverySong(score: Int): String
    fun scoreEveryOtherSong(score: Int): String
    fun allowedBadJudgments(bad: Int): String
    fun allowedMissingExScore(bad: Int, total: Int?): String
    fun allowedTotalMisses(misses: Int): String
    fun allowedSongMisses(misses: Int): String
    fun clearFirstCountSongs(clearType: ClearType, songs: Int): String
    fun clearEverySong(clearType: ClearType): String
    fun clearTrial(): String

    fun scoreString(score: Int) = "${score / 1000}k"
}
