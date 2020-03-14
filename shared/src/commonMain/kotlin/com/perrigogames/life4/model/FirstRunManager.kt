package com.perrigogames.life4.model

import com.perrigogames.life4.SettingsKeys.KEY_INFO_NAME
import com.perrigogames.life4.SettingsKeys.KEY_INFO_RIVAL_CODE
import com.perrigogames.life4.SettingsKeys.KEY_INFO_TWITTER_NAME
import com.perrigogames.life4.SettingsKeys.KEY_INIT_STATE
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import org.koin.core.inject

/**
 * A manager class that tracks where a user is in the first run process and
 * handles the flows between these states.
 */
class FirstRunManager: BaseModel() {

    private val settings: Settings by inject()

    val requireSignin: Boolean get() =
        settings.getStringOrNull(KEY_INFO_NAME) == null
    val showPlacements: Boolean get() =
        stateString == VAL_INIT_STATE_PLACEMENTS
    val showRankList: Boolean get() =
        stateString == VAL_INIT_STATE_RANKS

    fun setUserBasics(name: String, rivalCode: String?, twitterName: String?) {
        settings.putString(KEY_INFO_NAME, name)
        if (!rivalCode.isNullOrEmpty()) {
            settings.putString(KEY_INFO_RIVAL_CODE, rivalCode)
        }
        if (!twitterName.isNullOrEmpty()) {
            settings.putString(KEY_INFO_TWITTER_NAME, twitterName)
        }
    }

    var stateString
        get() = settings.getStringOrNull(KEY_INIT_STATE)
        set(v) { settings[KEY_INIT_STATE] = v }

    companion object {
        const val VAL_INIT_STATE_PLACEMENTS = "placements"
        const val VAL_INIT_STATE_RANKS = "ranks"
        const val VAL_INIT_STATE_DONE = "done"
    }
}
