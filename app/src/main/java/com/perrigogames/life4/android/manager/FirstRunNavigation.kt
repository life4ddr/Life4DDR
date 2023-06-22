package com.perrigogames.life4.android.manager

import android.app.Activity
import android.content.Intent
import com.perrigogames.life4.android.activity.firstrun.FirstRankSelectionActivity
import com.perrigogames.life4.android.activity.firstrun.PlacementListActivity
import com.perrigogames.life4.android.activity.profile.PlayerProfileActivity
import com.perrigogames.life4.model.settings.InitState

fun Activity.replaceWithInitActivity(initState: InitState) {
    startActivity(Intent(this, initState.intentClass))
    finish()
}

val InitState.intentClass get() = when (this) {
    InitState.DONE -> PlayerProfileActivity::class.java
    InitState.RANKS -> FirstRankSelectionActivity::class.java
    InitState.PLACEMENTS -> PlacementListActivity::class.java
}