package com.perrigogames.life4.feature.sanbai

import com.perrigogames.life4.SettingsKeys
import com.perrigogames.life4.feature.settings.SettingsManager
import kotlinx.datetime.Instant

/**
 * Holds information that the Sanbai integration needs to retain.
 */
interface ISanbaiAPISettings {
    var bearerToken: String
    var refreshToken: String
    var refreshExpires: Instant
    var playerId: String?

    fun setProperties(
        bearerToken: String,
        refreshToken: String,
        refreshExpires: Instant,
        playerId: String?
    ) {
        this.bearerToken = bearerToken
        this.refreshToken = refreshToken
        this.refreshExpires = refreshExpires
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

    override var refreshExpires: Instant
        get() = basicSettings.getStringOrNull(SettingsKeys.KEY_SANBAI_REFRESH_EXPIRES)
            ?.let { Instant.parse(it) }
            ?: Instant.DISTANT_PAST
        set(value) {
            basicSettings.putString(SettingsKeys.KEY_SANBAI_REFRESH_EXPIRES, value.toString())
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