package com.perrigogames.life4.data.trials

import com.perrigogames.life4.data.Song
import com.perrigogames.life4.db.SongInfo
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.enums.colorRes
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

fun Song.toUITrialSong(songInfo: SongInfo? = null) = UITrialSong(
    jacketUrl = this.url,
    songNameText = songInfo?.title ?: this.name,
    subtitleText = songInfo?.version?.printName ?: this.skillId,
    playStyle = this.playStyle,
    difficultyClass = this.difficultyClass,
    difficultyText = this.difficultyNumber.toString(),
    difficultyNumber = this.difficultyNumber,
)
