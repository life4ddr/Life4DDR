package com.perrigogames.life4.data.trials

import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle

fun createUITrialSong(
    jacketUrl: String = "https://life4-mobile.s3.us-west-1.amazonaws.com/images/jackets/songs/bag-jacket.webp",
    songNameText: String = "bag",
    artistText: String = "REVEN-G",
    playStyle: PlayStyle = PlayStyle.SINGLE,
    difficultyClass: DifficultyClass = DifficultyClass.EXPERT,
    difficultyText: String = "13",
    difficultyNumber: Int = 13,
) = UITrialSong(
    jacketUrl = jacketUrl,
    songNameText = songNameText,
    subtitleText = artistText,
    playStyle = playStyle,
    difficultyClass = difficultyClass,
    difficultyText = difficultyText,
    difficultyNumber = difficultyNumber,
)

val bags = listOf(
    createUITrialSong(
        songNameText = "bag",
        difficultyClass = DifficultyClass.DIFFICULT,
        difficultyText = "13",
        difficultyNumber = 13,
    ),
    createUITrialSong(
        songNameText = "bag",
        difficultyClass = DifficultyClass.EXPERT,
        difficultyText = "13",
        difficultyNumber = 13,
    ),
    createUITrialSong(
        songNameText = "bag",
        difficultyClass = DifficultyClass.CHALLENGE,
        difficultyText = "13",
        difficultyNumber = 13,
    )
)