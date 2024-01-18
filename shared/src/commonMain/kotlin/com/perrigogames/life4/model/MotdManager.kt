package com.perrigogames.life4.model

import com.perrigogames.life4.api.MotdLocalRemoteData
import com.perrigogames.life4.api.base.LocalDataReader
import com.perrigogames.life4.api.base.unwrapLoaded
import com.perrigogames.life4.data.MessageOfTheDay
import com.perrigogames.life4.ktor.GithubDataAPI
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class MotdManager: BaseModel() {

    private val dataReader: LocalDataReader by inject(named(GithubDataAPI.MOTD_FILE_NAME))
    private val settings: Settings by inject()

    private val _motdFlow = MutableSharedFlow<Event?>(replay = 8)
    val motdFlow: SharedFlow<Event?> = _motdFlow

    private val motdRemote = MotdLocalRemoteData(dataReader).apply { start() }
//        override fun onDataVersionChanged(data: MessageOfTheDay) {
//            if (settings.getInt(KEY_LAST_MOTD, -1) < data.version) {
//                settings[KEY_LAST_MOTD] = data.version
//                _motdFlow.tryEmit(Event.MotdEvent(data))
//                // FIXME eventBus.postSticky(MotdEvent(data))
//            }
//        }
//
//        override fun onMajorVersionBlock() {
//            _motdFlow.tryEmit(Event.DataRequiresAppUpdateEvent)
//            // FIXME eventBus.postSticky(DataRequiresAppUpdateEvent())
//        }

    val dataVersionString get() = motdRemote.versionState.value.versionString

    val currentMotd: MessageOfTheDay get() = motdRemote.dataState.value.unwrapLoaded()!!
}

sealed class Event {
    object DataRequiresAppUpdateEvent: Event()
    data class MotdEvent(val motd: MessageOfTheDay): Event()
}
