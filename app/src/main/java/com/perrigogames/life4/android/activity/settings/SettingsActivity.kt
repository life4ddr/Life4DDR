package com.perrigogames.life4.android.activity.settings

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SENDTO
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.*
import com.perrigogames.life4.*
import com.perrigogames.life4.SettingsKeys.KEY_CREDITS
import com.perrigogames.life4.SettingsKeys.KEY_DEBUG
import com.perrigogames.life4.SettingsKeys.KEY_DEBUG_INDUCE_CRASH
import com.perrigogames.life4.SettingsKeys.KEY_DEBUG_LEADERBOARD
import com.perrigogames.life4.SettingsKeys.KEY_DEBUG_NOTIF_LADDER_RANK
import com.perrigogames.life4.SettingsKeys.KEY_DEBUG_NOTIF_PLACEMENT
import com.perrigogames.life4.SettingsKeys.KEY_DEBUG_NOTIF_TRIAL_RANK
import com.perrigogames.life4.SettingsKeys.KEY_DEBUG_SONG_RECORDS
import com.perrigogames.life4.SettingsKeys.KEY_FEEDBACK
import com.perrigogames.life4.SettingsKeys.KEY_FIND_US_TWITTER
import com.perrigogames.life4.SettingsKeys.KEY_IMPORT_DATA
import com.perrigogames.life4.SettingsKeys.KEY_IMPORT_GAME_VERSION
import com.perrigogames.life4.SettingsKeys.KEY_IMPORT_VIEW_LIST
import com.perrigogames.life4.SettingsKeys.KEY_INFO_IMPORT
import com.perrigogames.life4.SettingsKeys.KEY_INFO_NAME
import com.perrigogames.life4.SettingsKeys.KEY_INFO_RANK
import com.perrigogames.life4.SettingsKeys.KEY_INFO_RIVAL_CODE
import com.perrigogames.life4.SettingsKeys.KEY_INFO_TWITTER_NAME
import com.perrigogames.life4.SettingsKeys.KEY_LADDER_CLEAR
import com.perrigogames.life4.SettingsKeys.KEY_LIST_HIGHLIGHT_NEW
import com.perrigogames.life4.SettingsKeys.KEY_LIST_SHOW_EX
import com.perrigogames.life4.SettingsKeys.KEY_LIST_SHOW_EX_REMAINING
import com.perrigogames.life4.SettingsKeys.KEY_LIST_TINT_COMPLETED
import com.perrigogames.life4.SettingsKeys.KEY_MANAGE_UNLOCKS
import com.perrigogames.life4.SettingsKeys.KEY_RECORDS_CLEAR
import com.perrigogames.life4.SettingsKeys.KEY_REFRESH_SONG_DB
import com.perrigogames.life4.SettingsKeys.KEY_SHOP_DANGERSHARK
import com.perrigogames.life4.SettingsKeys.KEY_SHOP_LIFE4
import com.perrigogames.life4.SettingsKeys.KEY_SONG_RESULTS_CLEAR
import com.perrigogames.life4.SettingsKeys.KEY_SUBMISSION_NOTIFICAION_TEST
import com.perrigogames.life4.android.BuildConfig
import com.perrigogames.life4.android.GetScoreList
import com.perrigogames.life4.android.GetTrialData
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.activity.base.BlockListCheckActivity
import com.perrigogames.life4.android.activity.base.SongRecordsListCheckActivity
import com.perrigogames.life4.android.manager.AndroidLadderDialogs
import com.perrigogames.life4.android.util.jacketResId
import com.perrigogames.life4.android.util.openWebUrlFromRes
import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.db.TrialDatabaseHelper
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.TrialRank
import com.perrigogames.life4.model.*
import com.russhwolf.settings.Settings
import org.greenrobot.eventbus.EventBus
import org.koin.core.KoinComponent
import org.koin.core.inject


class SettingsActivity : AppCompatActivity(),
    SettingsFragmentListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings,
                SettingsFragment()
            )
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

    override fun onFragmentEntered(title: String) {
        supportActionBar?.title = title
    }

    abstract class BaseSettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener, KoinComponent {

        protected val ladderManager: LadderManager by inject()
        protected val ladderDialogs: AndroidLadderDialogs by inject()
        protected val trialManager: TrialManager by inject()
        protected val trialDb: TrialDatabaseHelper by inject()
        protected val trialSessionManager: TrialSessionManager by inject()
        protected val playerManager: PlayerManager by inject()
        protected val ignoreListManager: IgnoreListManager by inject()
        protected val settings: Settings by inject()
        protected val notifications: Notifications by inject()
        protected val eventBus: EventBus by inject()

        private var listener: SettingsFragmentListener? = null

        protected val getScores = registerForActivityResult(GetScoreList()) { ladderDialogs.handleSkillAttackImport(requireActivity(), it) }
        protected val getTrials = registerForActivityResult(GetTrialData()) { trialDb.importRecordExportStrings(it) }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
            listener!!.onFragmentEntered(fragmentName() ?: requireContext().getString(R.string.action_settings))
        }

        override fun onAttach(context: Context) {
            super.onAttach(context)
            try {
                listener = context as SettingsFragmentListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement OnFragmentInteractionListener")
            }
        }

        override fun onDetach() {
            super.onDetach()
            listener = null
        }

        abstract fun fragmentName(): String?

        protected fun preference(key: String) = preferenceScreen.findPreference<Preference>(key)!!

        protected inline fun preferenceListener(key: String, crossinline action: (Preference) -> Boolean) {
            preference(key).onPreferenceClickListener = Preference.OnPreferenceClickListener { action(it) }
        }

        protected inline fun preference(target: PreferenceGroup = preferenceScreen, block: Preference.() -> Unit) =
            target.addPreference(Preference(context).apply(block))

        protected inline fun switch(target: PreferenceGroup = preferenceScreen, block: SwitchPreference.() -> Unit) =
            target.addPreference(SwitchPreference(context).apply(block))

        protected inline fun checkBox(target: PreferenceGroup = preferenceScreen, block: CheckBoxPreference.() -> Unit) =
            target.addPreference(CheckBoxPreference(context).apply(block))

        protected inline fun category(
            key: String,
            title: String,
            target: PreferenceGroup = preferenceScreen,
            block: PreferenceCategory.() -> Unit
        ) = PreferenceCategory(context).also {
            it.key = key
            it.title = title
            target.addPreference(it)
            it.apply(block)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) = Unit
    }

    class SettingsFragment : BaseSettingsFragment() {
        override fun fragmentName() = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            (preference(KEY_IMPORT_GAME_VERSION) as DropDownPreference).apply {
                summary = ignoreListManager.getIgnoreList(value).name
                entries = ignoreListManager.ignoreListTitles.toTypedArray()
                entryValues = ignoreListManager.ignoreListIds.toTypedArray()
            }
            preferenceListener(KEY_MANAGE_UNLOCKS) {
                startActivity(Intent(context, SongUnlockActivity::class.java))
                true
            }
            preferenceListener(KEY_IMPORT_VIEW_LIST) {
                startActivity(Intent(context, BlockListCheckActivity::class.java))
                true
            }
            preferenceListener(KEY_IMPORT_DATA) {
                try {
                    getScores.launch(Unit)
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        R.string.no_ddra_manager,
                        Toast.LENGTH_LONG
                    ).show()
                }
                true
            }

            preferenceListener(KEY_SHOP_LIFE4) {
                (activity as SettingsActivity).openWebUrlFromRes(R.string.url_shop_life4)
                true
            }
            preferenceListener(KEY_SHOP_DANGERSHARK) {
                (activity as SettingsActivity).openWebUrlFromRes(R.string.url_shop_dangershark)
                true
            }
            preferenceListener(KEY_FEEDBACK) {
                (context as AppCompatActivity).startActivity(Intent(ACTION_SENDTO, Uri.parse(
                    "mailto:life4@perrigogames.com?subject=${Uri.encode(getString(R.string.life4_feedback_email_subject))}")))
                true
            }
            preferenceListener(KEY_FIND_US_TWITTER) {
                (activity as SettingsActivity).openWebUrlFromRes(R.string.url_twitter)
                true
            }
            preferenceListener(KEY_CREDITS) {
                (context as AppCompatActivity).startActivity(Intent(context, CreditsActivity::class.java))
                true
            }

            preference(KEY_DEBUG).isVisible = BuildConfig.DEBUG

            preference {
                key = "version_info"
                title = "Version ${BuildConfig.VERSION_NAME}${
                    if (BuildConfig.DEBUG) " (${BuildConfig.VERSION_CODE})"
                    else ""}"
                onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    VersionsDialog().show(requireActivity().supportFragmentManager, VersionsDialog.TAG)
                    true
                }
            }
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            if (key != null) {
                when (key) {
                    KEY_IMPORT_GAME_VERSION -> {
                        ignoreListManager.invalidateIgnoredIds()
                        findPreference<DropDownPreference>(key)?.let {
                            it.summary = ignoreListManager.getIgnoreList(it.value).name
                        }
                        eventBus.post(LadderRanksReplacedEvent())
                    }
                    KEY_INFO_IMPORT -> findPreference<EditTextPreference>(key)?.let {
                        it.text?.let { text -> playerManager.importPlayerInfo(text) }
                        it.text = null
                    }
                }
            }
        }
    }

    class UserInfoSettingsFragment : BaseSettingsFragment() {
        override fun fragmentName() = requireContext().getString(R.string.edit_user_info)

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.user_info_preferences, rootKey)

            (preference(KEY_INFO_RANK) as DropDownPreference).apply {
                summary = LadderRank.parse(value?.toLongOrNull())?.toString() ?: getString(R.string.none)
                entries = LadderRank.values().map { it.toString() }.toMutableList().apply {
                    add(0, "NONE")
                }.toTypedArray()
                entryValues = LadderRank.values().map { it.stableId.toString() }.toMutableList().apply {
                    add(0, "")
                }.toTypedArray()
            }

            preference(KEY_INFO_NAME).summary = settings.getStringOrNull(KEY_INFO_NAME)
            preference(KEY_INFO_RIVAL_CODE).summary = settings.getStringOrNull(KEY_INFO_RIVAL_CODE)
            preference(KEY_INFO_TWITTER_NAME).summary = settings.getStringOrNull(KEY_INFO_TWITTER_NAME)

            preferenceListener(KEY_SUBMISSION_NOTIFICAION_TEST) {
                notifications.showUserInfoNotifications(1579)
                true
            }
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            if (key != null) {
                when (key) {
                    KEY_INFO_NAME, KEY_INFO_RIVAL_CODE, KEY_INFO_TWITTER_NAME -> {
                        findPreference<EditTextPreference>(key)?.let { it.summary = it.text }
                        eventBus.post(LocalUserInfoUpdatedEvent())
                    }
                    KEY_INFO_RANK -> {
                        findPreference<DropDownPreference>(key)?.let {
                            LadderRank.parse(it.value.toLongOrNull()).let { rank ->
                                it.summary = rank?.toString() ?: getString(R.string.none)
                                ladderManager.setUserRank(rank)
                            }
                        }
                    }
                }
            }
        }
    }

    class TrialSettingsFragment : BaseSettingsFragment() {
        override fun fragmentName() = requireContext().getString(R.string.trial_settings)

        private val listUpdateListener: (Preference) -> Boolean = {
            eventBus.post(TrialListUpdatedEvent())
            true
        }
        private val listReplaceListener: (Preference) -> Boolean = {
            eventBus.post(TrialListReplacedEvent())
            true
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.trial_preferences, rootKey)

            preferenceListener(KEY_LIST_SHOW_EX, listUpdateListener)
            preferenceListener(KEY_LIST_SHOW_EX_REMAINING, listUpdateListener)
            preferenceListener(KEY_LIST_TINT_COMPLETED, listUpdateListener)
            preferenceListener(KEY_LIST_HIGHLIGHT_NEW, listReplaceListener)

//            preferenceListener(KEY_INFO_IMPORT_TRIALS) {
//                try {
//                    getTrials.launch(Unit)
//                } catch (e: Exception) {
//                    Toast.makeText(requireActivity(), R.string.import_trials_toast_failure, Toast.LENGTH_SHORT).show()
//                }
//                true
//            }
        }
    }

    class ClearDataFragment : BaseSettingsFragment() {
        override fun fragmentName() = requireContext().getString(R.string.clear_data)

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.clear_data_preferences, rootKey)
            preferenceListener(KEY_LADDER_CLEAR) {
                ladderDialogs.withActivity(requireActivity()) {
                    ladderManager.clearGoalStates()
                }
                true
            }
            preferenceListener(KEY_RECORDS_CLEAR) {
                AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.are_you_sure)
                    .setMessage(R.string.confirm_erase_trial_data)
                    .setPositiveButton(R.string.yes) { _, _ -> trialManager.clearSessions() }
                    .setNegativeButton(R.string.no, null)
                    .show() //FIXME extract
                true
            }
            preferenceListener(KEY_SONG_RESULTS_CLEAR) {
                ladderDialogs.withActivity(requireActivity()) {
                    ladderManager.clearSongResults()
                }
                true
            }
            preferenceListener(KEY_REFRESH_SONG_DB) {
                ladderDialogs.withActivity(requireActivity()) {
                    ladderManager.refreshSongDatabase()
                }
                true
            }
        }
    }

    class DebugSettingsFragment : BaseSettingsFragment() {
        override fun fragmentName() = "Debug Settings"

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.debug_preferences, rootKey)

            val context = preferenceManager.context

            preferenceListener(KEY_DEBUG_INDUCE_CRASH) { throw IllegalAccessException() }
            preferenceListener(KEY_DEBUG_NOTIF_PLACEMENT) {
                notifications.showPlacementNotification(LadderRank.values().random())
                true
            }
            preferenceListener(KEY_DEBUG_NOTIF_LADDER_RANK) {
                notifications.showLadderRankChangedNotification(LadderRank.values().random())
                true
            }
            preferenceListener(KEY_DEBUG_NOTIF_TRIAL_RANK) {
                notifications.showTrialRankChangedNotification(trialManager.trials.random(), TrialRank.values().random())
                true
            }
            preferenceListener(KEY_DEBUG_LEADERBOARD) {
//                (context as AppCompatActivity).startActivity(Intent(context, LadderLeaderboardActivity::class.java))
                true
            }
            preferenceListener(KEY_DEBUG_SONG_RECORDS) {
                (context as AppCompatActivity).startActivity(Intent(context, SongRecordsListCheckActivity::class.java))
                true
            }
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) = Unit
    }

    class DebugTrialRanksFragment : BaseSettingsFragment() {
        override fun fragmentName() = "Trial Ranks"

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.empty_preferences, rootKey)
            val ranksList = TrialRank.values().map { it.toString() }.toMutableList()
            ranksList.add(0, "NONE")
            val ranksArray = ranksList.toTypedArray()
            trialManager.trials.filter { it.goals != null && it.goals!!.isNotEmpty() }.forEach { trial ->
                preferenceScreen.addPreference(DropDownPreference(context).apply {
                    key = "$KEY_DEBUG_RANK_PREFIX${trial.id}"
                    title = trial.name
                    icon = ContextCompat.getDrawable(context, trial.jacketResId(requireContext()))
                    summary = trialManager.getRankForTrial(trial.id)?.toString() ?: "NONE"
                    entries = ranksArray
                    entryValues = ranksArray
                })
            }
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            if (key != null) {
                when {
                    key.startsWith(KEY_DEBUG_RANK_PREFIX) -> findPreference<DropDownPreference>(key)?.let { it ->
                        val rank = TrialRank.parse(it.entry.toString())
                        val trial = trialManager.findTrial(it.key.substring(KEY_DEBUG_RANK_PREFIX.length))!!
                        val session = InProgressTrialSession(trial, if (trial.isEvent) null else rank)
                            .apply { goalObtained = (rank != null) }
                        trialSessionManager.saveSession(session)
                        it.summary = rank?.toString() ?: "NONE"
                    }
                }
            }
        }
    }

    companion object {
        private const val KEY_DEBUG_RANK_PREFIX = "KEY_DEBUG_RANK_PREFIX"
    }
}

interface SettingsFragmentListener {
    fun onFragmentEntered(title: String)
}
