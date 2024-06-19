package com.perrigogames.life4.feature.songlist

import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.GameVersion
import com.perrigogames.life4.enums.PlayStyle

data class Song(
    val id: Long,
    val skillId: String,
    val title: String,
    val artist: String?,
    val version: GameVersion,
    val preview: Boolean?,
)

data class Chart(
    val song: Song,
    val playStyle: PlayStyle,
    val difficultyClass: DifficultyClass,
    val difficultyNumber: Int,
)
