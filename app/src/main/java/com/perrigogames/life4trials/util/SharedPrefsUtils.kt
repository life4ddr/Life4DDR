package com.perrigogames.life4trials.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.data.SongResult
import com.perrigogames.life4trials.data.Trial
import com.perrigogames.life4trials.data.TrialRank
import com.perrigogames.life4trials.data.TrialSession

object SharedPrefsUtils {

    const val KEY_RANK_PREFS = "rank_preferences"
    const val KEY_USER_PREFS = "user_preferences"
    const val KEY_TUTORIAL_PREFS = "tutorial_preferences"
    const val KEY_DATA_PREFS = "data_preferences"

    fun getRankForTrial(c: Context, trial: Trial): TrialRank? = getRankForTrial(c, trial.name)

    fun getRankForTrial(c: Context, trialName: String): TrialRank? {
        val index = rankPrefs(c).getInt(trialName, -1)
        return if (index < 0) null else TrialRank.values()[index]
    }

    fun setRankForTrial(c: Context, trial: Trial, rank: TrialRank?) = setRankForTrial(c, trial.name, rank)

    fun setRankForTrial(c: Context, trialName: String, rank: TrialRank?) {
        return with (rankPrefs(c).edit()) {
            if (rank != null) {
                putInt(trialName, rank.ordinal)
            } else {
                remove(trialName)
            }
            apply()
        }
    }

    fun getBestSessionForTrial(c: Context, trial: Trial): Array<SongResult>? {
        val sessionString = rankPrefs(c).getString("${trial.name}_best", null)
        return sessionString?.let { DataUtil.gson.fromJson(sessionString, Array<SongResult>::class.java) }
    }

    fun setBestSessionForTrial(c: Context, session: TrialSession) {
        return with (rankPrefs(c).edit()) {
            putString("${session.trial.name}_best", DataUtil.gson.toJson(session.results))
            apply()
        }
    }

    fun clearRanks(c: Context) = clearPrefs(rankPrefs(c))

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

    private fun rankPrefs(c: Context) =
        c.getSharedPreferences(KEY_RANK_PREFS, Context.MODE_PRIVATE)

    private fun tutorialPrefs(c: Context) =
        c.getSharedPreferences(KEY_TUTORIAL_PREFS, Context.MODE_PRIVATE)

    private fun userPrefs(c: Context) =
        PreferenceManager.getDefaultSharedPreferences(c)

    private fun clearPrefs(prefs: SharedPreferences) = with (prefs.edit()) {
        clear()
        apply()
    }
}