package com.perrigogames.life4trials.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.RankDetailsActivity
import com.perrigogames.life4trials.RankDetailsActivity.Companion.ARG_RANK_ENTRY
import com.perrigogames.life4trials.activity.RankListActivityFragment.OnRankListInteractionListener
import com.perrigogames.life4trials.data.RankEntry
import kotlinx.android.synthetic.main.content_rank_list.*

/**
 * Activity displaying the list of ladder ranks that can be obtained.
 */
class RankListActivity : AppCompatActivity(), OnRankListInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank_list)

        layout_container.removeAllViews()
        supportFragmentManager.beginTransaction()
            .add(R.id.layout_container,
                RankListActivityFragment.newInstance(3))
            .commit()
    }

    override fun onListFragmentInteraction(item: RankEntry) {
        startActivity(Intent(this, RankDetailsActivity::class.java).apply {
            putExtra(ARG_RANK_ENTRY, item)
        })
    }
}
