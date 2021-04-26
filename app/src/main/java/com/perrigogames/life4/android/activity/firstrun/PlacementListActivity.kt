package com.perrigogames.life4.android.activity.firstrun

import android.content.Intent
import android.os.Bundle
import android.view.View
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlacementListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerPlacements.layoutManager = LinearLayoutManager(this)
        binding.recyclerPlacements.adapter = PlacementListAdapter(placementManager.placements) { id ->
            startActivityForResult(PlacementDetailsActivity.intent(this, id),
                REQUEST_PLACEMENT_FINISH
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PLACEMENT_FINISH && resultCode == RESULT_FINISHED) {
            startActivity(firstRunManager.finishProcessIntent(this))
            finish()
        }
    }

    fun onRanksClick(v: View) {
        startActivity(firstRunManager.rankListIntent(this))
        finish()
    }

    fun onNoRankClick(v: View) {
        startActivity(firstRunManager.finishProcessIntent(this))
        finish()
    }

    companion object {
        const val REQUEST_PLACEMENT_FINISH = 4655
    }
}
