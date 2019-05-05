package com.perrigogames.life4trials

import android.app.Application
import org.greenrobot.eventbus.EventBus

class Life4Application: Application() {

    companion object {
        val eventBus = EventBus()
    }
}