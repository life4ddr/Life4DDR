package com.perrigogames.life4.android.activity.profile

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.perrigogames.life4.DataRequiresAppUpdateEvent
import com.perrigogames.life4.MotdEvent
import com.perrigogames.life4.SettingsKeys.KEY_INFO_NAME
import com.perrigogames.life4.android.GetScoreList
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.activity.settings.SettingsActivity
import com.perrigogames.life4.android.activity.trial.TrialListActivity
import com.perrigogames.life4.android.activity.trial.TrialRecordsActivity
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.databinding.ActivityPlayerProfileBinding
import com.perrigogames.life4.android.manager.AndroidLadderDialogs
import com.perrigogames.life4.android.ui.dialogs.ManualScoreEntryDialog
import com.perrigogames.life4.android.ui.dialogs.MotdDialog
import com.perrigogames.life4.android.ui.rankdetails.RankDetailsViewModel
import com.perrigogames.life4.android.util.openWebUrlFromRes
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.model.LadderDataManager
import com.perrigogames.life4.model.TrialManager
import com.russhwolf.settings.Settings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * An Activity that displays the local player's current profile. This includes
 * info like their name and rank, in-progress goals, and navigation buttons to
 * other experiences like Tournaments or Trials.
 */
class PlayerProfileActivity : AppCompatActivity(), RankDetailsViewModel.OnGoalListInteractionListener, KoinComponent {

    private val ladderDataManager: LadderDataManager by inject()
    private val ladderDialogs: AndroidLadderDialogs by inject()
    private val trialManager: TrialManager by inject()
    private val settings: Settings by inject()

    private lateinit var binding: ActivityPlayerProfileBinding

    private var rank: LadderRank? = null
    private var goalRank: LadderRank? = null

    private val getScores = registerForActivityResult(GetScoreList()) { ladderDialogs.handleSkillAttackImport(this, it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContent {
            LIFE4Theme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        PlayerProfile(
                            onAction = ::handleAction
                        )
                    }
                }
            }
        }
    }

    private fun handleAction(action: PlayerProfileAction) = when (action) {
        PlayerProfileAction.ChangeRank -> startActivity(Intent(this, RankListActivity::class.java))
        PlayerProfileAction.Settings -> startActivity(Intent(this, SettingsActivity::class.java))
        PlayerProfileAction.Trials -> startActivity(Intent(this, TrialListActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_show_motd -> showMotd()
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.action_records -> startActivity(Intent(this, TrialRecordsActivity::class.java))
            R.id.action_web_profile -> openWebUrlFromRes(
                R.string.url_player_profile,
                settings.getStringOrNull(KEY_INFO_NAME)
            )
            R.id.action_import_data -> {
                try {
                    getScores.launch(Unit)
                } catch (e: Exception) {
                    Toast.makeText(this, R.string.no_ddra_manager, Toast.LENGTH_LONG,)
                        .show()
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onRankSubmitClicked() = openWebUrlFromRes(R.string.url_standard_submission_form)

    // FIXME EventBus sticky
    fun onDataRequiresAppUpdate(e: DataRequiresAppUpdateEvent) {
        AlertDialog.Builder(this)
            .setTitle(R.string.update_your_app)
            .setMessage(R.string.remote_data_needs_update)
            .setPositiveButton(R.string.okay, null)
            .create().show()
    }

    // FIXME EventBus sticky
    fun onMotdReceived(e: MotdEvent) {
        showMotd()
    }

    private fun showMotd() = MotdDialog().show(supportFragmentManager, MotdDialog.TAG)

    private fun showAddScoreDialog() = ManualScoreEntryDialog().show(supportFragmentManager, MotdDialog.TAG)

//    private fun setupContent() {
//        binding.content.viewCornerViewLeft.apply {
//            visibilityBool = trialManager.hasEventTrial
//            cornerType = TrialJacketCorner.EVENT
//        }
//    }

//    private fun updatePlayerContent() {
//        if (rank != null) {
//            binding.content.textRank.apply {
//                text = resources.getString(rank!!.nameRes)
//                setTextColor(ContextCompat.getColor(context, rank!!.colorRes))
//            }
//        }
//        binding.content.textRank.isVisible = rank != null
//        binding.content.textChangeRank.isVisible = rank == null
//
//        val options = RankDetailsFragment.Options(
//            hideNonActive = true,
//            showHeader = false,
//            showNextGoals = false,
//            allowNextSwitcher = false)
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.container_current_goals, RankDetailsFragment.newInstance(goalRank, options))
//            .commitNowAllowingStateLoss()
//    }
}
