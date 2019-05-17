package com.perrigogames.life4trials.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.TrialRank

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
                key = "user_info"
                title = "User Info"
                summary = "Shown in notifications when submitting your results."
                screen.addPreference(this)
                addPreference(EditTextPreference(context).apply {
                    key = KEY_INFO_RIVAL_CODE
                    title = "Rival Code"
                })
                addPreference(EditTextPreference(context).apply {
                    key = KEY_INFO_TWITTER_NAME
                    title = "Twitter Name"
                })
            }
            PreferenceCategory(context).apply {
                key = "trial_details"
                title = "Trial Details"
                screen.addPreference(this)
                addPreference(SwitchPreference(context).apply {
                    key = KEY_DETAILS_PHOTO_SELECT
                    title = "Use photo picker"
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
                PreferenceCategory(context).apply {
                    key = "debug_flags_category"
                    title = "Debug Flags*"
                    screen.addPreference(this)
                    addPreference(SwitchPreference(context).apply {
                        key = KEY_DEBUG_DETAILS_DISPLAY_ALL_RANKS
                        title = "Display all ranks"
                        summary = "Show all the ranks one after the other on the Details screen"
                    })
                    addPreference(SwitchPreference(context).apply {
                        key = KEY_DEBUG_BYPASS_CAMERA
                        title = "Bypass camera"
                        summary = "Use a generic image instead of launching the Camera"
                    })
                    addPreference(SwitchPreference(context).apply {
                        key = KEY_DEBUG_BYPASS_STAT_ENTRY
                        title = "Bypass stats entry"
                        summary = "Use random score values when entering a new photo"
                    })
                }
                PreferenceCategory(context).apply {
                    key = "debug_ranks_category"
                    title = "Debug Ranks*"
                    screen.addPreference(this)
                    (1..4).forEach { idx ->
                        addPreference(DropDownPreference(context).apply {
                            key = "rank_$idx"
                            title = "Rank $idx"
                            entryValues = TrialRank.values().map { it.toString() }.toTypedArray()
                        })
                    }
                }
            }

            preferenceScreen = screen
        }
    }

    companion object {
        const val KEY_DETAILS_PHOTO_SELECT = "KEY_DETAILS_PHOTO_SELECT"
        const val KEY_INFO_RIVAL_CODE = "KEY_INFO_RIVAL_CODE"
        const val KEY_INFO_TWITTER_NAME = "KEY_INFO_TWITTER_NAME"

        const val KEY_DEBUG_DETAILS_DISPLAY_ALL_RANKS = "dddar"
        const val KEY_DEBUG_BYPASS_STAT_ENTRY = "dbse"
        const val KEY_DEBUG_BYPASS_CAMERA = "dbc"
    }
}