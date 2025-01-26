package com.perrigogames.life4.feature.sanbai

import com.perrigogames.life4.SettingsKeys
import com.perrigogames.life4.feature.settings.SettingsManager

/**
 * Holds temporary
 */
interface ISanbaiAPISettings {
    var bearerToken: String
    var refreshToken: String
    var playerId: String?

    fun setProperties(
        bearerToken: String,
        refreshToken: String,
        playerId: String?
    ) {
        this.bearerToken = bearerToken
        this.refreshToken = refreshToken
        this.playerId = playerId
    }
}

class SanbaiAPISettings : SettingsManager(), ISanbaiAPISettings {

    override var bearerToken: String
        get() = basicSettings.getString(SettingsKeys.KEY_SANBAI_BEARER_TOKEN, defaultValue = "")
        set(value) {
            basicSettings.putString(SettingsKeys.KEY_SANBAI_BEARER_TOKEN, value)
        }

    override var refreshToken: String
        get() = basicSettings.getString(SettingsKeys.KEY_SANBAI_REFRESH_TOKEN, defaultValue = "")
        set(value) {
            basicSettings.putString(SettingsKeys.KEY_SANBAI_REFRESH_TOKEN, value)
        }

    override var playerId: String?
        get() = basicSettings.getStringOrNull(SettingsKeys.KEY_SANBAI_PLAYER_ID)
        set(value) {
            if (value != null) {
                basicSettings.putString(SettingsKeys.KEY_SANBAI_PLAYER_ID, value)
            } else {
                basicSettings.remove(SettingsKeys.KEY_SANBAI_PLAYER_ID)
            }
        }
}