package com.perrigogames.life4trials.util

import android.content.Context
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

    fun clearRanks(c: Context) {
        return with (rankPrefs(c).edit()) {
            clear()
            apply()
        }
    }

    private fun rankPrefs(c: Context) =
        c.getSharedPreferences(c.resources.getString(R.string.rank_preferences_key), Context.MODE_PRIVATE)
}