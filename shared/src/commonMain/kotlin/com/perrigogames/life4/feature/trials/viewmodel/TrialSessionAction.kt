package com.perrigogames.life4.feature.trials.viewmodel

import com.perrigogames.life4.feature.trials.enums.TrialRank

sealed class TrialSessionAction {
    data object StartTrial : TrialSessionAction()

    data class ChangeTargetRank(
        val target: TrialRank
    ) : TrialSessionAction()

    data class TakePhoto(
        val index: Int,
    ) : TrialSessionAction()

    data object TakeResultsPhoto : TrialSessionAction()

    data class PhotoTaken(
        val photoUri: String,
        val index: Int,
    ) : TrialSessionAction()

    data class ResultsPhotoTaken(
        val photoUri: String,
    ) : TrialSessionAction()

    data object HideBottomSheet : TrialSessionAction()

    data object AdvanceStage : TrialSessionAction()

    data class UseShortcut(
        val songId: String,
        val shortcut: ShortcutType?,
    ) : TrialSessionAction()

    data class EditItem(
        val index: Int,
    ) : TrialSessionAction()

    data class ChangeText(
        val id: String,
        val text: String,
    ) : TrialSessionAction()
}
