package com.perrigogames.life4trials.activity.firstrun

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.perrigogames.life4.model.PlacementManager
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.firstrun.PlacementDetailsActivity.Companion.RESULT_FINISHED
import com.perrigogames.life4.model.FirstRunManager
import com.perrigogames.life4trials.manager.finishProcessIntent
import com.perrigogames.life4trials.manager.rankListIntent
import com.perrigogames.life4trials.ui.firstrun.PlacementListAdapter
import kotlinx.android.synthetic.main.activity_placement_list.*
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * An [AppCompatActivity] displaying the list of Placement sets for the user to select one to play.
 */
class PlacementListActivity : AppCompatActivity(), KoinComponent {

    private val firstRunManager: FirstRunManager by inject()
    private val placementManager: PlacementManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_placement_list)

        recycler_placements.layoutManager = LinearLayoutManager(this)
        recycler_placements.adapter = PlacementListAdapter(placementManager.placements) { id ->
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
