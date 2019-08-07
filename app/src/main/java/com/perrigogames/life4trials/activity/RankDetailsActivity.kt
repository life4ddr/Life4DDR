package com.perrigogames.life4trials.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.LadderRank
import com.perrigogames.life4trials.data.RankEntry
import com.perrigogames.life4trials.event.LadderRanksReplacedEvent
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.ui.rankdetails.RankDetailsFragment
import com.perrigogames.life4trials.ui.rankdetails.RankDetailsViewModel
import com.perrigogames.life4trials.view.RankHeaderView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RankDetailsActivity : AppCompatActivity(), RankHeaderView.NavigationListener, RankDetailsViewModel.OnGoalListInteractionListener {

    private val ladderManager get() = life4app.ladderManager

    private var rankEntry: RankEntry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank_details)
        if (savedInstanceState == null) {
            setupRank(intent.getSerializableExtra(ARG_RANK_ENTRY) as? RankEntry)
        }
    }

    override fun onPreviousClicked() {
        rankEntry?.let { setupRank(ladderManager.previousEntry(it.rank)) }
    }

    override fun onNextClicked() {
        if (rankEntry == null) {
            setupRank(ladderManager.findRankEntry(LadderRank.values().first()))
        } else {
            val nextEntry = ladderManager.nextEntry(rankEntry!!.rank)
            if (nextEntry != null) {
                setupRank(nextEntry)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Life4Application.eventBus.register(this)
    }

    override fun onStop() {
        super.onStop()
        Life4Application.eventBus.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRankListUpdated(e: LadderRanksReplacedEvent) = setupRank(ladderManager.findRankEntry(rankEntry?.rank))

    fun useRankClicked(v: View) {
        setResult(RESULT_RANK_SELECTED, Intent().putExtra(EXTRA_RANK, rankEntry?.rank?.stableId))
        finish()
    }

    private fun setupRank(entry: RankEntry?) {
        rankEntry = entry
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, RankDetailsFragment(rankEntry, RankDetailsFragment.Options(), this, this))
            .commitNow()
    }

    companion object {
        const val ARG_RANK_ENTRY = "ARG_RANK_ENTRY"
        const val RESULT_RANK_SELECTED = 4966
        const val EXTRA_RANK = "EXTRA_RANK"

        fun intent(context: Context, entry: RankEntry?) = Intent(context, RankDetailsActivity::class.java).also {
            it.putExtra(ARG_RANK_ENTRY, entry)
        }
    }
}
