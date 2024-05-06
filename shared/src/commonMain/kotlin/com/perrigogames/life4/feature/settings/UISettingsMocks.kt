package com.perrigogames.life4.feature.settings

import com.perrigogames.life4.MR
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc

object UISettingsMocks {
    private const val URL_SHOP_LIFE4 = "https://life4.bigcartel.com/"
    private const val URL_SHOP_DANGERSHARK = "https://www.dangershark.com/"
    private const val URL_FIND_US_ON_X = "https://x.com/life4ddr/"

    val divider = UISettingsItem.Divider

    object Root {
        val editUserItem =
            UISettingsItem.Link(
                title = StringDesc.Resource(MR.strings.edit_user_info),
                action = SettingsAction.Navigate(SettingsPage.EDIT_USER_INFO),
            )
        val gameVersionItem =
            UISettingsItem.Link(
                title = StringDesc.Resource(MR.strings.action_game_version),
                action = SettingsAction.Modal(SettingsPageModal.GameVersion),
            )
        val trialSettingsItem =
            UISettingsItem.Link(
                title = StringDesc.Resource(MR.strings.trial_settings),
                action = SettingsAction.Navigate(SettingsPage.TRIAL_SETTINGS),
            )
        val clearDataItem =
            UISettingsItem.Link(
                title = StringDesc.Resource(MR.strings.clear_data),
                action = SettingsAction.Navigate(SettingsPage.CLEAR_DATA),
            )
        val shopLife4 =
            UISettingsItem.Link(
                title = StringDesc.Resource(MR.strings.action_shop_life4),
                subtitle = StringDesc.Resource(MR.strings.description_shop_life4),
                action = SettingsAction.WebLink(URL_SHOP_LIFE4),
            )
        val shopDangershark =
            UISettingsItem.Link(
                title = StringDesc.Resource(MR.strings.action_shop_dangershark),
                subtitle = StringDesc.Resource(MR.strings.description_shop_dangershark),
                action = SettingsAction.WebLink(URL_SHOP_DANGERSHARK),
            )
        val findUsOnX =
            UISettingsItem.Link(
                title = StringDesc.Resource(MR.strings.find_us_on_x),
                action = SettingsAction.WebLink(URL_FIND_US_ON_X),
            )
        val credits =
            UISettingsItem.Link(
                title = StringDesc.Resource(MR.strings.credits),
                action = SettingsAction.ShowCredits,
            )
        val appVersionItem =
            UISettingsItem.Link(
                title = StringDesc.Raw("Version X"),
                action = SettingsAction.Modal(SettingsPageModal.AppVersion),
            )
        val helpAndFeedbackHeader =
            UISettingsItem.Header(
                title = StringDesc.Resource(MR.strings.help_and_feedback),
            )

        val page =
            UISettingsData(
                screenTitle = StringDesc.Resource(MR.strings.tab_settings),
                settingsItems =
                    listOf(
                        editUserItem,
                        gameVersionItem,
                        trialSettingsItem,
                        clearDataItem,
                        divider,
                        helpAndFeedbackHeader,
                        shopLife4,
                        shopDangershark,
                        findUsOnX,
                        credits,
                        appVersionItem,
                    ),
            )
    }

    object EditUser {
        val page =
            UISettingsData(
                screenTitle = StringDesc.Resource(MR.strings.edit_user_info),
                settingsItems = listOf(),
            )
    }

    object Trial {
        val page =
            UISettingsData(
                screenTitle = StringDesc.Resource(MR.strings.trial_settings),
                settingsItems = listOf(),
            )
    }

    object ClearData {
        val page =
            UISettingsData(
                screenTitle = StringDesc.Resource(MR.strings.clear_data),
                settingsItems = listOf(),
            )
    }
}
