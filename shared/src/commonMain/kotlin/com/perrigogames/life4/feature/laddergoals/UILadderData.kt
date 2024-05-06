package com.perrigogames.life4.feature.laddergoals

import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.colorRes
import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.desc.StringDesc

typealias CategorizedUILadderGoals = List<Pair<String, List<UILadderGoal>>>

data class UILadderData(
    val goals: UILadderGoals,
    val allowCompleting: Boolean = true,
    val allowHiding: Boolean = true,
    val showCompleted: Boolean = false,
    val showHidden: Boolean = false,
) {
    constructor(
        items: List<UILadderGoal>,
        allowCompleting: Boolean = true,
        allowHiding: Boolean = true,
        showCompleted: Boolean = false,
        showHidden: Boolean = false,
    ) : this(
        goals = UILadderGoals.SingleList(items),
        allowCompleting,
        allowHiding,
        showCompleted,
        showHidden,
    )
}

sealed class UILadderGoals {
    abstract val rawGoals: List<UILadderGoal>

    abstract fun replaceGoal(
        id: Long,
        block: (UILadderGoal) -> UILadderGoal,
    ): UILadderGoals

    data class SingleList(val items: List<UILadderGoal>) : UILadderGoals() {
        override val rawGoals get() = items

        override fun replaceGoal(
            id: Long,
            block: (UILadderGoal) -> UILadderGoal,
        ) = copy(
            items =
                items.map { currGoal ->
                    if (currGoal.id.toLong() == id) {
                        block(currGoal)
                    } else {
                        currGoal
                    }
                },
        )
    }

    data class CategorizedList(val categories: CategorizedUILadderGoals) : UILadderGoals() {
        override val rawGoals get() = categories.flatMap { it.second }

        override fun replaceGoal(
            id: Long,
            block: (UILadderGoal) -> UILadderGoal,
        ) = copy(
            categories =
                categories.map { category ->
                    if (category.second.any { it.id.toLong() == id }) {
                        category.copy(
                            second =
                                category.second.map { currGoal ->
                                    if (currGoal.id.toLong() == id) {
                                        block(currGoal)
                                    } else {
                                        currGoal
                                    }
                                },
                        )
                    } else {
                        category
                    }
                },
        )
    }
}

data class UILadderGoal(
    val id: Long,
    val goalText: StringDesc,
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
        progressText = "$count / $max", // FIXME hardcoded
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
