package com.perrigogames.life4.feature.settings

import com.perrigogames.life4.model.BaseModel
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import org.koin.core.component.inject

@OptIn(ExperimentalSettingsApi::class)
open class SettingsManager : BaseModel() {
    protected val settings: FlowSettings by inject()
}
