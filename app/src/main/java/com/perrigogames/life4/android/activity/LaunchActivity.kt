package com.perrigogames.life4.android.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.perrigogames.life4.android.activity.firstrun.FirstRunInfoActivity
import com.perrigogames.life4.android.activity.firstrun.PlacementListActivity
import com.perrigogames.life4.android.activity.profile.PlayerProfileActivity
import com.perrigogames.life4.android.activity.profile.RankListActivity
import com.perrigogames.life4.model.settings.FirstRunSettingsManager
import com.perrigogames.life4.model.settings.InitState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * The first launched activity, determines the path through the startup flow that should be taken
 * based on the current save state.
 */
class LaunchActivity: AppCompatActivity(), KoinComponent {

    private val firstRunSettingsManager: FirstRunSettingsManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            combine(
                firstRunSettingsManager.requireSignin,
                firstRunSettingsManager.initState,
            ) { requireSignin, initState ->
                requireSignin to initState
            }.collect { (requireSignin, initState) ->
                println("FOOBAR $requireSignin $initState")
                val intent = Intent(this@LaunchActivity, when {
                    requireSignin -> FirstRunInfoActivity::class.java
                    initState == InitState.PLACEMENTS -> PlacementListActivity::class.java
                    initState == InitState.RANKS -> RankListActivity::class.java
                    else -> PlayerProfileActivity::class.java
                })
                startActivity(intent)
                finish()
            }
        }
    }
}
