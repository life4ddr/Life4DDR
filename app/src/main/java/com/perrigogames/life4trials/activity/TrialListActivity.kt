package com.perrigogames.life4trials.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.TrialType
import com.perrigogames.life4trials.ui.triallist.TrialListFragment
import kotlinx.android.synthetic.main.activity_trial_list.*

/**
 * Activity for presenting the list of trials to the user. Tapping on a trial will open
 * an associated [TrialDetailsActivity].
 */
class TrialListActivity : AppCompatActivity(), TrialListFragment.OnTrialListInteractionListener {

    private val useGrid: Boolean get() = intent?.extras?.getBoolean(EXTRA_GRID, true) ?: true

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trial_list)
        setSupportActionBar(toolbar)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container_fragment, TrialListFragment.newInstance(useGrid))
            .commit()
    }

    override fun onTrialSelected(trialId: String, trialType: TrialType) {
        when (trialType) {
            TrialType.TRIAL -> startActivity(TrialDetailsActivity.intent(this, trialId))
            TrialType.PLACEMENT -> startActivity(PlacementDetailsActivity.intent(this, trialId))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_trial_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_list_view -> restartActivity(false)
            R.id.action_grid_view -> restartActivity(true)
            R.id.action_records -> startActivity(Intent(this, TrialRecordsActivity::class.java))
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun restartActivity(grid: Boolean = useGrid) {
        intent.putExtra(EXTRA_GRID, grid)
        recreate()
    }

    companion object {
        const val EXTRA_GRID = "EXTRA_GRID"
    }
}
