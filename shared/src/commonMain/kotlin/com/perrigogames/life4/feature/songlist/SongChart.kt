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
    val deleted: Boolean = false,
)

data class Chart(
    val song: Song,
    val playStyle: PlayStyle,
    val difficultyClass: DifficultyClass,
    val difficultyNumber: Int,
    val difficultyNumberTier: Double? = null,
    val lockType: Int? = null,
) {
    val combinedDifficultyNumber: Double = difficultyNumber + (difficultyNumberTier ?: -0.005)
    val difficultyTierString: String = difficultyNumberTier?.let {
        (it * 100).toInt().toString().let { text ->
            if (text.length == 2) text else "0$text"
        }
    } ?: "??"
    val combinedDifficultyNumberString: String = "$difficultyNumber.$difficultyTierString"
}
