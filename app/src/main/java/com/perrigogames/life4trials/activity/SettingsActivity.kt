package com.perrigogames.life4trials.activity

import android.content.Intent
import android.content.Intent.ACTION_SENDTO
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.TrialRank
import com.perrigogames.life4trials.event.SavedRankUpdatedEvent
import com.perrigogames.life4trials.event.TrialListUpdatedEvent
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.util.NotificationUtil
import com.perrigogames.life4trials.util.SharedPrefsUtils



class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }

    class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            val context = preferenceManager.context
            val screen = preferenceManager.createPreferenceScreen(context)

            PreferenceCategory(context).apply {
                key = "user_info"
                title = "User Info"
                screen.addPreference(this)
                addPreference(EditTextPreference(context).apply {
                    key = KEY_INFO_RIVAL_CODE
                    title = context.getString(R.string.rival_code)
                    summary = SharedPrefsUtils.getUserString(context, KEY_INFO_RIVAL_CODE)
                })
                addPreference(EditTextPreference(context).apply {
                    key = KEY_INFO_TWITTER_NAME
                    title = context.getString(R.string.twitter_name)
                    summary = SharedPrefsUtils.getUserString(context, KEY_INFO_TWITTER_NAME)
                })
                addPreference(SwitchPreference(context).apply {
                    key = KEY_SUBMISSION_NOTIFICAION
                    title = "Show info notifications during submission"
                })
                addPreference(Preference(context).apply {
                    key = KEY_SUBMISSION_NOTIFICAION_TEST
                    title = "Test notifications"
                    onPreferenceClickListener = Preference.OnPreferenceClickListener {
                        NotificationUtil.showUserInfoNotifications(context, 1579)
                        true
                    }
                })
            }
            PreferenceCategory(context).apply {
                key = "trial_list"
                title = "Trial List"
                screen.addPreference(this)
                addPreference(SwitchPreference(context).apply {
                    key = KEY_LIST_TINT_COMPLETED
                    title = "Tint fully completed Trials"
                    onPreferenceClickListener = Preference.OnPreferenceClickListener {
                        Life4Application.eventBus.post(TrialListUpdatedEvent())
                        true
                    }
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
                    onPreferenceClickListener = Preference.OnPreferenceClickListener {
                        startActivity(Intent(ACTION_SENDTO, Uri.parse(
                            "mailto:life4@perrigogames.com?subject=${Uri.encode(getString(R.string.life4_feedback_email_subject))}")))
                        true
                    }
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

                    val ranksList = TrialRank.values().map { it.toString() }.toMutableList()
                    ranksList.add(0, "NONE")
                    val ranksArray = ranksList.toTypedArray()
                    context.life4app.trialManager.trials.forEach { trial ->
                        addPreference(DropDownPreference(context).apply {
                            key = "$KEY_DEBUG_RANK_PREFIX${trial.id}"
                            title = trial.name
                            summary = SharedPrefsUtils.getRankForTrial(context, trial)?.toString() ?: "NONE"
                            entries = ranksArray
                            entryValues = ranksArray
                        })
                    }
                }
            }
            preferenceScreen = screen
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            if (key != null) {
                when {
                    key == KEY_INFO_RIVAL_CODE ||
                    key == KEY_INFO_TWITTER_NAME -> findPreference<EditTextPreference>(key)?.let { it.summary = it.text }
                    key.startsWith(KEY_DEBUG_RANK_PREFIX) -> findPreference<DropDownPreference>(key)?.let {
                        val rank = TrialRank.parse(it.entry.toString())
                        SharedPrefsUtils.setRankForTrial(preferenceManager.context, it.key.substring(KEY_DEBUG_RANK_PREFIX.length), rank)
                        Life4Application.eventBus.post(SavedRankUpdatedEvent())
                        it.summary = rank?.toString() ?: "NONE"
                    }
                }
            }
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }
    }

    companion object {
        const val KEY_LIST_TINT_COMPLETED = "KEY_LIST_TINT_COMPLETED"
        const val KEY_DETAILS_PHOTO_SELECT = "KEY_DETAILS_PHOTO_SELECT"
        const val KEY_INFO_RIVAL_CODE = "KEY_INFO_RIVAL_CODE"
        const val KEY_INFO_TWITTER_NAME = "KEY_INFO_TWITTER_NAME"
        const val KEY_SUBMISSION_NOTIFICAION = "KEY_SUBMISSION_NOTIFICAION"
        const val KEY_SUBMISSION_NOTIFICAION_TEST = "KEY_SUBMISSION_NOTIFICAION_TEST"

        const val KEY_DEBUG_DETAILS_DISPLAY_ALL_RANKS = "dddar"
        const val KEY_DEBUG_BYPASS_STAT_ENTRY = "dbse"
        const val KEY_DEBUG_BYPASS_CAMERA = "dbc"

        private const val KEY_DEBUG_RANK_PREFIX = "KEY_DEBUG_RANK_PREFIX"
    }
}