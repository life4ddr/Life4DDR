package com.perrigogames.life4trials.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.activity.SettingsActivity

object SharedPrefsUtil {

    const val KEY_INIT = "KEY_INIT"
    const val KEY_RANK_PREFS = "rank_preferences"
    const val KEY_USER_PREFS = "user_preferences"
    const val KEY_TUTORIAL_PREFS = "tutorial_preferences"
    const val KEY_DATA_PREFS = "data_preferences"
    const val KEY_APP_CRASHED = "app_crashed"

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

    /** @return a stored string in the user preferences */
    fun getUserString(c: Context, flag: String, def: String? = null) = userPrefs(c).getString(flag, def)

    /** @return a stored debug flag in the user preferences, also checking the debug state of the app */
    fun getDebugFlag(c: Context, flag: String) = BuildConfig.DEBUG && getUserFlag(c, flag, false)

    fun setUserFlag(c: Context, flag: String, v: Boolean) = userPrefs(c).edit(true) { putBoolean(flag, v) }

    fun setDebugFlag(c: Context, flag: String, v: Boolean) = setUserFlag(c, flag, v)

    fun initializeDefaults(c: Context) {
        userPrefs(c).let {
            if (it.getInt(KEY_INIT, 0) != 1) {
                it.edit().putInt(KEY_INIT, 1)
                    .putBoolean(SettingsActivity.KEY_LIST_TINT_COMPLETED, true)
                    .putBoolean(SettingsActivity.KEY_SUBMISSION_NOTIFICAION, true).apply()
            }
        }
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