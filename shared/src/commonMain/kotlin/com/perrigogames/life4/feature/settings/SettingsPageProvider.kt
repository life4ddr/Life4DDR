package com.perrigogames.life4.feature.settings

import com.perrigogames.life4.AppInfo
import com.perrigogames.life4.MR
import com.perrigogames.life4.SettingsKeys
import com.perrigogames.life4.model.BaseModel
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.koin.core.component.inject

@OptIn(ExperimentalSettingsApi::class)
class SettingsPageProvider : BaseModel() {

    private val appInfo: AppInfo by inject()
    private val flowSettings: FlowSettings by inject()

    private val difficultyTierFlow = flowSettings
        .getBooleanOrNullFlow(SettingsKeys.KEY_ENABLE_DIFFICULTY_TIERS)

    fun getRootPage(isDebug: Boolean): Flow<UISettingsData> =
        difficultyTierFlow.map { it ?: false }
            .map { diffTierEnabled ->
                UISettingsData(
                    screenTitle = MR.strings.tab_settings.desc(),
                    settingsItems = listOfNotNull(
                        UISettingsItem.Link( // User Info
                            title = MR.strings.edit_user_info.desc(),
                            action = SettingsAction.Navigate(SettingsPage.EDIT_USER_INFO)
                        ),
                        UISettingsItem.Link( // Game Version
                            title = MR.strings.action_game_version.desc(),
                            action = SettingsAction.Modal(SettingsPageModal.GameVersion)
                        ),
                        UISettingsItem.Checkbox( // Enable Difficulty Tiers
                            title = MR.strings.enable_difficulty_tiers.desc(),
                            action = SettingsAction.SetBoolean(SettingsKeys.KEY_ENABLE_DIFFICULTY_TIERS, !diffTierEnabled),
                            toggled = !diffTierEnabled
                        ),
                        UISettingsItem.Link( // Trial Settings
                            title = MR.strings.trial_settings.desc(),
                            action = SettingsAction.Navigate(SettingsPage.TRIAL_SETTINGS)
                        ),
                        UISettingsItem.Link( // Sanbai Settings
                            title = MR.strings.sanbai_settings.desc(),
                            action = SettingsAction.Navigate(SettingsPage.SANBAI_SETTINGS)
                        ),
                        UISettingsItem.Link( // Clear Data
                            title = MR.strings.clear_data.desc(),
                            action = SettingsAction.Navigate(SettingsPage.CLEAR_DATA)
                        ),
                        if (isDebug) {
                            UISettingsItem.Link( // Debug Options
                                title = StringDesc.Raw("Debug Options"),
                                action = SettingsAction.Navigate(SettingsPage.DEBUG)
                            )
                        } else {
                            null
                        },
                        UISettingsItem.Link( // Shop LIFE4
                            title = MR.strings.action_shop_life4.desc(),
                            subtitle = MR.strings.description_shop_life4.desc(),
                            action = SettingsAction.WebLink(URL_SHOP_LIFE4)
                        ),
                        UISettingsItem.Link( // Shop Dangershark
                            title = MR.strings.action_shop_dangershark.desc(),
                            subtitle = MR.strings.description_shop_dangershark.desc(),
                            action = SettingsAction.WebLink(URL_SHOP_DANGERSHARK)
                        ),
                        UISettingsItem.Link( // X Link
                            title = MR.strings.find_us_on_x.desc(),
                            action = SettingsAction.WebLink(URL_FIND_US_ON_X)
                        ),
                        UISettingsItem.Link( // Credits
                            title = MR.strings.credits.desc(),
                            action = SettingsAction.ShowCredits
                        ),
                        UISettingsItem.Link( // Version String
                            title = StringDesc.Raw("Version ${appInfo.version}"),
                            action = SettingsAction.Modal(SettingsPageModal.AppVersion)
                        ),
                        UISettingsItem.Header( // Help and Feedback
                            title = MR.strings.help_and_feedback.desc()
                        )
                    )
                )
            }

    fun getEditUserPage(): Flow<UISettingsData> = flowOf(
        UISettingsData(
            screenTitle = MR.strings.edit_user_info.desc(),
            settingsItems = listOf()
        )
    )

    fun getTrialPage(): Flow<UISettingsData> = flowOf(
        UISettingsData(
            screenTitle = MR.strings.trial_settings.desc(),
            settingsItems = listOf()
        )
    )

    fun getSanbaiPage(): Flow<UISettingsData> = flowOf(
        UISettingsData(
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
    )

    fun getClearDataPage(): Flow<UISettingsData> = flowOf(
        UISettingsData(
            screenTitle = MR.strings.clear_data.desc(),
            settingsItems = listOf()
        )
    )

    fun getDebugPage(): Flow<UISettingsData> = flowOf(
        UISettingsData(
            screenTitle = StringDesc.Raw("Debug"),
            settingsItems = listOf(
                UISettingsItem.Link(
                    title = StringDesc.Raw("Create debug scores"),
                    action = SettingsAction.Debug.SongData
                )
            )
        )
    )

    companion object {
        private const val URL_SHOP_LIFE4 = "https://life4.bigcartel.com/"
        private const val URL_SHOP_DANGERSHARK = "https://www.dangershark.com/"
        private const val URL_FIND_US_ON_X = "https://x.com/life4ddr/"
    }
}