package com.perrigogames.life4.android.activity.profile

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.perrigogames.life4.*
import com.perrigogames.life4.SettingsKeys.KEY_IMPORT_PREFER_LEGACY
import com.perrigogames.life4.SettingsKeys.KEY_INFO_NAME
import com.perrigogames.life4.SettingsKeys.KEY_INFO_RIVAL_CODE
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.model.LadderManager
import com.perrigogames.life4.model.TrialManager
import com.perrigogames.life4.android.GetScoreList
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.activity.settings.SettingsActivity
import com.perrigogames.life4.android.activity.trial.TrialListActivity
import com.perrigogames.life4.android.activity.trial.TrialRecordsActivity
import com.perrigogames.life4.android.databinding.ActivityPlayerProfileBinding
import com.perrigogames.life4.android.manager.AndroidLadderDialogs
import com.perrigogames.life4.android.ui.rankdetails.RankDetailsFragment
import com.perrigogames.life4.android.ui.rankdetails.RankDetailsViewModel
import com.perrigogames.life4.android.util.CommonSizes
import com.perrigogames.life4.android.util.openWebUrlFromRes
import com.perrigogames.life4.android.util.visibilityBool
import com.perrigogames.life4.android.view.JacketCornerView
import com.perrigogames.life4.android.view.RankImageView
import com.russhwolf.settings.Settings
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * An Activity that displays the local player's current profile. This includes
 * info like their name and rank, in-progress goals, and navigation buttons to
 * other experiences like Tournaments or Trials.
 */
class PlayerProfileActivity : AppCompatActivity(), RankDetailsViewModel.OnGoalListInteractionListener, KoinComponent {

    private val ladderManager: LadderManager by inject()
    private val ladderDialogs: AndroidLadderDialogs by inject()
    private val trialManager: TrialManager by inject()
    private val settings: Settings by inject()
    private val eventBus: EventBus by inject()

    private lateinit var binding: ActivityPlayerProfileBinding

    private var rank: LadderRank? = null
    private var goalRank: LadderRank? = null

    private val getScores = registerForActivityResult(GetScoreList()) { ladderDialogs.handleSkillAttackImport(this, it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        eventBus.register(this)

        setupContent()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.action_records -> startActivity(Intent(this, TrialRecordsActivity::class.java))
            R.id.action_import_data -> {
                var done = false
                if (!settings.getBoolean(KEY_IMPORT_PREFER_LEGACY, false)) {
                    try {
                        getScores.launch(Unit)
                        done = true
                    } catch (e: Exception) { }
                }
                if (!done) {
                    ladderDialogs.showImportFlow(this)
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onDestroy() {
        eventBus.unregister(this)
        super.onDestroy()
    }

    fun onExtraViewClicked(v: View) {
        when (v.id) {
            R.id.view_mode_button_left -> startActivity(Intent(this, TrialListActivity::class.java))
            R.id.view_mode_button_right -> startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onRankSubmitClicked() = openWebUrlFromRes(R.string.url_standard_submission_form)

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLadderRankModified(e: LadderRankUpdatedEvent) = updatePlayerContent()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTrialListReplaced(e: TrialListReplacedEvent) = updatePlayerContent()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTrialListUpdated(e: TrialListUpdatedEvent) = updatePlayerContent()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongResultsUpdated(e: SongResultsUpdatedEvent) = updatePlayerContent()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRankListUpdated(e: LadderRanksReplacedEvent) = updatePlayerContent()

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onDataRequiresAppUpdate(e: DataRequiresAppUpdateEvent) {
        AlertDialog.Builder(this)
            .setTitle(R.string.update_your_app)
            .setMessage(R.string.remote_data_needs_update)
            .setPositiveButton(R.string.okay, null)
            .create().show()
        eventBus.removeStickyEvent(e)
    }

    private fun setupContent() {
        val context = this

        binding.content.viewModeButtonLeft.apply {
            textTitle.text = getString(R.string.trials)
            imageBackground.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.life4_trials_logo_invert))
        }
        binding.content.viewModeButtonRight.apply {
            textTitle.text = getString(R.string.action_settings)
            imageBackground.apply {
                setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_cogwheel))
                setColorFilter(ContextCompat.getColor(context, R.color.white))
                scaleX = 0.7f
                scaleY = 0.7f
                setPadding(
                    0,
                    CommonSizes.contentPaddingLarge(resources) + CommonSizes.contentPaddingMed(resources),
                    0,
                    0
                )
            }
        }

        binding.content.viewCornerViewLeft.root.apply {
            visibilityBool = trialManager.hasEventTrial
            cornerType = JacketCornerView.CornerType.EVENT
        }

        updatePlayerContent()
    }

    private fun updatePlayerContent() {
        binding.content.textPlayerName.text = settings.getStringOrNull(KEY_INFO_NAME)
        binding.content.textPlayerRivalCode.text = settings.getStringOrNull(KEY_INFO_RIVAL_CODE)
        binding.content.textPlayerRivalCode.apply { visibilityBool = text.isNotEmpty() }
        rank = ladderManager.getUserRank()
        goalRank = ladderManager.getUserGoalRank()

        binding.content.imageRank.also {
            it.setOnClickListener { startActivity(Intent(this, RankListActivity::class.java)) }
            it.rank = rank
        }

        val options = RankDetailsFragment.Options(
            hideCompleted = true,
            hideIgnored = false,
            showHeader = false,
            showNextGoals = false,
            allowNextSwitcher = false)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container_current_goals, RankDetailsFragment.newInstance(goalRank, options))
            .commitNowAllowingStateLoss()
    }
}
