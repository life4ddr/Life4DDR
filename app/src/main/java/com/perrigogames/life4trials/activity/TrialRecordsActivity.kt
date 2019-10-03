package com.perrigogames.life4trials.activity

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.db.TrialSessionDB
import com.perrigogames.life4trials.ui.trialrecords.TrialRecordsFragment

class TrialRecordsActivity : AppCompatActivity(), TrialRecordsFragment.OnRecordsListInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(FrameLayout(this).also {
            it.id = R.id.container_fragment
        })
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_fragment, TrialRecordsFragment.newInstance())
                .commitNow()
        }
    }

    override fun onRecordsListInteraction(item: TrialSessionDB) {
    }
}
