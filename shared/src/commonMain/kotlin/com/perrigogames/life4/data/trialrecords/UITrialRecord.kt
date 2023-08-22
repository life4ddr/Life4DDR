package com.perrigogames.life4.data.trialrecords

import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.TrialRank

data class UITrialRecord(
    val trialTitleText: String,
    val trialSubtitleText: String? = null,
    val exScoreText: String,
    val exProgressPercent: Float,
    val trialSongs: List<UITrialSong>,
    val rank: TrialRank,
    val achieved: Boolean,
)

data class UITrialSong(
    val songTitleText: String,
    val scoreText: String,
    val difficultyClass: DifficultyClass,
)