package com.perrigogames.life4trials.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.RankEntry
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.ui.rankdetails.RankDetailsFragment
import com.perrigogames.life4trials.view.RankHeaderView

class RankDetailsActivity : AppCompatActivity(), RankHeaderView.NavigationListener {

    private val ladderManager get() = life4app.ladderManager

    private var rankEntry: RankEntry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rankEntry = intent.getSerializableExtra(ARG_RANK_ENTRY) as? RankEntry
        if (rankEntry == null) {
            finish()
            return
        }

        setContentView(R.layout.activity_rank_details)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RankDetailsFragment.newInstance(rankEntry!!, navigationListener = this))
                .commitNow()
        }
    }

    override fun onPreviousClicked() = navigationButtonClicked(ladderManager.previousEntry(rankEntry!!.rank))

    override fun onNextClicked() = navigationButtonClicked(ladderManager.nextEntry(rankEntry!!.rank))

    private fun navigationButtonClicked(entry: RankEntry?) {
        if (entry != null) {
            startActivity(intent(this, entry))
            finish()
        }
    }

    companion object {
        const val ARG_RANK_ENTRY = "ARG_RANK_ENTRY"

        fun intent(context: Context, entry: RankEntry) = Intent(context, RankDetailsActivity::class.java).also {
            it.putExtra(ARG_RANK_ENTRY, entry)
        }
    }
}
