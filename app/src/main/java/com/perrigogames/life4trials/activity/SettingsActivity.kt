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
import com.perrigogames.life4trials.data.LadderRank
import com.perrigogames.life4trials.data.TrialRank
import com.perrigogames.life4trials.data.TrialSession
import com.perrigogames.life4trials.event.LadderRanksReplacedEvent
import com.perrigogames.life4trials.event.LocalUserInfoUpdatedEvent
import com.perrigogames.life4trials.event.TrialListReplacedEvent
import com.perrigogames.life4trials.event.TrialListUpdatedEvent
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.util.NotificationUtil
import com.perrigogames.life4trials.util.SharedPrefsUtil
import com.perrigogames.life4trials.util.openWebUrlFromRes

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

        private val ladderManager get() = context!!.life4app.ladderManager
        private val trialManager get() = context!!.life4app.trialManager
        private val playerManager get() = context!!.life4app.playerManager
        private val songDataManager get() = context!!.life4app.songDataManager

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
            preference(KEY_INFO_NAME).summary =
                SharedPrefsUtil.getUserString(context, KEY_INFO_NAME)
            (preference(KEY_INFO_RANK) as DropDownPreference).apply {
                summary = LadderRank.parse(value?.toLongOrNull())?.toString() ?: getString(R.string.none)
                entries = LadderRank.values().map { it.toString() }.toMutableList().apply {
                    add(0, "NONE")
                }.toTypedArray()
                entryValues = LadderRank.values().map { it.stableId.toString() }.toMutableList().apply {
                    add(0, "")
                }.toTypedArray()
            }
            (preference(KEY_IMPORT_GAME_VERSION) as DropDownPreference).apply {
                summary = songDataManager.getIgnoreList(value).name
                entries = songDataManager.ignoreListTitles.toTypedArray()
                entryValues = songDataManager.ignoreListIds.toTypedArray()
            }
            preferenceListener(KEY_IMPORT_VIEW_LIST) {
                startActivity(Intent(context, BlockListCheckActivity::class.java))
                true
            }
            preference(KEY_INFO_RIVAL_CODE).summary =
                SharedPrefsUtil.getUserString(context, KEY_INFO_RIVAL_CODE)
            preference(KEY_INFO_TWITTER_NAME).summary =
                SharedPrefsUtil.getUserString(context, KEY_INFO_TWITTER_NAME)
            preferenceListener(KEY_SUBMISSION_NOTIFICAION_TEST) {
                NotificationUtil.showUserInfoNotifications(context, 1579)
                true
            }
            preferenceListener(KEY_IMPORT_DATA) {
                ladderManager.showImportFlow(activity!!)
                true
            }

            preferenceListener(KEY_LIST_SHOW_EX, listUpdateListener)
            preferenceListener(KEY_LIST_SHOW_EX_REMAINING, listUpdateListener)
            preferenceListener(KEY_LIST_TINT_COMPLETED, listUpdateListener)
            preferenceListener(KEY_LIST_HIGHLIGHT_NEW, listReplaceListener)

            preferenceListener(KEY_LADDER_CLEAR) {
                ladderManager.clearGoalStates(context)
                true
            }
            preferenceListener(KEY_RECORDS_CLEAR) {
                trialManager.clearRecords(context)
                true
            }
            preferenceListener(KEY_SONG_RESULTS_CLEAR) {
                ladderManager.clearSongResults(context)
                true
            }
            preferenceListener(KEY_REFRESH_SONG_DB) {
                ladderManager.refreshSongDatabase(context)
                true
            }

            preferenceListener(KEY_SHOP) {
                (activity as SettingsActivity).openWebUrlFromRes(R.string.url_shop)
                true
            }
            preferenceListener(KEY_FEEDBACK) {
                (context as AppCompatActivity).startActivity(Intent(ACTION_SENDTO, Uri.parse(
                    "mailto:life4@perrigogames.com?subject=${Uri.encode(getString(R.string.life4_feedback_email_subject))}")))
                true
            }
            preferenceListener(KEY_CREDITS) {
                (context as AppCompatActivity).startActivity(Intent(context, CreditsActivity::class.java))
                true
            }

            if (BuildConfig.DEBUG) {
                addDebugSettings()
            }

            preference {
                key = "version_info"
                title = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            }
        }

        private fun addDebugSettings() {
            category("debug_flags_category", "Debug Settings*") {
                checkBox(this) {
                    key = KEY_DEBUG_DETAILS_DISPLAY_ALL_RANKS
                    title = "Display all ranks"
                    summary = "Show all the ranks one after the other on the Details screen"
                }
                checkBox(this) {
                    key = KEY_DEBUG_BYPASS_CAMERA
                    title = "Bypass camera"
                    summary = "Use a generic image instead of launching the Camera"
                }
                checkBox(this) {
                    key = KEY_DEBUG_ACCEPT_INVALID
                    title = "Accept invalid song data"
                    summary = "Allow missing fields when entering scores and steps"
                }
                checkBox(this) {
                    key = KEY_DEBUG_BYPASS_STAT_ENTRY
                    title = "Bypass stats entry"
                    summary = "Use random score values when entering a new photo"
                }
                preference(this) {
                    key = "induce_crash"
                    title = "Induce crash"
                    onPreferenceClickListener = Preference.OnPreferenceClickListener {
                        throw IllegalAccessException()
                    }
                }
            }
            category("debug_notifications_category", "Debug Notifications*") {
                preference(this) {
                    key = "debug_placement"
                    title = "Placement results"
                    onPreferenceClickListener = Preference.OnPreferenceClickListener {
                        NotificationUtil.showPlacementNotification(context, LadderRank.values().random())
                        true
                    }
                }
                preference(this) {
                    key = "debug_ladder"
                    title = "Ladder rank up"
                    onPreferenceClickListener = Preference.OnPreferenceClickListener {
                        NotificationUtil.showLadderRankChangedNotification(context, LadderRank.values().random())
                        true
                    }
                }
                preference(this) {
                    key = "debug_trial"
                    title = "Trial rank up"
                    onPreferenceClickListener = Preference.OnPreferenceClickListener {
                        NotificationUtil.showTrialRankChangedNotification(context, trialManager.trials.random(), TrialRank.values().random())
                        true
                    }
                }
            }
            Preference(context).apply {
                key = KEY_DEBUG_LEADERBOARD
                title = "Leaderboard Test"
                onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    (context as AppCompatActivity).startActivity(Intent(context, LadderLeaderboardActivity::class.java))
                    true
                }
                preferenceScreen.addPreference(this)
            }
            Preference(context).apply {
                key = KEY_DEBUG_DATA_DUMP
                title = "Dump Song Data"
                onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    songDataManager.dumpData()
                    true
                }
                preferenceScreen.addPreference(this)
            }
            Preference(context).apply {
                key = KEY_DEBUG_SONG_RECORDS
                title = "Song Records"
                onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    (context as AppCompatActivity).startActivity(Intent(context, SongRecordsListCheckActivity::class.java))
                    true
                }
                preferenceScreen.addPreference(this)
            }
            Preference(context).apply {
                key = "induce_crash"
                title = "Induce crash"
                onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    throw IllegalAccessException()
                }
            }
            category("debug_ranks_category", "Debug Ranks*") {
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
                    key == KEY_INFO_NAME ||
                    key == KEY_INFO_RIVAL_CODE ||
                    key == KEY_INFO_TWITTER_NAME ->  {
                        findPreference<EditTextPreference>(key)?.let { it.summary = it.text }
                        Life4Application.eventBus.post(LocalUserInfoUpdatedEvent())
                    }
                    key == KEY_INFO_RANK -> {
                        findPreference<DropDownPreference>(key)?.let {
                            LadderRank.parse(it.value.toLongOrNull()).let { rank ->
                                it.summary = rank?.toString() ?: getString(R.string.none)
                                ladderManager.setUserRank(rank)
                            }
                        }
                    }
                    key == KEY_IMPORT_GAME_VERSION -> {
                        songDataManager.invalidateIgnoredIds()
                        findPreference<DropDownPreference>(key)?.let {
                            it.summary = songDataManager.getIgnoreList(it.value).name
                        }
                        Life4Application.eventBus.post(LadderRanksReplacedEvent())
                    }
                    key == KEY_INFO_IMPORT -> findPreference<EditTextPreference>(key)?.let {
                        it.text?.let { text -> playerManager.importPlayerInfo(text) }
                        it.text = null
                    }
                    key.startsWith(KEY_DEBUG_RANK_PREFIX) -> findPreference<DropDownPreference>(key)?.let { it ->
                        val rank = TrialRank.parse(it.entry.toString())
                        val trial = trialManager.findTrial(it.key.substring(KEY_DEBUG_RANK_PREFIX.length))!!
                        val session = TrialSession(trial, if (trial.isEvent) null else rank).apply { goalObtained = (rank != null) }
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

        private inline fun preference(target: PreferenceGroup = preferenceScreen, block: Preference.() -> Unit) =
            target.addPreference(Preference(context).apply(block))

        private inline fun switch(target: PreferenceGroup = preferenceScreen, block: SwitchPreference.() -> Unit) =
            target.addPreference(SwitchPreference(context).apply(block))

        private inline fun checkBox(target: PreferenceGroup = preferenceScreen, block: CheckBoxPreference.() -> Unit) =
            target.addPreference(CheckBoxPreference(context).apply(block))

        private inline fun category(key: String,
                                    title: String,
                                    target: PreferenceGroup = preferenceScreen,
                                    block: PreferenceCategory.() -> Unit) =
            PreferenceCategory(context).also {
                it.key = key
                it.title = title
                target.addPreference(it)
                it.apply(block)
            }
    }

    companion object {
        const val KEY_LIST_TINT_COMPLETED = "KEY_LIST_TINT_COMPLETED"
        const val KEY_LIST_SHOW_EX = "KEY_LIST_SHOW_EX"
        const val KEY_LIST_SHOW_EX_REMAINING = "KEY_LIST_SHOW_EX_REMAINING"
        const val KEY_LIST_HIGHLIGHT_NEW = "KEY_LIST_HIGHLIGHT_NEW"
        const val KEY_DETAILS_PHOTO_SELECT = "KEY_DETAILS_PHOTO_SELECT"
        const val KEY_DETAILS_EXPERT = "KEY_DETAILS_EXPERT"
        const val KEY_DETAILS_ENFORCE_EXPERT = "KEY_DETAILS_ENFORCE_EXPERT"
        const val KEY_INFO_NAME = "KEY_INFO_NAME"
        const val KEY_INFO_RANK = "KEY_INFO_RANK"
        const val KEY_INFO_RIVAL_CODE = "KEY_INFO_RIVAL_CODE"
        const val KEY_INFO_TWITTER_NAME = "KEY_INFO_TWITTER_NAME"
        const val KEY_INFO_IMPORT = "KEY_INFO_IMPORT"
        const val KEY_SUBMISSION_NOTIFICAION = "KEY_SUBMISSION_NOTIFICAION"
        const val KEY_SUBMISSION_NOTIFICAION_TEST = "KEY_SUBMISSION_NOTIFICAION_TEST"
        const val KEY_RECORDS_REMAINING_EX = "KEY_RECORDS_REMAINING_EX"
        const val KEY_SHOP = "KEY_SHOP"
        const val KEY_IMPORT_GAME_VERSION = "KEY_IMPORT_GAME_VERSION"
        const val KEY_FEEDBACK = "KEY_FEEDBACK"
        const val KEY_CREDITS = "KEY_CREDITS"
        const val KEY_CATEGORY_IMPORT = "KEY_CATEGORY_IMPORT"
        const val KEY_IMPORT_DATA = "KEY_IMPORT_DATA"
        const val KEY_IMPORT_SKIP_DIRECTIONS = "KEY_IMPORT_SKIP_DIRECTIONS"
        const val KEY_IMPORT_VIEW_LIST = "KEY_IMPORT_VIEW_LIST"
        const val KEY_LADDER_CLEAR = "KEY_LADDER_CLEAR"
        const val KEY_SONG_RESULTS_CLEAR = "KEY_SONG_RESULTS_CLEAR"
        const val KEY_REFRESH_SONG_DB = "KEY_REFRESH_SONG_DB"
        const val KEY_RECORDS_CLEAR = "KEY_RECORDS_CLEAR"

        const val KEY_DEBUG_DETAILS_DISPLAY_ALL_RANKS = "dddar"
        const val KEY_DEBUG_BYPASS_STAT_ENTRY = "dbse"
        const val KEY_DEBUG_ACCEPT_INVALID = "dbai"
        const val KEY_DEBUG_BYPASS_CAMERA = "dbc"
        const val KEY_DEBUG_LEADERBOARD = "dlb"
        const val KEY_DEBUG_DATA_DUMP = "dimo"
        const val KEY_DEBUG_SONG_RECORDS = "dsr"

        private const val KEY_DEBUG_RANK_PREFIX = "KEY_DEBUG_RANK_PREFIX"
    }
}