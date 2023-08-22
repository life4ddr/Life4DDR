package com.perrigogames.life4.data.trialrecords

import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.TrialRank
import com.perrigogames.life4.longNumberString
import kotlin.random.Random

object UITrialMocks {
    fun createUITrialRecord(
        trialTitleText: String = "Trial Title",
        trialSubtitleText: String = "(Retired)",
        exScoreText: String = "1234 / 2345",
        progressPercent: Float = Random.nextFloat(),
        trialSongs: List<UITrialSong> = (0..3).map {
            UITrialSong(
                songTitleText = "Song $it",
                scoreText = randomScoreString(),
                difficultyClass = randomDifficultyClass()
            )
        },
        rank: TrialRank = TrialRank.values().random(),
        achieved: Boolean = true,
    ) = UITrialRecord(
        trialTitleText = trialTitleText,
        trialSubtitleText = trialSubtitleText,
        exScoreText = exScoreText,
        exProgressPercent = progressPercent,
        trialSongs = trialSongs,
        rank = rank,
        achieved = achieved,
    )

    fun createUITrialSong(
        songTitleText: String = "Song Title",
        scoreText: String = randomScoreString(),
        difficultyClass: DifficultyClass,
    ) = UITrialSong(
        songTitleText = songTitleText,
        scoreText = scoreText,
        difficultyClass = difficultyClass,
    )

    private fun randomScoreString() = (999000 - Random.nextInt(0, 40000)).longNumberString()

    private fun randomDifficultyClass() = if (Random.nextBoolean()) DifficultyClass.CHALLENGE else DifficultyClass.EXPERT
}