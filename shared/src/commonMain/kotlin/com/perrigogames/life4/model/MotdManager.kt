package com.perrigogames.life4.model

import com.perrigogames.life4.api.MotdLocalRemoteData
import com.perrigogames.life4.api.base.LocalDataReader
import com.perrigogames.life4.api.base.unwrapLoaded
import com.perrigogames.life4.data.MessageOfTheDay
import com.perrigogames.life4.ktor.GithubDataAPI
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class MotdManager: BaseModel() {

    private val dataReader: LocalDataReader by inject(named(GithubDataAPI.MOTD_FILE_NAME))
    private val settings: Settings by inject()

    private val _motdFlow = MutableSharedFlow<Event?>(replay = 8)
    val motdFlow: SharedFlow<Event?> = _motdFlow

    private val data = MotdLocalRemoteData(dataReader)
//        override fun onDataVersionChanged(data: MessageOfTheDay) {
//            if (settings.getInt(KEY_LAST_MOTD, -1) < data.version) {
//                settings[KEY_LAST_MOTD] = data.version
//                _motdFlow.tryEmit(Event.MotdEvent(data))
//            }
//        }

    init {
        mainScope.launch {
            data.versionState.collect { state ->
                if (state.majorVersionBlocked) {
                    _motdFlow.emit(Event.DataRequiresAppUpdateEvent)
                }
            }
        }
        mainScope.launch {
            data.start()
        }
    }

    val dataVersionString: Flow<String> =
        data.versionState.map { it.versionString }

    val currentMotd: MessageOfTheDay get() = data.dataState.value.unwrapLoaded()!!
}

sealed class Event {
    data object DataRequiresAppUpdateEvent: Event()
    data class MotdEvent(val motd: MessageOfTheDay): Event()
}
