package com.perrigogames.life4.feature.trials.view

import com.perrigogames.life4.feature.trials.enums.TrialRank
import com.perrigogames.life4.feature.trials.viewmodel.TrialSessionAction
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.color.ColorDesc
import dev.icerock.moko.resources.desc.image.ImageDesc

/**
 * Describes the content of the Trial Session screen.
 */
data class UITrialSession(
    val trialTitle: StringDesc,
    val trialLevel: StringDesc,
    val backgroundImage: ImageDesc,
    val exScoreBar: UIEXScoreBar,
    val targetRank: UITargetRank,
    val content: UITrialSessionContent,
    val buttonText: StringDesc,
    val buttonAction: TrialSessionAction,
)

/**
 * Describes the content of the EX score bar.
 */
data class UIEXScoreBar(
    val labelText: StringDesc,
    val currentEx: Int,
    val hintCurrentEx: Int? = null,
    val maxEx: Int,
    val currentExText: StringDesc,
    val maxExText: StringDesc,
)

/**
 * Describes the content of the Target Rank section for a
 * Trial session.
 */
sealed class UITargetRank {

    abstract val rank: TrialRank
//    abstract val rankIcon: ImageDesc
    abstract val title: StringDesc
    abstract val titleColor: ColorDesc
    abstract val rankGoalItems: List<StringDesc>

    /**
     * Specifies an open selector that can be changed by the
     * user.
     */
    data class Selection(
        override val rank: TrialRank,
        override val title: StringDesc,
        override val titleColor: ColorDesc,
        override val rankGoalItems: List<StringDesc>,
        val availableRanks: List<TrialRank>,
    ) : UITargetRank()

    /**
     * Specifies an in-progress Trial with goals that should
     * still be visible.
     */
    data class InProgress(
        override val rank: TrialRank,
        override val title: StringDesc,
        override val titleColor: ColorDesc,
        override val rankGoalItems: List<StringDesc>
    ) : UITargetRank()

    /**
     * Specifies a completed Trial that doesn't need to show
     * the goals.
     */
    data class Achieved(
        override val rank: TrialRank,
        override val title: StringDesc,
        override val titleColor: ColorDesc,
    ) : UITargetRank() {
        override val rankGoalItems: List<StringDesc> = emptyList()
    }
}

fun UITargetRank.Selection.toInProgress() = UITargetRank.InProgress(rank, title, titleColor, rankGoalItems)

fun UITargetRank.InProgress.toAchieved() = UITargetRank.Achieved(rank, title, titleColor)

/**
 * Describes the content of the bottom half of the screen.
 */
sealed class UITrialSessionContent {

    /**
     * Specifies a summary view with each song getting equal
     * screen space.  Optionally, score/EX information can also
     */
    data class Summary(
        val items: List<Item>,
    ) : UITrialSessionContent() {

        data class Item(
            val jacketUrl: String?,
            val difficultyClassText: StringDesc,
            val difficultyClassColor: ColorDesc,
            val difficultyNumberText: StringDesc,
            val summaryContent: SummaryContent? = null,
        )

        data class SummaryContent(
            val topText: StringDesc?,
            val bottomMainText: StringDesc,
            val bottomSubText: StringDesc,
        )
    }

    /**
     * Specifies a focused song view where the main set of songs
     * is shown with reduced size and information alongside a
     * single focused song with all the information needed to
     * effectively find it.
     */
    data class SongFocused(
        val items: List<Item>,
        val focusedJacketUrl: String?,
        val songTitleText: StringDesc,
        val difficultyClassText: StringDesc,
        val difficultyClassColor: ColorDesc,
        val difficultyNumberText: StringDesc,
        val exScoreText: StringDesc,
    ) : UITrialSessionContent() {

        data class Item(
            val jacketUrl: String?,
            val topText: StringDesc?,
            val bottomBoldText: StringDesc?,
            val bottomTagColor: ColorDesc,
            val tapAction: TrialSessionAction?,
        )
    }
}

/**
 * Describes the content of the song detail bottom sheet, shown
 * when a single song needs editing.
 */
sealed class UITrialBottomSheet {

    open val onDismissAction: TrialSessionAction = TrialSessionAction.HideBottomSheet

    /**
     * Describes the state where the bottom sheet should be used
     * for image capture.
     */
    data class ImageCapture(val index: Int?) : UITrialBottomSheet() {

        fun createResultAction(uri: String) =
            index?.let { index -> TrialSessionAction.PhotoTaken(uri, index) }
                ?: TrialSessionAction.ResultsPhotoTaken(uri)
    }

    /**
     * Placeholder for details panel used only in KM.
     */
    data class DetailsPlaceholder(
        override val onDismissAction: TrialSessionAction = TrialSessionAction.HideBottomSheet,
    ) : UITrialBottomSheet()

    /**
     * Describes the state where the bottom sheet should be used
     * for entering score details.
     */
    data class Details(
        val imagePath: String,
        val fields: List<List<Field>>,
        val isEdit: Boolean,
        val shortcuts: List<Shortcut>,
        override val onDismissAction: TrialSessionAction = TrialSessionAction.HideBottomSheet,
    ) : UITrialBottomSheet() {
    }

    /**
     * Defines a single field on the sheet.
     * @param id the ID of the field, used when communicating input
     *  back to KM when clicking Submit.
     * @param weight the amount of space this field should take up,
     *  relative to the other fields.
     * @param text the initial text to show in the field. Any
     *  changes to text should be tracked in the native code and
     *  submitted using [generateSubmitAction].
     * @param label the text to show by the field to identify it.
     */
    data class Field(
        val id: String,
        val text: String,
        val label: StringDesc,
        val enabled: Boolean = true,
        val weight: Float = 1f,
        val hasError: Boolean = false,
    )

    /**
     * Defines a Shortcut action that can be taken to set some data
     * automatically, with the intent to cut down on redundant asks.
     */
    data class Shortcut(
        val itemText: StringDesc,
        val action: TrialSessionAction,
    )
}