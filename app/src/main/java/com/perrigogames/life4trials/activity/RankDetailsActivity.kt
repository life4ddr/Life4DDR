package com.perrigogames.life4trials.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.RankEntry
import com.perrigogames.life4trials.ui.rankdetails.RankDetailsFragment

class RankDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val entry = intent.getSerializableExtra(ARG_RANK_ENTRY) as? RankEntry
        if (entry == null) {
            finish()
            return
        }

        setContentView(R.layout.activity_rank_details)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RankDetailsFragment.newInstance(entry))
                .commitNow()
        }
    }

    companion object {
        const val ARG_RANK_ENTRY = "ARG_RANK_ENTRY"
    }
}
