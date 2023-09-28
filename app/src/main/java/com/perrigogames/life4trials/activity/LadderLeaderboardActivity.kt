package com.perrigogames.life4trials.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.api.ApiPlayer
import com.perrigogames.life4trials.ui.leaderboard.LeaderboardFragment
import com.perrigogames.life4trials.ui.leaderboard.LeaderboardFragment.OnLeaderboardInteractionListener

class LadderLeaderboardActivity : AppCompatActivity(), OnLeaderboardInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ladder_leaderboard)
        supportFragmentManager.beginTransaction()
            .add(R.id.container_fragment, LeaderboardFragment.newLadderInstance())
            .commit()
    }

    override fun onListFragmentInteraction(item: ApiPlayer?) {
    }
}
