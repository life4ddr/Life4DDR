package com.perrigogames.life4.feature.firstrun

import com.perrigogames.life4.MR
import com.perrigogames.life4.SettingsKeys
import com.perrigogames.life4.model.settings.SettingsManager
import com.russhwolf.settings.ExperimentalSettingsApi
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalSettingsApi::class)
class FirstRunSettingsManager : SettingsManager() {

    val initState: Flow<InitState?> = settings.getStringOrNullFlow(SettingsKeys.KEY_INIT_STATE)
        .map { InitState.parse(it) }

    val requireSignin: Flow<Boolean> = initState.map { it == null }

    fun setInitState(state: InitState?) = mainScope.launch {
        state?.also {
            settings.putString(SettingsKeys.KEY_INIT_STATE, it.key)
        } ?: settings.remove(SettingsKeys.KEY_INIT_STATE)
    }
}

enum class InitState(val key: String, val description: ResourceStringDesc) {
    PLACEMENTS(key = "placements", description = StringDesc.Resource(MR.strings.first_run_rank_method_placement)),
    RANKS(key = "ranks", description = StringDesc.Resource(MR.strings.first_run_rank_method_selection)),
    DONE(key = "done", description = StringDesc.Resource(MR.strings.first_run_rank_method_no_rank)),
    ;

    companion object {
        fun parse(key: String?): InitState? = entries.firstOrNull { it.key == key }
    }
}