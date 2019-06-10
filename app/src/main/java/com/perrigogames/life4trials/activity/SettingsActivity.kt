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
import com.perrigogames.life4trials.data.TrialSession
import com.perrigogames.life4trials.event.TrialListReplacedEvent
import com.perrigogames.life4trials.event.TrialListUpdatedEvent
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.util.NotificationUtil
import com.perrigogames.life4trials.util.SharedPrefsUtil

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

        private val trialManager get() = context!!.life4app.trialManager

        private val listUpdateListener: (Preference) -> Boolean = {
            Life4Application.eventBus.post(TrialListUpdatedEvent())
            true
        }
        private val listReplaceListener: (Preference) -> Boolean = {
            Life4Application.eventBus.post(TrialListReplacedEvent())
            true
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val context = preferenceManager.context
            preference(KEY_INFO_RIVAL_CODE).summary =
                SharedPrefsUtil.getUserString(context, KEY_INFO_RIVAL_CODE)
            preference(KEY_INFO_TWITTER_NAME).summary =
                SharedPrefsUtil.getUserString(context, KEY_INFO_TWITTER_NAME)
            preferenceListener(KEY_SUBMISSION_NOTIFICAION) {
                NotificationUtil.showUserInfoNotifications(context, 1579)
                true
            }
            preferenceListener(KEY_RECORDS_CLEAR) {
                trialManager.clearRecords(context)
                true
            }

            preferenceListener(KEY_LIST_SHOW_EX, listUpdateListener)
            preferenceListener(KEY_LIST_SHOW_EX_REMAINING, listUpdateListener)
            preferenceListener(KEY_LIST_TINT_COMPLETED, listUpdateListener)
            preferenceListener(KEY_LIST_HIGHLIGHT_NEW, listReplaceListener)

            preferenceListener(KEY_FEEDBACK) {
                startActivity(Intent(ACTION_SENDTO, Uri.parse(
                    "mailto:life4@perrigogames.com?subject=${Uri.encode(getString(R.string.life4_feedback_email_subject))}")))
                true
            }

            if (BuildConfig.DEBUG) {
                addDebugRanks()
            }

            Preference(context).apply {
                key = "version_info"
                title = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                preferenceScreen.addPreference(this)
            }
        }

        private fun addDebugRanks() {
            PreferenceCategory(context).apply {
                key = "debug_flags_category"
                title = "Debug Flags*"
                preferenceScreen.addPreference(this)
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
                    key = KEY_DEBUG_ACCEPT_INVALID
                    title = "Accept invalid song data"
                    summary = "Allow missing fields when entering scores and steps"
                })
                addPreference(SwitchPreference(context).apply {
                    key = KEY_DEBUG_BYPASS_STAT_ENTRY
                    title = "Bypass stats entry"
                    summary = "Use random score values when entering a new photo"
                })
            }
            Preference(context).apply {
                key = "induce_crash"
                title = "Induce crash"
                onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    throw IllegalAccessException()
                }
                preferenceScreen.addPreference(this)
            }
            PreferenceCategory(context).apply {
                key = "debug_ranks_category"
                title = "Debug Ranks*"
                preferenceScreen.addPreference(this)

                val ranksList = TrialRank.values().map { it.toString() }.toMutableList()
                ranksList.add(0, "NONE")
                val ranksArray = ranksList.toTypedArray()
                context.life4app.trialManager.trials.filter { it.goals != null && it.goals.isNotEmpty() }.forEach { trial ->
                    addPreference(DropDownPreference(context).apply {
                        key = "$KEY_DEBUG_RANK_PREFIX${trial.id}"
                        title = trial.name
                        summary = trialManager.getRankForTrial(trial.id)?.toString() ?: "NONE"
                        entries = ranksArray
                        entryValues = ranksArray
                    })
                }
            }
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            if (key != null) {
                when {
                    key == KEY_INFO_RIVAL_CODE ||
                    key == KEY_INFO_TWITTER_NAME -> findPreference<EditTextPreference>(key)?.let { it.summary = it.text }
                    key.startsWith(KEY_DEBUG_RANK_PREFIX) -> findPreference<DropDownPreference>(key)?.let { it ->
                        val rank = TrialRank.parse(it.entry.toString())
                        val session = TrialSession(
                            trialManager.findTrial(it.key.substring(KEY_DEBUG_RANK_PREFIX.length))!!,
                            rank ?: TrialRank.SILVER).apply { goalObtained = (rank != null) }
                        trialManager.saveRecord(session)
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

        private fun preference(key: String) = preferenceScreen.findPreference<Preference>(key)!!

        private inline fun preferenceListener(key: String, crossinline action: (Preference) -> Boolean) {
            preference(key).onPreferenceClickListener = Preference.OnPreferenceClickListener { action(it) }
        }
    }

    companion object {
        const val KEY_LIST_TINT_COMPLETED = "KEY_LIST_TINT_COMPLETED"
        const val KEY_LIST_SHOW_EX = "KEY_LIST_SHOW_EX"
        const val KEY_LIST_SHOW_EX_REMAINING = "KEY_LIST_SHOW_EX_REMAINING"
        const val KEY_LIST_HIGHLIGHT_NEW = "KEY_LIST_HIGHLIGHT_NEW"
        const val KEY_DETAILS_PHOTO_SELECT = "KEY_DETAILS_PHOTO_SELECT"
        const val KEY_DETAILS_EXPERT = "KEY_DETAILS_EXPERT"
        const val KEY_INFO_RIVAL_CODE = "KEY_INFO_RIVAL_CODE"
        const val KEY_INFO_TWITTER_NAME = "KEY_INFO_TWITTER_NAME"
        const val KEY_SUBMISSION_NOTIFICAION = "KEY_SUBMISSION_NOTIFICAION"
        const val KEY_RECORDS_REMAINING_EX = "KEY_RECORDS_REMAINING_EX"
        const val KEY_RECORDS_CLEAR = "KEY_RECORDS_CLEAR"
        const val KEY_FEEDBACK = "KEY_FEEDBACK"

        const val KEY_DEBUG_DETAILS_DISPLAY_ALL_RANKS = "dddar"
        const val KEY_DEBUG_BYPASS_STAT_ENTRY = "dbse"
        const val KEY_DEBUG_ACCEPT_INVALID = "dbai"
        const val KEY_DEBUG_BYPASS_CAMERA = "dbc"

        private const val KEY_DEBUG_RANK_PREFIX = "KEY_DEBUG_RANK_PREFIX"
    }
}