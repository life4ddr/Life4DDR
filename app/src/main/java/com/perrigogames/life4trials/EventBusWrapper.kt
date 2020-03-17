package com.perrigogames.life4trials

import com.perrigogames.life4.model.EventBusNotifier
import org.greenrobot.eventbus.EventBus
import org.koin.core.KoinComponent
import org.koin.core.inject

class AndroidEventBusNotifier: EventBusNotifier, KoinComponent {
    private val eventBus: EventBus by inject()
    override fun post(event: Any) = eventBus.post(event)
    override fun postSticky(event: Any) = eventBus.postSticky(event)
}
