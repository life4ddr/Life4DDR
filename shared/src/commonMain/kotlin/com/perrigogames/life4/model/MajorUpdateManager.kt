package com.perrigogames.life4.model

import com.perrigogames.life4.SettingsKeys.KEY_MAJOR_UPDATE
import com.perrigogames.life4.log
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import org.koin.core.inject

enum class MajorUpdate {
    SONG_DB, A20_REQUIRED, DOUBLES_FIX
}

class MajorUpdateManager: BaseModel() {

    private val settings: Settings by inject()

    val updates: List<MajorUpdate> by lazy {
        val currentUpdate = settings.getInt(KEY_MAJOR_UPDATE, -1)
        val out = MajorUpdate.values().filter { it.ordinal > currentUpdate }
        out.forEach { log("MajorUpdateManager", "Processing upgrade ${it.name}") }
        settings[KEY_MAJOR_UPDATE] = MajorUpdate.values().last().ordinal
        out
    }
}
