package com.perrigogames.life4.android.activity.firstrun

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.perrigogames.life4.android.activity.firstrun.PlacementDetailsActivity.Companion.RESULT_FINISHED
import com.perrigogames.life4.android.databinding.ActivityPlacementListBinding
import com.perrigogames.life4.android.manager.finishProcessIntent
import com.perrigogames.life4.android.manager.rankListIntent
import com.perrigogames.life4.android.ui.firstrun.PlacementListAdapter
import com.perrigogames.life4.model.FirstRunManager
import com.perrigogames.life4.model.PlacementManager
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * An [AppCompatActivity] displaying the list of Placement sets for the user to select one to play.
 */
class PlacementListActivity : AppCompatActivity(), KoinComponent {

    private val firstRunManager: FirstRunManager by inject()
    private val placementManager: PlacementManager by inject()

    private lateinit var binding: ActivityPlacementListBinding

    private val startPlacement = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_FINISHED) {
            startActivity(firstRunManager.finishProcessIntent(this))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlacementListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerPlacements.layoutManager = LinearLayoutManager(this)
        binding.recyclerPlacements.adapter = PlacementListAdapter(placementManager.placements) { id ->
            startPlacement.launch(PlacementDetailsActivity.intent(this, id))
        }

        binding.buttonRanks.setOnClickListener { onRanksClick() }
        binding.buttonNoRank.setOnClickListener { onNoRankClick() }
    }

    private fun onRanksClick() {
        startActivity(firstRunManager.rankListIntent(this))
        finish()
    }

    private fun onNoRankClick() {
        startActivity(firstRunManager.finishProcessIntent(this))
        finish()
    }
}
