package com.perrigogames.life4

import com.russhwolf.settings.Settings
import org.koin.core.KoinComponent
import org.koin.core.inject

interface LadderDialogs: KoinComponent {

    val settings: Settings
    private val shouldShowImportTutorial get() = !settings.getBoolean(SettingsKeys.KEY_IMPORT_SKIP_DIRECTIONS, false)

    fun showImportFlow() {
        if (shouldShowImportTutorial) {
            showImportDirectionsDialog()
        } else {
            showImportEntryDialog()
        }
    }

    fun onClearGoalStates(positive: () -> Unit)
    fun onClearSongResults(positive: () -> Unit)
    fun onRefreshSongDatabase(positive: () -> Unit)

    fun showImportDirectionsDialog()
    fun showImportEntryDialog()
    fun showImportProcessingDialog(dataString: String)

    fun showLadderUpdateToast()
    fun showImportFinishedToast()
}