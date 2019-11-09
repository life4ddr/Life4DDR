package com.perrigogames.life4trials.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_INFO_NAME
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_INFO_RIVAL_CODE
import com.perrigogames.life4trials.data.BaseRankGoal
import com.perrigogames.life4trials.data.LadderRank
import com.perrigogames.life4trials.db.GoalStatus
import com.perrigogames.life4trials.event.*
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.ui.rankdetails.RankDetailsFragment
import com.perrigogames.life4trials.ui.rankdetails.RankDetailsViewModel
import com.perrigogames.life4trials.util.*
import com.perrigogames.life4trials.view.JacketCornerView
import kotlinx.android.synthetic.main.activity_player_profile.*
import kotlinx.android.synthetic.main.content_player_profile.*
import kotlinx.android.synthetic.main.item_profile_mode_button.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * An Activity that displays the local player's current profile. This includes
 * info like their name and rank, in-progress goals, and navigation buttons to
 * other experiences like Tournaments or Trials.
 */
class PlayerProfileActivity : AppCompatActivity(), RankDetailsViewModel.OnGoalListInteractionListener {

    private val ladderManager get() = life4app.ladderManager
    private val songDataManager get() = life4app.songDataManager
    private val trialManager get() = life4app.trialManager
    private var rank: LadderRank? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_profile)
        setSupportActionBar(toolbar)
        Life4Application.eventBus.register(this)

        setupContent()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        SharedPrefsUtil.isPreviewEnabled().let { p ->
            menu.findItem(R.id.action_progress_matrix).isVisible = p
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.action_records -> startActivity(Intent(this, TrialRecordsActivity::class.java))
            R.id.action_import_data -> ladderManager.showImportFlow(this)
            R.id.action_progress_matrix -> startActivity(MatrixTestActivity.intent(this, LadderRank.DIAMOND1))
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onDestroy() {
        Life4Application.eventBus.unregister(this)
        super.onDestroy()
    }

    fun onExtraViewClicked(v: View) {
        when (v.id) {
            R.id.view_mode_button_left -> startActivity(Intent(this, TrialListActivity::class.java))
            R.id.view_mode_button_right -> startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onGoalStateChanged(item: BaseRankGoal, goalStatus: GoalStatus, hiddenGoals: Int) = Unit

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
        Life4Application.eventBus.removeStickyEvent(e)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMajorVersion(e: MajorUpdateProcessEvent) {
        when (e.version) {
            MajorUpdate.SONG_DB -> ladderManager.onDatabaseMajorUpdate(this)
            MajorUpdate.A20_REQUIRED -> songDataManager.onA20RequiredUpdate(this)
        }
        Life4Application.eventBus.removeStickyEvent(e)
    }

    private fun setupContent() {
        view_mode_button_left.text_title.text = getString(R.string.trials)
        view_mode_button_left.image_background.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.life4_trials_logo_invert))
        view_mode_button_right.text_title.text = getString(R.string.action_settings)
        view_mode_button_right.image_background.apply {
            setImageDrawable(ContextCompat.getDrawable(this@PlayerProfileActivity, R.drawable.ic_cogwheel))
            setColorFilter(ContextCompat.getColor(this@PlayerProfileActivity, R.color.white))
            scaleX = 0.7f
            scaleY = 0.7f
            setPadding(0, CommonSizes.contentPaddingLarge(resources) + CommonSizes.contentPaddingMed(resources), 0, 0)
        }

        view_corner_view_left.visibilityBool = trialManager.hasEventTrial
        (view_corner_view_left as JacketCornerView).cornerType = JacketCornerView.CornerType.EVENT

        updatePlayerContent()
    }

    private fun updatePlayerContent() {
        text_player_name.text = SharedPrefsUtil.getUserString(this, KEY_INFO_NAME)
        text_player_rival_code.text = SharedPrefsUtil.getUserString(this, KEY_INFO_RIVAL_CODE)
        text_player_rival_code.apply { visibilityBool = text.isNotEmpty() }
        rank = ladderManager.getUserRank()

        image_rank.also {
            it.setOnClickListener { startActivity(Intent(this, RankListActivity::class.java)) }
            it.rank = rank
        }

        val options = RankDetailsFragment.Options(
            hideCompleted = true,
            hideIgnored = false,
            showHeader = false,
            showNextGoals = true,
            allowNextSwitcher = false)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container_current_goals, RankDetailsFragment.newInstance(rank, options))
            .commitNowAllowingStateLoss()
    }
}
