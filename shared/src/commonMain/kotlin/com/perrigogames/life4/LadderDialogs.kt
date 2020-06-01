package com.perrigogames.life4

import com.russhwolf.settings.Settings

interface LadderDialogs {

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
    fun showImportProcessingDialog(dataLines: List<String>, legacy: Boolean)

    fun showLadderUpdateToast()
    fun showImportFinishedToast()
}