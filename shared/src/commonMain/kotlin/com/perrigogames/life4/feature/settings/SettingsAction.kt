package com.perrigogames.life4.feature.settings

import com.perrigogames.life4.enums.GameVersion

/**
 * An action to be taken when a Settings object is interacted with.
 */
sealed class SettingsAction {
    data object None: SettingsAction()
    data class WebLink(val url: String): SettingsAction()
    data class Navigate(val page: SettingsPage): SettingsAction()
    data object NavigateBack: SettingsAction()
    sealed class Sanbai: SettingsAction() {
        data object RefreshLibrary: Sanbai()
        data object RefreshUserScores: Sanbai()
    }
    data class Email(val email: String): SettingsAction()
    data class SetBoolean(val id: String, val newValue: Boolean): SettingsAction()
    data class SetString(val id: String, val newValue: String): SettingsAction()
    data class SetGameVersion(val newValue: GameVersion): SettingsAction()
    data class Modal(val modal: SettingsPageModal): SettingsAction()
    data object ShowCredits: SettingsAction()
    sealed class Debug: SettingsAction() {
        data object SongLockPage: Debug()
    }
}