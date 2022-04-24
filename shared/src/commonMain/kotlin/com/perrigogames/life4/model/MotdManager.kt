package com.perrigogames.life4.model

import com.perrigogames.life4.DataRequiresAppUpdateEvent
import com.perrigogames.life4.MotdEvent
import com.perrigogames.life4.SettingsKeys.KEY_LAST_MOTD
import com.perrigogames.life4.api.MotdLocalRemoteData
import com.perrigogames.life4.api.base.CompositeData
import com.perrigogames.life4.api.base.LocalDataReader
import com.perrigogames.life4.data.MessageOfTheDay
import com.perrigogames.life4.ktor.GithubDataAPI
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class MotdManager: BaseModel() {

    private val dataReader: LocalDataReader by inject(named(GithubDataAPI.MOTD_FILE_NAME))
    private val eventBus: EventBusNotifier by inject()
    private val settings: Settings by inject()

    private val motdRemote = MotdLocalRemoteData(dataReader, object: CompositeData.NewDataListener<MessageOfTheDay> {
        override fun onDataVersionChanged(data: MessageOfTheDay) {
            if (settings.getInt(KEY_LAST_MOTD, -1) < data.version) {
                settings[KEY_LAST_MOTD] = data.version
                eventBus.postSticky(MotdEvent(data))
            }
        }

        override fun onMajorVersionBlock() = eventBus.postSticky(DataRequiresAppUpdateEvent())
    }).apply { start() }

    val dataVersionString get() = motdRemote.versionString

    val currentMotd: MessageOfTheDay get() = motdRemote.data
}
