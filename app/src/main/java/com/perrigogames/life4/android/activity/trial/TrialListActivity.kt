package com.perrigogames.life4.android.activity.trial

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.perrigogames.life4.SavedRankUpdatedEvent
import com.perrigogames.life4.TrialListReplacedEvent
import com.perrigogames.life4.TrialListUpdatedEvent
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.activity.firstrun.PlacementDetailsActivity
import com.perrigogames.life4.android.activity.settings.SettingsActivity
import com.perrigogames.life4.android.compose.LIFE4DDRTheme
import com.perrigogames.life4.android.databinding.ActivityTrialListBinding
import com.perrigogames.life4.android.manager.AndroidTrialNavigation
import com.perrigogames.life4.android.ui.trial.TrialJacketList
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.enums.TrialType
import com.perrigogames.life4.model.TrialManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Activity for presenting the list of trials to the user. Tapping on a trial will open
 * an associated [TrialDetailsActivity].
 */
class TrialListActivity : AppCompatActivity(), KoinComponent {

    private val eventBus: EventBus by inject()
    private val trialManager: TrialManager by inject()
    private val trialNavigation: AndroidTrialNavigation by inject()

    private lateinit var binding: ActivityTrialListBinding

    private var viewState by mutableStateOf(trialManager.createViewState())

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        binding = ActivityTrialListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.containerCompose.setContent {
            LIFE4DDRTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TrialJacketList(
                        displayList = viewState.displayTrials,
                        onTrialSelected = this@TrialListActivity::onTrialSelected,
                    )
                }
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        eventBus.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        eventBus.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRankUpdated(e: SavedRankUpdatedEvent) {
        viewState = trialManager.createViewState()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onListUpdated(e: TrialListUpdatedEvent) {
        viewState = trialManager.createViewState()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onListReplaced(e: TrialListReplacedEvent) {
        viewState = trialManager.createViewState()
    }

    private fun onTrialSelected(trial: Trial) = when (trial.type) {
        TrialType.EVENT,
        TrialType.TRIAL -> startActivity(TrialDetailsActivity.intent(this, trial.id))
        TrialType.PLACEMENT -> startActivity(PlacementDetailsActivity.intent(this, trial.id))
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
