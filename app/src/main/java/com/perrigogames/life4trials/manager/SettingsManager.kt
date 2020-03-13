package com.perrigogames.life4trials.manager

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.perrigogames.life4.MajorUpdate
import com.perrigogames.life4.MajorUpdateProcessEvent
import com.perrigogames.life4.model.BaseModel
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.R
import org.greenrobot.eventbus.EventBus
import org.koin.core.inject

class SettingsManager: BaseModel() {

    private val context: Context by inject()
    private val eventBus: EventBus by inject()

    init {
        initializeDefaults()
    }

    fun initializeDefaults() {
        PreferenceManager.setDefaultValues(context, R.xml.root_preferences, false)
    }

    /** @return a stored flag in the user preferences */
    fun getUserFlag(flag: String, def: Boolean) = userPrefs.getBoolean(flag, def)

    /** @return a stored int in the user preferences */
    fun getUserInt(flag: String, def: Int) = userPrefs.getInt(flag, def)

    /** @return a stored Long in the user preferences */
    fun getUserLong(flag: String, def: Long) = userPrefs.getLong(flag, def)

    /** @return a stored string in the user preferences */
    fun getUserString(flag: String, def: String? = null): String? = userPrefs.getString(flag, def)

    /** @return a stored debug flag in the user preferences, also checking the debug state of the app */
    fun getDebugFlag(flag: String) = BuildConfig.DEBUG && getUserFlag(flag, false)

    fun setUserFlag(flag: String, v: Boolean) = userPrefs.edit(true) { putBoolean(flag, v) }

    fun setUserInt(flag: String, v: Int) = userPrefs.edit(true) { putInt(flag, v) }

    fun setUserLong(flag: String, v: Long) = userPrefs.edit(true) { putLong(flag, v) }

    fun setUserString(flag: String, v: String? = null) = userPrefs.edit(true) { putString(flag, v) }

    fun setDebugFlag(flag: String, v: Boolean) = setUserFlag(flag, v)

    private val userPrefs get() = PreferenceManager.getDefaultSharedPreferences(context)

    override fun onApplicationException() {
        setUserFlag(KEY_APP_CRASHED, true)
    }

    //
    // Major Update
    //
    fun handleMajorUpdate(c: Context) {
        val currentUpdate = getUserInt(KEY_MAJOR_UPDATE, -1)
        MajorUpdate.values()
            .filter { it.ordinal > currentUpdate }
            .forEach {
                Log.i(javaClass.simpleName, "Processing upgrade ${it.name}")
                eventBus.postSticky(MajorUpdateProcessEvent(it))
            }
        setUserInt(KEY_MAJOR_UPDATE, MajorUpdate.values().last().ordinal)
    }

    companion object {
        const val KEY_APP_CRASHED = "app_crashed"
        const val KEY_MAJOR_UPDATE = "major_update"
        const val KEY_SONG_LIST_VERSION = "song_list_version"
    }
}
