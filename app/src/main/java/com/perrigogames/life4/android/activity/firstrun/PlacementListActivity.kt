package com.perrigogames.life4.android.activity.firstrun

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.activity.firstrun.PlacementDetailsActivity.Companion.RESULT_FINISHED
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.manager.intentClass
import com.perrigogames.life4.android.manager.replaceWithInitActivity
import com.perrigogames.life4.model.PlacementManager
import com.perrigogames.life4.model.settings.InitState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * An [AppCompatActivity] displaying the list of Placement sets for the user to select one to play.
 */
class PlacementListActivity : AppCompatActivity(), KoinComponent {

    private val placementManager: PlacementManager by inject()

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
                    Column {
                        PlacementScreen(
                            data = placementManager.createUiData(),
                            onPlacementSelected = { startPlacement.launch(PlacementDetailsActivity.intent(this@PlacementListActivity, it)) },
                            modifier = Modifier.weight(1f),
                            onPlacementBypassed = { TODO() }
                        )
                        Row {
                            Button(
                                onClick = { onRanksClick() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(R.string.rank_list))
                            }
                            Button(
                                onClick = { onNoRankClick() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(R.string.start_no_rank))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onRanksClick() = replaceWithInitActivity(InitState.RANKS)

    private fun onNoRankClick() = replaceWithInitActivity(InitState.DONE)
}
