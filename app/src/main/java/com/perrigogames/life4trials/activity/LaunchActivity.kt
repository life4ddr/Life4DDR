package com.perrigogames.life4trials.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.manager.FirstRunManager
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * The first launched activity, determines the path through the startup flow that should be taken
 * based on the current save state.
 */
class LaunchActivity: AppCompatActivity(), KoinComponent {

    private val firstRunManager: FirstRunManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(firstRunManager.launchIntent)
        finish()
    }
}
