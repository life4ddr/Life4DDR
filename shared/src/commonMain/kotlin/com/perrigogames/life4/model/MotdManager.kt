package com.perrigogames.life4.model

import com.perrigogames.life4.MotdEvent
import com.perrigogames.life4.SettingsKeys.KEY_LAST_MOTD
import com.perrigogames.life4.api.FetchListener
import com.perrigogames.life4.api.MotdRemoteData
import com.perrigogames.life4.data.MessageOfTheDay
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import org.koin.core.inject

class MotdManager: BaseModel() {

    private val eventBus: EventBusNotifier by inject()
    private val settings: Settings by inject()

    var currentMotd: MessageOfTheDay? = null

    init {
        MotdRemoteData(listener = object: FetchListener<MessageOfTheDay> {
            override fun onFetchUpdated(data: MessageOfTheDay) {
                currentMotd = data
                if (settings.getInt(KEY_LAST_MOTD, -1) < data.version) {
                    settings[KEY_LAST_MOTD] = data.version
                    eventBus.postSticky(MotdEvent(data))
                }
            }

            override fun onFetchFailed() = Unit  // TODO log this
        }).fetch()
    }
}
