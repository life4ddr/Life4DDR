package com.perrigogames.life4.feature.songresults

import com.perrigogames.life4.GameConstants.HIGHEST_DIFFICULTY
import com.perrigogames.life4.MR
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.util.CompoundIntRange
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc

data class UIFilterView(
    val playStyleSelector: List<UIPlayStyleSelection>? = null,
    val difficultyClassSelector: List<UIDifficultyClassSelection>,
    val difficultyNumberTitle: StringDesc = StringDesc.Resource(MR.strings.label_difficulty_number),
    val difficultyNumberRange: CompoundIntRange = CompoundIntRange(1..HIGHEST_DIFFICULTY),
    val clearTypeTitle: StringDesc = StringDesc.Resource(MR.strings.label_clear_type),
    val clearTypeRange: CompoundIntRange = CompoundIntRange(0 until ClearType.entries.size),
    val scoreRangeBottomValue: Int? = null,
    val scoreRangeBottomHint: StringDesc = StringDesc.Raw("0"),
    val scoreRangeTopValue: Int? = null,
    val scoreRangeTopHint: StringDesc = StringDesc.Raw("1,000,000"),
) {

    constructor(
        showPlayStyleSelector: Boolean = true,
        selectedPlayStyle: PlayStyle = PlayStyle.SINGLE,
        selectedDifficultyClasses: List<DifficultyClass> = DifficultyClass.entries,
        difficultyNumberSelection: IntRange? = null,
        clearTypeSelection: IntRange? = null,
        scoreRangeBottomValue: Int? = null,
        scoreRangeTopValue: Int? = null
    ) : this(
        playStyleSelector = if (showPlayStyleSelector) {
            PlayStyle.entries.map {
                UIPlayStyleSelection(
                    text = it.uiName,
                    selected = it == selectedPlayStyle,
                    action = UIFilterAction.SelectPlayStyle(it)
                )
            }
        } else {
            null
        },
        difficultyClassSelector = DifficultyClass.entries.mapNotNull { diff ->
            if (selectedPlayStyle == PlayStyle.DOUBLE && diff == DifficultyClass.BEGINNER) {
                return@mapNotNull null
            }
            UIDifficultyClassSelection(
                text = StringDesc.Raw(selectedPlayStyle.aggregateString(diff)),
                selected = selectedDifficultyClasses.contains(diff),
                action = UIFilterAction.ToggleDifficultyClass(diff, !selectedDifficultyClasses.contains(diff))
            )
        },
        difficultyNumberRange = CompoundIntRange(
            outerRange = 1..HIGHEST_DIFFICULTY,
            innerRange = difficultyNumberSelection
        ),
        clearTypeRange = CompoundIntRange(
            outerRange = 0 until ClearType.entries.size,
            innerRange = clearTypeSelection
        ),
        scoreRangeBottomValue = scoreRangeBottomValue,
        scoreRangeTopValue = scoreRangeTopValue,
    )
}

data class UIPlayStyleSelection(
    val text: StringDesc,
    val selected: Boolean,
    val action: UIFilterAction
)

data class UIDifficultyClassSelection(
    val text: StringDesc,
    val selected: Boolean,
    val action: UIFilterAction
)

sealed class UIFilterAction {
    data class SelectPlayStyle(val playStyle: PlayStyle): UIFilterAction()
    data class ToggleDifficultyClass(val difficultyClass: DifficultyClass, val selected: Boolean): UIFilterAction()
    data class SetDifficultyNumberRange(val range: IntRange): UIFilterAction() {
        constructor(min: Int, max: Int) : this(min..max)
    }
    data class SetClearTypeRange(val range: IntRange): UIFilterAction() {
        constructor(min: Int, max: Int) : this(min..max)
    }
    data class SetScoreRange(val first: Int? = null, val last: Int? = null): UIFilterAction()
}

fun FilterState.toUIFilterView(showPlayStyleSelector: Boolean) = UIFilterView(
    showPlayStyleSelector = showPlayStyleSelector,
    selectedPlayStyle = selectedPlayStyle,
    selectedDifficultyClasses = difficultyClassSelection,
    difficultyNumberSelection = difficultyNumberRange,
    clearTypeSelection = clearTypeRange,
    scoreRangeBottomValue = scoreRangeBottomValue,
    scoreRangeTopValue = scoreRangeTopValue
)
