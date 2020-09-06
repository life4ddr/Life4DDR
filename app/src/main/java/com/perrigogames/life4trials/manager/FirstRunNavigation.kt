package com.perrigogames.life4trials.manager

import android.content.Context
import android.content.Intent
import com.perrigogames.life4.model.FirstRunManager
import com.perrigogames.life4trials.activity.firstrun.FirstRankSelectionActivity
import com.perrigogames.life4trials.activity.firstrun.FirstRunInfoActivity
import com.perrigogames.life4trials.activity.firstrun.PlacementListActivity
import com.perrigogames.life4trials.activity.profile.PlayerProfileActivity

fun FirstRunManager.launchIntent(context: Context): Intent = when {
    requireSignin -> Intent(context, FirstRunInfoActivity::class.java)
    showPlacements -> placementIntent(context)
    showRankList -> rankListIntent(context)
    else -> finishProcessIntent(context)
}

fun FirstRunManager.placementIntent(context: Context): Intent {
    stateString = FirstRunManager.VAL_INIT_STATE_PLACEMENTS
    return Intent(context, PlacementListActivity::class.java)
}

fun FirstRunManager.rankListIntent(context: Context): Intent {
    stateString = FirstRunManager.VAL_INIT_STATE_RANKS
    return Intent(context, FirstRankSelectionActivity::class.java)
}

fun FirstRunManager.finishProcessIntent(context: Context): Intent {
    stateString = FirstRunManager.VAL_INIT_STATE_DONE
    return Intent(context, PlayerProfileActivity::class.java)
}
