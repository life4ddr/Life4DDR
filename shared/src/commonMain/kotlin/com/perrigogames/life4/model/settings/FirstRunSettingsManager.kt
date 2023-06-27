package com.perrigogames.life4.model.settings

import com.perrigogames.life4.SettingsKeys
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalSettingsApi::class)
class FirstRunSettingsManager : SettingsManager() {

    val initState: Flow<InitState?> = settings.getStringOrNullFlow(SettingsKeys.KEY_INIT_STATE)
        .map { InitState.parse(it) }

    val requireSignin: Flow<Boolean> = initState.map { it == null }

    fun setInitState(state: InitState?) = mainScope.launch {
        state?.also { settings.putString(SettingsKeys.KEY_INIT_STATE, it.key) } ?: settings.remove(SettingsKeys.KEY_INIT_STATE)
    }
}

enum class InitState(val key: String) {
    PLACEMENTS("placements"),
    RANKS("ranks"),
    DONE("done"),
    ;

    companion object {
        fun parse(key: String?): InitState? = values().firstOrNull { it.key == key }
    }
}