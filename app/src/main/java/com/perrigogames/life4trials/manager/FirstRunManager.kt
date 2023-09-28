package com.perrigogames.life4trials.manager

import android.content.Context
import android.content.Intent
import com.perrigogames.life4trials.activity.*
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_INFO_NAME
import com.perrigogames.life4trials.util.SharedPrefsUtil
import com.perrigogames.life4trials.util.SharedPrefsUtil.KEY_INIT_STATE
import com.perrigogames.life4trials.util.SharedPrefsUtil.VAL_INIT_STATE_DONE
import com.perrigogames.life4trials.util.SharedPrefsUtil.VAL_INIT_STATE_PLACEMENTS
import com.perrigogames.life4trials.util.SharedPrefsUtil.VAL_INIT_STATE_RANKS

/**
 * A manager class that tracks where a user is in the first run process and
 * handles the flows between these states.
 */
class FirstRunManager(private val context: Context): BaseManager() {

    val requireSignin: Boolean get() =
        SharedPrefsUtil.getUserString(context, KEY_INFO_NAME, null) == null
    val showPlacements: Boolean get() =
        stateString == VAL_INIT_STATE_PLACEMENTS
    val showRankList: Boolean get() =
        stateString == VAL_INIT_STATE_RANKS

    val launchIntent: Intent = when {
        requireSignin -> Intent(context, FirstRunInfoActivity::class.java)
        showPlacements -> placementIntent
        showRankList -> rankListIntent
        else -> finishProcessIntent
    }

    val placementIntent: Intent get() {
        stateString = VAL_INIT_STATE_PLACEMENTS
        return Intent(context, PlacementListActivity::class.java)
    }

    val rankListIntent: Intent get() {
        stateString = VAL_INIT_STATE_RANKS
        return Intent(context, FirstRankSelectionActivity::class.java)
    }

    val finishProcessIntent: Intent get() {
        stateString = VAL_INIT_STATE_DONE
        return Intent(context, PlayerProfileActivity::class.java)
    }

    fun setUserBasics(name: String, rivalCode: String?, twitterName: String?) {
        SharedPrefsUtil.setUserString(context, KEY_INFO_NAME, name)
        if (!rivalCode.isNullOrEmpty()) {
            SharedPrefsUtil.setUserString(context, SettingsActivity.KEY_INFO_RIVAL_CODE, rivalCode)
        }
        if (!twitterName.isNullOrEmpty()) {
            SharedPrefsUtil.setUserString(context, SettingsActivity.KEY_INFO_TWITTER_NAME, twitterName)
        }
    }

    private var stateString
        get() = SharedPrefsUtil.getUserString(context, KEY_INIT_STATE, null)
        set(v) = SharedPrefsUtil.setUserString(context, KEY_INIT_STATE, v)
}