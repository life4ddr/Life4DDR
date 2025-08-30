package com.perrigogames.life4.feature.motd

import com.perrigogames.life4.SettingsKeys.KEY_LAST_MOTD
import com.perrigogames.life4.model.BaseModel
import com.russhwolf.settings.Settings
import org.koin.core.component.inject

/**
 * Settings class to simplify reading and writing Message of the Day data.
 * MotD operates on an integer-based priority system, where a new message
 * is shown if the message's version number is higher than the one that is
 * currently saved.
 */
interface MotdSettings {
    fun shouldShowMotd(version: Long): Boolean
    fun setLastVersion(version: Long)
}

class DefaultMotdSettings : MotdSettings, BaseModel() {
    private val settings: Settings by inject()

    private var lastMotdId: Long
        get() = settings.getLong(KEY_LAST_MOTD, defaultValue = -1)
        set(value) = settings.putLong(KEY_LAST_MOTD, value)

    override fun shouldShowMotd(version: Long): Boolean {
        return version < lastMotdId
    }

    override fun setLastVersion(version: Long) {
        lastMotdId = version
    }
}