package com.perrigogames.life4.feature.trials.view

import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.enums.colorRes
import com.perrigogames.life4.feature.trials.data.TrialSong
import dev.icerock.moko.resources.ColorResource

data class UITrialSong(
    val jacketUrl: String?,
    val songNameText: String,
    val subtitleText: String,
    val playStyle: PlayStyle,
    val difficultyClass: DifficultyClass,
    val difficultyText: String,
    val difficultyNumber: Int,
) {
    val color: ColorResource = difficultyClass.colorRes
    val chartString = playStyle.aggregateString(difficultyClass)
}

fun TrialSong.toUITrialSong() = UITrialSong(
    jacketUrl = url,
    songNameText = chart.song.title,
    subtitleText = chart.song.version.printName,
    playStyle = playStyle,
    difficultyClass = difficultyClass,
    difficultyText = chart.difficultyNumber.toString(),
    difficultyNumber = chart.difficultyNumber,
)
