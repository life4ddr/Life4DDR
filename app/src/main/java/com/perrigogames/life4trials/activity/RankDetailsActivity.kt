package com.perrigogames.life4trials.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4.data.LadderRank
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.event.LadderRanksReplacedEvent
import com.perrigogames.life4trials.manager.LadderManager
import com.perrigogames.life4trials.ui.rankdetails.RankDetailsFragment
import com.perrigogames.life4trials.ui.rankdetails.RankDetailsViewModel
import com.perrigogames.life4trials.util.nameRes
import com.perrigogames.life4trials.util.visibilityBool
import com.perrigogames.life4trials.view.RankHeaderView
import kotlinx.android.synthetic.main.activity_rank_details.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.core.KoinComponent
import org.koin.core.inject

class RankDetailsActivity : AppCompatActivity(), RankHeaderView.NavigationListener, RankDetailsViewModel.OnGoalListInteractionListener, KoinComponent {

    private val ladderManager: LadderManager by inject()
    private val eventBus: EventBus by inject()

    private var rank: LadderRank? = null
    private var showNextGoals: Boolean = false

    private val showWorkToward get() = intent.getBooleanExtra(ARG_SHOW_SET_GOAL, true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank_details)
        if (savedInstanceState == null) {
            setupRank(LadderRank.parse(intent.getLongExtra(ARG_RANK, 0)))
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(rank?.nameRes ?: R.string.no_rank)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onPreviousClicked() {
        rank?.let { setupRank(ladderManager.previousEntry(it)?.rank) }
    }

    override fun onNextClicked() {
        if (rank == null) {
            setupRank(LadderRank.values().first())
        } else {
            val nextEntry = ladderManager.nextEntry(rank)
            if (nextEntry != null) {
                setupRank(nextEntry.rank)
            }
        }
    }

    override fun onNextSwitchToggled(enabled: Boolean) {
        showNextGoals = enabled
        setupRank(rank)
    }

    override fun onStart() {
        super.onStart()
        eventBus.register(this)
    }

    override fun onStop() {
        super.onStop()
        eventBus.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRankListUpdated(e: LadderRanksReplacedEvent) = setupRank(rank)

    fun useRankClicked(v: View) {
        setResult(RESULT_RANK_SELECTED, Intent().putExtra(EXTRA_RANK, rank?.stableId))
        finish()
    }

    fun useRankTargetClicked(v: View) {
        setResult(RESULT_RANK_TARGET_SELECTED, Intent().putExtra(EXTRA_TARGET_RANK, rank?.stableId))
        finish()
    }

    private fun setupRank(rank: LadderRank?) {
        this.rank = rank
        button_use_rank.text = when {
            rank != null -> getString(R.string.i_am_rank_format, getString(rank.nameRes))
            else -> getString(R.string.i_have_no_rank)
        }

        val targetRank = if (showNextGoals) ladderManager.nextEntry(rank)?.rank else rank
        button_work_toward_rank.visibilityBool = showWorkToward && targetRank != null
        (targetRank)?.let { targetRank ->
            button_work_toward_rank.text = getString(R.string.work_towards_rank_format, getString(targetRank.nameRes))
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, RankDetailsFragment.newInstance(
                rank, RankDetailsFragment.Options(showNextGoals = showNextGoals)))
            .commitNow()
    }

    companion object {
        const val ARG_RANK = "ARG_RANK"
        const val ARG_SHOW_SET_GOAL = "ARG_SHOW_SET_GOAL"
        const val RESULT_RANK_SELECTED = 4966
        const val RESULT_RANK_TARGET_SELECTED = 4967
        const val EXTRA_RANK = "EXTRA_RANK"
        const val EXTRA_TARGET_RANK = "EXTRA_TARGET_RANK"

        fun intent(context: Context, rank: LadderRank?, showSetGoal: Boolean = true) =
            Intent(context, RankDetailsActivity::class.java).also {
                it.putExtra(ARG_RANK, rank?.stableId)
                it.putExtra(ARG_SHOW_SET_GOAL, showSetGoal)
            }
    }
}
