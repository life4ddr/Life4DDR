package com.perrigogames.life4.feature.laddergoals

import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.longNumberString
import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc
import kotlin.random.Random

object UILadderMocks {
    fun createUILadderGoal(
        id: Long = 0,
        goalText: String = "Perform this generic action",
        completed: Boolean = false,
        hidden: Boolean = false,
        canHide: Boolean = true,
        progress: UILadderProgress? = null,
        detailItems: List<UILadderDetailItem> = emptyList(),
    ) = UILadderGoal(
        id = id,
        goalText = StringDesc.Raw(goalText),
        completed = completed,
        hidden = hidden,
        canHide = canHide,
        progress = progress,
        detailItems = detailItems,
    )

    fun createUILadderDetailItem(
        leftText: String = "A Song Name or Something",
        leftColor: ColorResource? = null,
        rightText: String? = null,
        rightColor: ColorResource? = null,
    ) = UILadderDetailItem(
        leftText = leftText,
        leftColor = leftColor,
        rightText = rightText,
        rightColor = rightColor,
    )

    fun createSongDetailItem(
        songName: String,
        difficultyClass: DifficultyClass? = null,
        score: Int = 1_000_000 - Random.nextInt(100, 50000),
    ) = UILadderDetailItem(
        leftText = songName,
        difficultyClass = difficultyClass,
        rightText = score.longNumberString(),
    )
}
