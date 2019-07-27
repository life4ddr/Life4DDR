package com.perrigogames.life4trials.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.PlacementDetailsActivity.Companion.RESULT_FINISHED
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
            startActivityForResult(PlacementDetailsActivity.intent(this, id), REQUEST_PLACEMENT_FINISH)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PLACEMENT_FINISH && resultCode == RESULT_FINISHED) {
            startActivity(firstRunManager.finishProcessIntent)
            finish()
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

    companion object {
        const val REQUEST_PLACEMENT_FINISH = 4655
    }
}
