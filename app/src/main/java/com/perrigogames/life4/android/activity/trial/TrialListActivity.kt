package com.perrigogames.life4.android.activity.trial

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4.TrialNavigation
import com.perrigogames.life4.android.R
import com.perrigogames.life4.enums.TrialType
import com.perrigogames.life4.android.activity.firstrun.PlacementDetailsActivity
import com.perrigogames.life4.android.activity.settings.SettingsActivity
import com.perrigogames.life4.android.databinding.ActivityTrialListBinding
import com.perrigogames.life4.android.manager.AndroidTrialNavigation
import com.perrigogames.life4.android.ui.trial.TrialListFragment
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.java.KoinJavaComponent.inject

/**
 * Activity for presenting the list of trials to the user. Tapping on a trial will open
 * an associated [TrialDetailsActivity].
 */
class TrialListActivity :
    AppCompatActivity(),
    TrialListFragment.OnTrialListInteractionListener,
    KoinComponent
{

    private val trialNavigation: AndroidTrialNavigation by inject()

    private lateinit var binding: ActivityTrialListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        binding = ActivityTrialListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container_fragment, TrialListFragment())
            .commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onTrialSelected(trialId: String, trialType: TrialType) {
        when (trialType) {
            TrialType.EVENT,
            TrialType.TRIAL -> startActivity(TrialDetailsActivity.intent(this, trialId))
            TrialType.PLACEMENT -> startActivity(
                PlacementDetailsActivity.intent(
                    this,
                    trialId
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_trial_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_open_trial_submission -> trialNavigation.showTrialSubmissionWeb(this)
            R.id.action_records -> startActivity(Intent(this, TrialRecordsActivity::class.java))
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}
