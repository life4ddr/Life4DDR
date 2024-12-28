package com.perrigogames.life4.feature.settings

import com.perrigogames.life4.AppInfo
import com.perrigogames.life4.model.BaseModel
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import org.koin.core.component.inject

@OptIn(ExperimentalSettingsApi::class)
open class SettingsManager : BaseModel() {
    private val appInfo: AppInfo by inject()
    private val basicSettings: Settings by inject()
    protected val settings: FlowSettings by inject()

    fun getDebugBoolean(key: String) = appInfo.isDebug && basicSettings.getBoolean(key, false)
}
