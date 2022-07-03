package com.perrigogames.life4.model

import co.touchlab.kermit.Logger
import com.perrigogames.life4.SettingsKeys.KEY_MAJOR_UPDATE
import com.perrigogames.life4.injectLogger
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import org.koin.core.component.inject

enum class MajorUpdate {
    SONG_DB, A20_REQUIRED, DOUBLES_FIX
}

class MajorUpdateManager: BaseModel() {

    private val logger: Logger by injectLogger("MajorUpdate")
    private val settings: Settings by inject()

    val updates: List<MajorUpdate> by lazy {
        val currentUpdate = settings.getInt(KEY_MAJOR_UPDATE, -1)
        val out = MajorUpdate.values().filter { it.ordinal > currentUpdate }
        out.forEach { logger.i("Processing upgrade ${it.name}") }
        settings[KEY_MAJOR_UPDATE] = MajorUpdate.values().last().ordinal
        out
    }
}
