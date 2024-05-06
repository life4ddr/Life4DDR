package com.perrigogames.life4

import com.perrigogames.life4.feature.songresults.LadderImporter
import com.russhwolf.settings.Settings

interface LadderDialogs {
    val settings: Settings

    fun onClearGoalStates(positive: () -> Unit)

    fun onClearSongResults(positive: () -> Unit)

    fun onRefreshSongDatabase(positive: () -> Unit)

    fun showImportProcessingDialog(
        dataLines: List<String>,
        opMode: LadderImporter.OpMode,
    )

    fun showLadderUpdateToast()

    fun showImportFinishedToast()
}
