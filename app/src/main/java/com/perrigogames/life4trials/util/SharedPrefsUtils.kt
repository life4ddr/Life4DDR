package com.perrigogames.life4trials.util

import android.content.Context
import androidx.preference.PreferenceManager
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.Trial
import com.perrigogames.life4trials.data.TrialRank

object SharedPrefsUtils {

    fun getRankForTrial(c: Context, trial: Trial): TrialRank? {
        val index = rankPrefs(c).getInt(trial.name, -1)
        return if (index < 0) null else TrialRank.values()[index]
    }

    fun setRankForTrial(c: Context, trial: Trial, rank: TrialRank) {
        return with (rankPrefs(c).edit()) {
            putInt(trial.name, rank.ordinal)
            apply()
        }
    }

    fun clearRanks(c: Context) = with (rankPrefs(c).edit()) {
        clear()
        apply()
    }

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

    /** @return a stored debug flag in the user preferences, also checking the debug state of the app */
    fun getDebugFlag(c: Context, flag: String) = BuildConfig.DEBUG && getUserFlag(c, flag, false)

    private fun rankPrefs(c: Context) =
        c.getSharedPreferences(c.resources.getString(R.string.rank_preferences_key), Context.MODE_PRIVATE)

    private fun tutorialPrefs(c: Context) =
        c.getSharedPreferences(c.resources.getString(R.string.tutorial_preferences_key), Context.MODE_PRIVATE)

    private fun userPrefs(c: Context) =
        PreferenceManager.getDefaultSharedPreferences(c)
}