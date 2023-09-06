package com.perrigogames.life4.android.activity.firstrun

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import com.perrigogames.life4.android.activity.firstrun.PlacementDetailsActivity.Companion.RESULT_FINISHED
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.databinding.ActivityPlacementListBinding
import com.perrigogames.life4.android.manager.intentClass
import com.perrigogames.life4.android.manager.replaceWithInitActivity
import com.perrigogames.life4.model.PlacementManager
import com.perrigogames.life4.model.settings.FirstRunSettingsManager
import com.perrigogames.life4.model.settings.InitState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * An [AppCompatActivity] displaying the list of Placement sets for the user to select one to play.
 */
class PlacementListActivity : AppCompatActivity(), KoinComponent {

    private val firstRunSettings: FirstRunSettingsManager by inject()
    private val placementManager: PlacementManager by inject()

    private lateinit var binding: ActivityPlacementListBinding

    private val startPlacement = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_FINISHED) {
            startActivity(Intent(this, InitState.DONE.intentClass))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LIFE4Theme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                ) {
                    PlacementScreen(
                        onComplete = ::replaceWithInitActivity,
                        onClose = { finish() },
                    )
                }
            }
        }
    }

    private fun onRanksClick() = replaceWithInitActivity(InitState.RANKS)

    private fun onNoRankClick() = replaceWithInitActivity(InitState.DONE)
}
