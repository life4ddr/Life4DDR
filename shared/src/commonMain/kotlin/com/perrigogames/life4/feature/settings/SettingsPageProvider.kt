package com.perrigogames.life4.feature.settings

import com.perrigogames.life4.AppInfo
import com.perrigogames.life4.MR
import com.perrigogames.life4.SettingsKeys
import com.perrigogames.life4.feature.songresults.SongResultSettings
import com.perrigogames.life4.model.BaseModel
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import org.koin.core.component.inject

@OptIn(ExperimentalSettingsApi::class)
class SettingsPageProvider : BaseModel() {

    private val appInfo: AppInfo by inject()
    private val flowSettings: FlowSettings by inject()
    private val songResultSettings: SongResultSettings by inject()

    private val difficultyTierFlow = songResultSettings.enableDifficultyTiers
    private val removedSongsFlow = songResultSettings.showRemovedSongs

    fun getRootPage(isDebug: Boolean): Flow<UISettingsData> = flowOf(
        UISettingsData(
            screenTitle = MR.strings.tab_settings.desc(),
            settingsItems = listOfNotNull(
                UISettingsItem.Link( // Edit User Info
                    title = MR.strings.edit_user_info.desc(),
                    action = SettingsAction.Navigate(SettingsPage.EDIT_USER_INFO)
                ),
                UISettingsItem.Link( // Song List Settings
                    title = MR.strings.song_list_settings.desc(),
                    action = SettingsAction.Navigate(SettingsPage.SONG_LIST_SETTINGS)
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
                UISettingsItem.Divider,
                UISettingsItem.Header( // Help and Feedback
                    title = MR.strings.help_and_feedback.desc()
                ),
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
                UISettingsItem.Link( // Discord Link
                    title = MR.strings.join_discord.desc(),
                    action = SettingsAction.WebLink(URL_JOIN_DISCORD)
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
                ),
                // TODO support link
            )
        )
    )

    fun getEditUserPage(): Flow<UISettingsData> = flowOf(
        UISettingsData(
            screenTitle = MR.strings.edit_user_info.desc(),
            settingsItems = listOf(
                UISettingsItem.Link( // Player name
                    title = MR.strings.action_name.desc(),
                    action = SettingsAction.Modal(SettingsPageModal.Text(UserInfoSettings.KEY_INFO_NAME))
                ),
                UISettingsItem.Link( // Rival code
                    title = MR.strings.action_rival_code.desc(),
                    action = SettingsAction.Modal(SettingsPageModal.RivalCode)
                ),
                UISettingsItem.Link( // Game version
                    title = MR.strings.action_game_version.desc(),
                    action = SettingsAction.Modal(SettingsPageModal.GameVersion)
                ),
            )
        )
    )

    fun getSongListPage(): Flow<UISettingsData> = combine(
        songResultSettings.enableDifficultyTiers,
        songResultSettings.showRemovedSongs,
    ) { diffTierEnabled, showRemovedSongs, ->
        UISettingsData(
            screenTitle = MR.strings.song_list_settings.desc(),
            settingsItems = listOf(
                UISettingsItem.Checkbox( // Enable Difficulty Tiers
                    title = MR.strings.enable_difficulty_tiers.desc(),
                    action = SettingsAction.SetBoolean(SettingsKeys.KEY_ENABLE_DIFFICULTY_TIERS, !diffTierEnabled),
                    toggled = diffTierEnabled
                ),
                UISettingsItem.Checkbox(
                    title = MR.strings.show_removed_songs.desc(),
                    action = SettingsAction.SetBoolean(SettingsKeys.KEY_SHOW_REMOVED_SONGS, !showRemovedSongs),
                    toggled = showRemovedSongs
                ),
            )
        )
    }

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
                    title = MR.strings.debug_locked_songs.desc(),
                    action = SettingsAction.Debug.SongLockPage
                )
            )
        )
    )

    companion object {
        private const val URL_SHOP_LIFE4 = "https://life4.bigcartel.com/"
        private const val URL_SHOP_DANGERSHARK = "https://www.etsy.com/shop/DangerShark/"
        private const val URL_FIND_US_ON_X = "https://x.com/life4ddr/"
        private const val URL_JOIN_DISCORD = "https://discord.gg/sTYjWNn"
    }
}