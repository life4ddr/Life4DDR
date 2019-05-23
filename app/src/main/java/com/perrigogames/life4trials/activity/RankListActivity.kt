package com.perrigogames.life4trials.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.R
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
        Toast.makeText(this, getString(item.rank.nameRes), Toast.LENGTH_SHORT).show()
    }
}
