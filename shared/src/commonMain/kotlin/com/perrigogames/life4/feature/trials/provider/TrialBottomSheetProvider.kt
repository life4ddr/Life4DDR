package com.perrigogames.life4.feature.trials.provider

import com.perrigogames.life4.MR
import com.perrigogames.life4.data.SongResult
import com.perrigogames.life4.feature.trials.view.UITrialBottomSheet
import com.perrigogames.life4.feature.trials.viewmodel.ShortcutType
import com.perrigogames.life4.feature.trials.viewmodel.TrialSessionAction
import com.perrigogames.life4.longNumberString
import dev.icerock.moko.resources.desc.desc

object TrialBottomSheetProvider {

    const val ID_SCORE = "score"
    const val ID_EX_SCORE = "ex_score"
    const val ID_PERFECTS = "perfects"
    const val ID_GREATS = "greats"

    fun provide(
        songResult: SongResult,
        songId: String,
        imagePath: String,
        shortcut: ShortcutType? = null,
    ): UITrialBottomSheet {
        return UITrialBottomSheet.Details(
            imagePath = imagePath,
            fields = when (shortcut) {
                ShortcutType.MFC -> listOf(
                    scoreField(1_000_000.longNumberString(), enabled = false)
                )

                ShortcutType.PFC -> listOf(
                    scoreField(songResult.score?.toString() ?: "", enabled = false),
                    perfectsField(songResult.perfects?.toString() ?: "")
                )

                ShortcutType.GFC -> listOf(
                    scoreField(songResult.score?.toString() ?: ""),
                    perfectsField(songResult.perfects?.toString() ?: ""),
                    greatsField(songResult.greats?.toString() ?: "")
                )

                null -> listOf(
                    scoreField(songResult.score?.toString() ?: ""),
                )
            },
            shortcuts = shortcuts(songId),
        )
    }

    private fun scoreField(
        initialText: String,
        enabled: Boolean = true
    ) = UITrialBottomSheet.Field(
        id = ID_SCORE,
        text = initialText,
        placeholder = MR.strings.score.desc(),
        enabled = enabled
    )

    private fun exScoreField(
        initialText: String,
        enabled: Boolean = true
    ) = UITrialBottomSheet.Field(
        id = ID_EX_SCORE,
        text = initialText,
        placeholder = MR.strings.ex_score.desc(),
        enabled = enabled
    )

    private fun perfectsField(
        initialText: String,
    ) = UITrialBottomSheet.Field(
        id = ID_PERFECTS,
        text = initialText,
        placeholder = MR.strings.perfects.desc(),
        enabled = true
    )

    private fun greatsField(
        initialText: String,
    ) = UITrialBottomSheet.Field(
        id = ID_GREATS,
        text = initialText,
        placeholder = MR.strings.perfects.desc(),
        enabled = true
    )

    fun shortcuts(songId: String) = listOf(
        UITrialBottomSheet.Shortcut(
            MR.strings.clear_mfc.desc(),
            TrialSessionAction.UseShortcut(songId, ShortcutType.MFC)
        ),
        UITrialBottomSheet.Shortcut(
            MR.strings.clear_pfc.desc(),
            TrialSessionAction.UseShortcut(songId, ShortcutType.PFC)
        ),
        UITrialBottomSheet.Shortcut(
            MR.strings.clear_gfc.desc(),
            TrialSessionAction.UseShortcut(songId, ShortcutType.GFC)
        )
    )
}