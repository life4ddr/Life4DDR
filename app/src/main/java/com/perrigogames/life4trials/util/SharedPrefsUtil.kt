package com.perrigogames.life4trials.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.event.MajorUpdateProcessEvent

enum class MajorUpdate {
    SONG_DB, A20_REQUIRED
}

object SharedPrefsUtil {

    const val KEY_INIT_STATE = "KEY_INIT_STATE"
    const val VAL_INIT_STATE_PLACEMENTS = "placements"
    const val VAL_INIT_STATE_RANKS = "ranks"
    const val VAL_INIT_STATE_DONE = "done"

    const val KEY_TUTORIAL_PREFS = "tutorial_preferences"
    const val KEY_APP_CRASHED = "app_crashed"
    const val KEY_MAJOR_UPDATE = "major_update"
    const val KEY_SONG_LIST_VERSION = "song_list_version"

    //
    // Major Update
    //
    fun handleMajorUpdate(c: Context) {
        val currentUpdate = getUserInt(c, KEY_MAJOR_UPDATE, -1)
        MajorUpdate.values()
            .filter { it.ordinal > currentUpdate }
            .forEach {
                Log.i(javaClass.simpleName, "Processing upgrade ${it.name}")
                Life4Application.eventBus.postSticky(MajorUpdateProcessEvent(it))
            }
        setUserInt(c, KEY_MAJOR_UPDATE, MajorUpdate.values().last().ordinal)
    }

    //
    // Tutorials
    //
    fun finishTutorial(c: Context, tutorial: String) {
        return with (tutorialPrefs(c).edit()) {
            putBoolean(tutorial, true)
        }
    }

    fun clearTutorials(c: Context) {
        return with (tutorialPrefs(c).edit()) {
            clear()
            apply()
        }
    }

    /** @return a stored flag in the user preferences */
    fun getUserFlag(c: Context, flag: String, def: Boolean) = userPrefs(c).getBoolean(flag, def)

    /** @return a stored int in the user preferences */
    fun getUserInt(c: Context, flag: String, def: Int) = userPrefs(c).getInt(flag, def)

    /** @return a stored Long in the user preferences */
    fun getUserLong(c: Context, flag: String, def: Long) = userPrefs(c).getLong(flag, def)

    /** @return a stored string in the user preferences */
    fun getUserString(c: Context, flag: String, def: String? = null): String? = userPrefs(c).getString(flag, def)

    /** @return a stored debug flag in the user preferences, also checking the debug state of the app */
    fun getDebugFlag(c: Context, flag: String) = BuildConfig.DEBUG && getUserFlag(c, flag, false)

    fun setUserFlag(c: Context, flag: String, v: Boolean) = userPrefs(c).edit(true) { putBoolean(flag, v) }

    fun setUserInt(c: Context, flag: String, v: Int) = userPrefs(c).edit(true) { putInt(flag, v) }

    fun setUserLong(c: Context, flag: String, v: Long) = userPrefs(c).edit(true) { putLong(flag, v) }

    fun setUserString(c: Context, flag: String, v: String? = null) = userPrefs(c).edit(true) { putString(flag, v) }

    fun setDebugFlag(c: Context, flag: String, v: Boolean) = setUserFlag(c, flag, v)

    /** @return true if preview features should be shown */
    fun isPreviewEnabled() = BuildConfig.DEBUG || BuildConfig.BUILD_TYPE == "preview"

    fun initializeDefaults(c: Context) {
        PreferenceManager.setDefaultValues(c, R.xml.root_preferences, false)
    }

    private fun tutorialPrefs(c: Context) =
        c.getSharedPreferences(KEY_TUTORIAL_PREFS, Context.MODE_PRIVATE)

    private fun userPrefs(c: Context) =
        PreferenceManager.getDefaultSharedPreferences(c)

    private fun clearPrefs(prefs: SharedPreferences) = with (prefs.edit()) {
        clear()
        apply()
    }
}
