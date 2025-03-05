package com.perrigogames.life4.feature.settings

import com.perrigogames.life4.MR
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

object UISettingsMocks {

    private const val URL_SHOP_LIFE4 = "https://life4.bigcartel.com/"
    private const val URL_SHOP_DANGERSHARK = "https://www.dangershark.com/"
    private const val URL_FIND_US_ON_X = "https://x.com/life4ddr/"

    val divider = UISettingsItem.Divider

    class Root(isDebug: Boolean) {
        val editUserItem = UISettingsItem.Link(
            title = MR.strings.edit_user_info.desc(),
            action = SettingsAction.Navigate(SettingsPage.EDIT_USER_INFO)
        )
        val gameVersionItem = UISettingsItem.Link(
            title = MR.strings.action_game_version.desc(),
            action = SettingsAction.Modal(SettingsPageModal.GameVersion)
        )
        val trialSettingsItem = UISettingsItem.Link(
            title = MR.strings.trial_settings.desc(),
            action = SettingsAction.Navigate(SettingsPage.TRIAL_SETTINGS)
        )
        val sanbaiSettingsItem = UISettingsItem.Link(
            title = MR.strings.sanbai_settings.desc(),
            action = SettingsAction.Navigate(SettingsPage.SANBAI_SETTINGS)
        )
        val clearDataItem = UISettingsItem.Link(
            title = MR.strings.clear_data.desc(),
            action = SettingsAction.Navigate(SettingsPage.CLEAR_DATA)
        )
        val debugItem = if (isDebug) {
            UISettingsItem.Link(
                title = StringDesc.Raw("Debug Options"),
                action = SettingsAction.Navigate(SettingsPage.DEBUG)
            )
        } else {
            null
        }
        val shopLife4 = UISettingsItem.Link(
            title = MR.strings.action_shop_life4.desc(),
            subtitle = MR.strings.description_shop_life4.desc(),
            action = SettingsAction.WebLink(URL_SHOP_LIFE4)
        )
        val shopDangershark = UISettingsItem.Link(
            title = MR.strings.action_shop_dangershark.desc(),
            subtitle = MR.strings.description_shop_dangershark.desc(),
            action = SettingsAction.WebLink(URL_SHOP_DANGERSHARK)
        )
        val findUsOnX = UISettingsItem.Link(
            title = MR.strings.find_us_on_x.desc(),
            action = SettingsAction.WebLink(URL_FIND_US_ON_X)
        )
        val credits = UISettingsItem.Link(
            title = MR.strings.credits.desc(),
            action = SettingsAction.ShowCredits
        )
        val appVersionItem = UISettingsItem.Link(
            title = StringDesc.Raw("Version X"),
            action = SettingsAction.Modal(SettingsPageModal.AppVersion)
        )
        val helpAndFeedbackHeader = UISettingsItem.Header(
            title = MR.strings.help_and_feedback.desc()
        )

        val page = UISettingsData(
            screenTitle = MR.strings.tab_settings.desc(),
            settingsItems = listOfNotNull(
                editUserItem, gameVersionItem, trialSettingsItem, sanbaiSettingsItem, clearDataItem, debugItem,
                divider,
                helpAndFeedbackHeader, shopLife4, shopDangershark, findUsOnX, credits, appVersionItem
            )
        )
    }

    object EditUser {
        val page = UISettingsData(
            screenTitle = MR.strings.edit_user_info.desc(),
            settingsItems = listOf()
        )
    }

    object Trial {
        val page = UISettingsData(
            screenTitle = MR.strings.trial_settings.desc(),
            settingsItems = listOf()
        )
    }

    object Sanbai {
        val page = UISettingsData(
            screenTitle = MR.strings.sanbai_settings.desc(),
            settingsItems = listOf(
                UISettingsItem.Link(
                    title = MR.strings.refresh_sanbai_library_data.desc(),
                    action = SettingsAction.Sanbai.RefreshLibrary
                ),
                UISettingsItem.Link(
                    title = MR.strings.refresh_sanbai_user_scores.desc(),
                    action = SettingsAction.Sanbai.RefreshUserScores
                )
            )
        )
    }

    object ClearData {
        val page = UISettingsData(
            screenTitle = MR.strings.clear_data.desc(),
            settingsItems = listOf()
        )
    }

    object Debug {
        val page = UISettingsData(
            screenTitle = StringDesc.Raw("Debug"),
            settingsItems = listOf(
                UISettingsItem.Link(
                    title = StringDesc.Raw("Create debug scores"),
                    action = SettingsAction.Debug.SongData
                )
            )
        )
    }
}