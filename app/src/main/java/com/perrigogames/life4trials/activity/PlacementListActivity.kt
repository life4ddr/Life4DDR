package com.perrigogames.life4trials.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.ui.firstrun.PlacementListAdapter
import kotlinx.android.synthetic.main.activity_placement_list.*

/**
 * An [AppCompatActivity] displaying the list of Placement sets for the user to select one to play.
 */
class PlacementListActivity : AppCompatActivity() {

    private val firstRunManager get() = life4app.firstRunManager
    private val placementManager get() = life4app.placementManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_placement_list)

        recycler_placements.layoutManager = LinearLayoutManager(this)
        recycler_placements.adapter = PlacementListAdapter(placementManager.placements) { id ->
            startActivity(PlacementDetailsActivity.intent(this, id))
        }
    }

    fun onRanksClick(v: View) {
        startActivity(firstRunManager.rankListIntent)
        finish()
    }

    fun onNoRankClick(v: View) {
        startActivity(firstRunManager.finishProcessIntent)
        finish()
    }
}
