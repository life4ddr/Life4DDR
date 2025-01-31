package com.perrigogames.life4.feature.trialsession

import com.perrigogames.life4.enums.TrialRank
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
    val songDetailsBottomSheet: UISongDetailBottomSheet? = null,
)

/**
 * Describes the content of the EX score bar.
 */
data class UIEXScoreBar(
    val currentEx: Int,
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

    /**
     * Specifies an in-progress Trial with goals that should
     * still be visible.
     */
    data class InProgress(
        override val rank: TrialRank,
//        override val rankIcon: ImageDesc,
        override val title: StringDesc,
        override val titleColor: ColorDesc,
        val rankGoalItems: List<StringDesc>
    ) : UITargetRank()

    /**
     * Specifies a completed Trial that doesn't need to show
     * the goals.
     */
    data class Achieved(
        override val rank: TrialRank,
//        override val rankIcon: ImageDesc,
        override val title: StringDesc,
        override val titleColor: ColorDesc,
    ) : UITargetRank()
}

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
        val buttonText: StringDesc,
        val buttonAction: TrialSessionAction,
    ) : UITrialSessionContent() {

        data class Item(
            val jacketUrl: String?,
            val difficultyClassText: StringDesc,
            val difficultyClassColor: ColorDesc,
            val difficultyNumberText: StringDesc,
            val summaryContent: SummaryContent? = null,
        )

        data class SummaryContent(
            val topText: String?,
            val bottomMainText: String,
            val bottomSubText: String,
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
        val focusedJacketUrl: String,
        val songTitleText: String,
        val difficultyClassText: String,
        val difficultyClassColor: ColorDesc,
        val difficultyNumberText: String,
        val buttonText: String,
        val buttonAction: TrialSessionAction,
    ) : UITrialSessionContent() {

        data class Item(
            val jacketUrl: String,
            val topText: String,
            val bottomBoldText: String,
            val bottomTagColor: ColorDesc,
        )
    }
}

/**
 * Describes the content of the song detail bottom sheet, shown
 * when a single song needs editing.
 */
data class UISongDetailBottomSheet(
    val imagePath: String,
    val fields: List<Field>,
    val shortcuts: List<Shortcut>,
) {

    /**
     * Defines a single field on the sheet.
     * @param id the ID of the field, used when communicating input
     *  back to KM when clicking Submit.
     * @param weight the amount of space this field should take up,
     *  relative to the other fields.
     * @param initialText the initial text to show in the field. Any
     *  changes to text should be tracked in the native code and
     *  submitted using [generateSubmitAction].
     * @param placeholder the text to show in the field when there's
     *  no user input.
     */
    data class Field(
        val id: String,
        val weight: Float = 1f,
        val initialText: String,
        val placeholder: String,
    )

    /**
     * Defines a Shortcut action that can be taken to set some data
     * automatically, with the intent to cut down on redundant asks.
     */
    data class Shortcut(
        val itemText: String,
        val action: TrialSessionAction,
    )

    /**
     * Generates the necessary Submit action to send to KM when
     * the Next button is pressed.
     * @param orderedTexts the contents of each of the shown fields
     *  in the same order as the KM data
     */
    fun generateSubmitAction(
        orderedTexts: List<String>
    ) = TrialSessionAction.SubmitFields(
        items = fields.zip(orderedTexts) { field, text ->
            field.id to text
        }
    )
}