package com.perrigogames.life4.feature.ladder

import com.perrigogames.life4.util.toStringWithoutDecimal
import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.desc.StringDesc

typealias CategorizedUILadderGoals = List<Pair<UILadderGoals.CategorizedList.Category, List<UILadderGoal>>>

data class UILadderData(
    val goals: UILadderGoals
) {
    constructor(
        items: List<UILadderGoal>
    ) : this(
        goals = UILadderGoals.SingleList(items)
    )
}

sealed class UILadderGoals {

    abstract val rawGoals: List<UILadderGoal>
    abstract fun replaceGoal(id: Long, block: (UILadderGoal) -> UILadderGoal): UILadderGoals

    data class SingleList(val items: List<UILadderGoal>) : UILadderGoals() {

        override val rawGoals get() = items

        override fun replaceGoal(id: Long, block: (UILadderGoal) -> UILadderGoal) = copy(
            items = items.map { currGoal ->
                if (currGoal.id == id) {
                    block(currGoal)
                } else {
                    currGoal
                }
            }
        )
    }

    data class CategorizedList(val categories: CategorizedUILadderGoals) : UILadderGoals() {

        override val rawGoals get() = categories.flatMap { it.second }

        override fun replaceGoal(id: Long, block: (UILadderGoal) -> UILadderGoal) = copy(
            categories = categories.map { category ->
                if (category.second.any { it.id == id}) {
                    category.copy(
                        second = category.second.map { currGoal ->
                            if (currGoal.id == id) {
                                block(currGoal)
                            } else {
                                currGoal
                            }
                        }
                    )
                } else {
                    category
                }
            }
        )

        data class Category(
            val title: StringDesc? = null,
            val goalText: StringDesc? = null
        )
    }
}

data class UILadderGoal(
    val id: Long,
    val goalText: StringDesc,
    val completed: Boolean = false,
    val completeAction: RankListInput? = null,
    val showCheckbox: Boolean = true,
    val hidden: Boolean = false,
    val hideAction: RankListInput? = null,
    val progress: UILadderProgress? = null,
    val expandAction: RankListInput? = null,
    val detailItems: List<UILadderDetailItem> = emptyList(),
) {
    constructor(
        id: Long,
        goalText: StringDesc,
        completed: Boolean = false,
        canComplete: Boolean = true,
        showCheckbox: Boolean = true,
        hidden: Boolean = false,
        canHide: Boolean = true,
        progress: UILadderProgress? = null,
        expandAction: RankListInput? = null,
        detailItems: List<UILadderDetailItem> = emptyList(),
    ) : this (
        id = id,
        goalText = goalText,
        completed = completed,
        completeAction = when (canComplete) {
            true -> RankListInput.OnGoal.ToggleComplete(id)
            false -> null
        },
        showCheckbox = showCheckbox,
        hidden = hidden,
        hideAction = when (canHide) {
            true -> RankListInput.OnGoal.ToggleHidden(id)
            false -> null
        },
        progress = progress,
        expandAction = expandAction,
        detailItems = detailItems,
    )
}

data class UILadderProgress(
    val progressPercent: Float,
    val progressText: String,
) {
    constructor(
        count: Int,
        max: Int,
        showMax: Boolean = true,
    ) : this(
        progressPercent = count.toFloat() / max.toFloat(),
        progressText = if (showMax) {
            "$count / $max"
        } else {
            "$count"
        }
    )

    constructor(
        count: Double,
        max: Double,
        showMax: Boolean = true,
    ) : this(
        progressPercent = (count / max).toFloat(),
        progressText = if (showMax) {
            "${count.toStringWithoutDecimal()} / ${max.toStringWithoutDecimal()}"
        } else {
            count.toStringWithoutDecimal()
        }
    )
}

data class UILadderDetailItem(
    val leftText: String,
    val leftColor: ColorResource? = null,
    val leftWeight: Float = 0.8f,
    val rightText: String? = null,
    val rightColor: ColorResource? = null,
    val rightWeight: Float = 0.2f,
)
