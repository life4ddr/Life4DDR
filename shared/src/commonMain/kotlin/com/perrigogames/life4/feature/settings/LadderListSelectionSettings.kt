package com.perrigogames.life4.feature.settings

import com.perrigogames.life4.SettingsKeys.KEY_IGNORE_LIST
import com.perrigogames.life4.SettingsKeys.KEY_IGNORE_LIST_SET
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalSettingsApi::class)
class LadderListSelectionSettings : SettingsManager() {

    val selectedIgnoreList: Flow<String> =
        settings.getStringFlow(KEY_IGNORE_LIST, "ALL_MUSIC")

    val ignoreListWasSet: Flow<Boolean> =
        settings.getBooleanFlow(KEY_IGNORE_LIST_SET, false)
            .distinctUntilChanged()

    fun setIgnoreList(id: String?) = mainScope.launch {
        id?.also {
            settings.putString(KEY_IGNORE_LIST, it)
            settings.putBoolean(KEY_IGNORE_LIST_SET, true)
        }
            ?: settings.remove(KEY_IGNORE_LIST)
    }
}