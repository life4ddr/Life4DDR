package com.perrigogames.life4.android.activity.trial

import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4.db.TrialSession
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.ui.trial.TrialRecordsFragment

class TrialRecordsActivity : AppCompatActivity(), TrialRecordsFragment.OnRecordsListInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(FrameLayout(this).also {
            it.id = R.id.container_compose
        })
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_compose, TrialRecordsFragment.newInstance())
                .commitNow()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onRecordsListInteraction(item: TrialSession) {
    }
}
