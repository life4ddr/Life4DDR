package com.perrigogames.life4trials.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            val context = preferenceManager.context
            val screen = preferenceManager.createPreferenceScreen(context)

            PreferenceCategory(context).apply {
                key = "notifications_category"
                title = "Notifications"
                screen.addPreference(this)
                addPreference(SwitchPreference(context).apply {
                    key = "notifications"
                    title = "Enable message notifications"
                })
            }

            PreferenceCategory(context).apply {
                key = "help"
                title = "Help"
                screen.addPreference(this)
                addPreference(Preference(context).apply {
                    key = "feedback"
                    title = "Send feedback"
                    summary = "Report technical issues or suggest new features"
                })
            }

            if (BuildConfig.DEBUG) {
                screen.addPreference(PreferenceCategory(context).apply {
                    key = "debug_rank_category"
                    title = "(D) Ranks"
                })
            }

            preferenceScreen = screen
        }
    }
}