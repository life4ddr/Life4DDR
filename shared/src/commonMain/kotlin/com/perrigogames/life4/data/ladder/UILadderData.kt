package com.perrigogames.life4.data.ladder

import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.colorRes
import dev.icerock.moko.resources.ColorResource

data class UILadderData(
    val goals: List<UILadderGoal>,
)

data class UILadderGoal(
    val goalText: String,
    val completed: Boolean = false,
    val hidden: Boolean = false,
    val canHide: Boolean = true,
    val progress: UILadderProgress? = null,
    val detailItems: List<UILadderDetailItem> = emptyList(),
)

data class UILadderProgress(
    val progressPercent: Float,
    val progressText: String,
) {
    constructor(
        count: Int,
        max: Int,
    ) : this(
        progressPercent = count.toFloat() / max.toFloat(),
        progressText = "$count / $max" // FIXME hardcoded
    )
}

data class UILadderDetailItem(
    val leftText: String,
    val leftColor: ColorResource? = null,
    val leftWeight: Float = 0.75f,
    val rightText: String? = null,
    val rightColor: ColorResource? = null,
    val rightWeight: Float = 0.25f,
) {

    constructor(
        leftText: String,
        difficultyClass: DifficultyClass? = null,
        rightText: String? = null,
    ) : this(
        leftText = leftText,
        leftColor = difficultyClass?.colorRes,
        rightText = rightText,
    )
}