package com.perrigogames.life4.android.activity.settings

import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.ui.unlocks.SongUnlockFragment

class SongUnlockActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = FrameLayout(this).also { it.id = R.id.layout_container }
        setContentView(container)

        supportFragmentManager.beginTransaction()
            .replace(container.id, SongUnlockFragment())
            .commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}