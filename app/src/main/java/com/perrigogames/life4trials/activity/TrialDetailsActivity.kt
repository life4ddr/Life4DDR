package com.perrigogames.life4trials.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_DETAILS_ENFORCE_EXPERT
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_DETAILS_PHOTO_SELECT
import com.perrigogames.life4trials.data.*
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.manager.TrialManager
import com.perrigogames.life4trials.ui.songlist.SongListFragment
import com.perrigogames.life4trials.util.SharedPrefsUtil
import com.perrigogames.life4trials.util.openWebUrlFromRes
import com.perrigogames.life4trials.view.SongView
import com.perrigogames.life4trials.view.TrialJacketView
import kotlinx.android.synthetic.main.content_trial_details.*


class TrialDetailsActivity: PhotoCaptureActivity(), SongListFragment.Listener {

    private val trialManager: TrialManager get() = life4app.trialManager
    private val trialId: String by lazy { intent.extras!!.getString(ARG_TRIAL_ID) }
    private val trial: Trial get() = trialManager.findTrial(trialId)!!

    private val storedRank: TrialRank? get() = life4app.trialManager.getRankForTrial(trial.id)
    private val initialRank: TrialRank by lazy { storedRank?.next
        ?: (intent.extras?.getInt(ARG_INITIAL_RANK)?.let { TrialRank.values()[it] } ?: TrialRank.SILVER)
    }

    override val snackbarContainer: ViewGroup get() = container

    private lateinit var trialSession: TrialSession
    private var currentIndex: Int? = null
    private var modified = false
        set(v) {
            field = v
            button_navigate_previous.visibility = if (v) GONE else VISIBLE
            button_navigate_next.visibility = if (v) GONE else VISIBLE
        }
    private var isNewEntry = false
    private var isFinal = false

    private lateinit var songListFragment: SongListFragment

    private var currentResult: SongResult?
        get() = currentIndex?.let { trialSession.results[it] }
        set(v) {
            currentIndex?.let { trialSession.results[it] = v }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_trial_details)
        trialSession = trialManager.startSession(trialId, initialRank)

        spinner_desired_rank.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, trialSession.availableRanks)
        spinner_desired_rank.setSelection(trialSession.availableRanks.indexOf(initialRank))
        spinner_desired_rank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setRank(trialSession.availableRanks[position])
            }
        }

        switch_acquire_mode.isChecked = SharedPrefsUtil.getUserFlag(this, KEY_DETAILS_PHOTO_SELECT, false)
        switch_acquire_mode.setOnCheckedChangeListener { _, isChecked ->
            SharedPrefsUtil.setUserFlag(this, KEY_DETAILS_PHOTO_SELECT, isChecked)
        }

        (view_trial_jacket as TrialJacketView).let { jacket ->
            jacket.trial = trial
            jacket.rank = storedRank
        }

        songListFragment = SongListFragment.newInstance(trial.id, tiled = false, useCurrentSession = true)
        supportFragmentManager.beginTransaction()
            .add(R.id.container_song_list_fragment, songListFragment)
            .commitNow()
    }

    override fun onResume() {
        super.onResume()
        try {
            scroll_details.scrollTo(0, 0)
        } catch (_: NullPointerException) {}
    }

    override fun onBackPressed() {
        if (modified) {
            AlertDialog.Builder(this).setTitle(R.string.are_you_sure)
                .setMessage(R.string.trial_close_confirmation)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.okay) { _, _ -> super.onBackPressed() }
                .show()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        if (v is SongView) {
            val idx = v.tag as Int
            currentIndex = idx
            val menuRes = if (trialSession.results[idx]?.photoUriString != null) R.menu.menu_song_replace else R.menu.menu_song_add
            menuInflater.inflate(menuRes, menu)
        }
    }

    fun navigationButtonClicked(v: View) {
        val prev = v.id == R.id.button_navigate_previous
        val targetTrial = if (prev) trialManager.previousTrial(trialId) else trialManager.nextTrial(trialId)
        if (targetTrial != null){
            startActivity(intent(this, targetTrial.id))
            finish()
        }
    }

    override fun onSongSelected(song: Song, position: Int) {
        currentIndex = position
        if (currentResult != null) {
            startEditActivity(currentIndex!!)
        } else {
            isNewEntry = true
            acquirePhoto()
        }
    }

    fun onLeaderboardClick(v: View) = openWebUrlFromRes(R.string.url_trial, trial.id)

    fun onConcedeClick(v: View) {
        AlertDialog.Builder(this).setTitle(R.string.are_you_sure)
            .setMessage(getString(R.string.trial_concede_confirmation_format, trial.name, trialSession.goalRank))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.okay) { _, _ -> concedeTrial() }
            .show()
    }

    fun onFinalizeClick(v: View) {
        if (!trialSession.shouldShowAdvancedSongDetails ||
            !SharedPrefsUtil.getUserFlag(this, KEY_DETAILS_ENFORCE_EXPERT, true) ||
            trialSession.results.none { it!!.misses == null || it.badJudges == null }) {

            isFinal = true
            acquirePhoto()
        } else {
            Snackbar.make(container, R.string.breakdown_information_missing, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setRank(rank: TrialRank) {
        trialSession.goalRank = rank

        image_desired_rank.rank = rank.parent
        text_goals_content.text = trialSession.trialGoalSet?.generateSingleGoalString(resources, trial)

        songListFragment.shouldShowAdvancedSongDetails = trialSession.shouldShowAdvancedSongDetails
    }

    private fun updateCompleteState() {
        val allSongsComplete = trialSession.results.filterNotNull().size == TrialData.TRIAL_LENGTH
        button_concede.visibility = if (allSongsComplete) GONE else VISIBLE
        button_finalize.visibility = if (allSongsComplete) VISIBLE else GONE
    }

    private fun concedeTrial() {
        life4app.trialManager.saveRecord(trialSession)
        finish()
    }

    override fun onNewPhotoCreated(uri: Uri) {
        if (currentIndex != null) {
            if (currentResult == null) {
                currentResult = SongResult(trial.songs[currentIndex!!], uri.toString())
            } else {
                currentResult!!.photoUri = uri
            }
        } else {
            trialSession.finalPhotoUriString = uri.toString()
        }
    }

    private fun startEditActivity(index: Int) {
        Intent(this, SongEntryActivity::class.java).also { i ->
            i.putExtra(SongEntryActivity.ARG_RESULT, trialSession.results[index])
            i.putExtra(SongEntryActivity.ARG_ADVANCED_DETAIL, trialSession.shouldShowAdvancedSongDetails)
            startActivityForResult(i, FLAG_SCORE_ENTER)
        }
    }

    private fun startSubmitActivity(uri: Uri) {
        trialSession.finalPhotoUri = uri
        Intent(this, TrialSubmissionActivity::class.java).also { i ->
            i.putExtra(TrialSubmissionActivity.ARG_SESSION, trialSession)
            startActivityForResult(i, FLAG_SCORE_ENTER)
        }
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            FLAG_SCORE_ENTER -> when (resultCode) {
                RESULT_OK -> onEntryFinished(data!!.getSerializableExtra(SongEntryActivity.RESULT_DATA) as? SongResult)
                SongEntryActivity.RESULT_RETAKE -> acquirePhoto()
                RESULT_CANCELED -> onEntryCancelled()
            }
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_gallery -> acquirePhoto(selection = true)
            R.id.action_camera -> acquirePhoto(selection = false)
            R.id.action_edit -> startEditActivity(currentIndex!!)
            else -> {
                currentIndex = null
                return false
            }
        }
        return true
    }

    private fun onEntryFinished(result: SongResult?) {
        currentResult = result
        songListFragment.setSongResult(currentIndex!!, result)
        updateCompleteState()
        modified = true
        currentIndex = null
        isNewEntry = false
        currentPhotoFile = null

        if (result?.passed == false) {
            AlertDialog.Builder(this).setTitle(R.string.trial_failed)
                .setMessage(R.string.trial_fail_confirmation)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.okay) { _, _ -> concedeTrial() }
                .show()
        }
    }

    override fun onPhotoTaken(uri: Uri) {
        if (isFinal) {
            startSubmitActivity(uri)
        } else {
            if (SharedPrefsUtil.getDebugFlag(this, SettingsActivity.KEY_DEBUG_BYPASS_STAT_ENTRY)) {
                currentResult!!.randomize()
                onEntryFinished(currentResult!!)
            } else {
                startEditActivity(currentIndex!!)
            }
        }
    }

    override fun onPhotoChosen(uri: Uri) {
        if (isFinal) {
            startSubmitActivity(uri)
        } else {
            if (currentResult == null) {
                currentResult = SongResult(trial.songs[currentIndex!!])
            }
            currentResult!!.photoUri = uri

            if (SharedPrefsUtil.getDebugFlag(this, SettingsActivity.KEY_DEBUG_BYPASS_STAT_ENTRY)) {
                currentResult!!.randomize()
                onEntryFinished(currentResult!!)
            } else {
                startEditActivity(currentIndex!!)
            }
        }
    }

    override fun onPhotoCancelled() = onEntryCancelled()

    private fun onEntryCancelled() {
        if (isNewEntry) {
            currentResult = null
        }
        currentIndex = null
        isNewEntry = false
    }

    companion object {
        const val ARG_TRIAL_ID = "ARG_TRIAL_ID"
        const val ARG_INITIAL_RANK = "ARG_INITIAL_RANK"

        const val FLAG_SCORE_ENTER = 7 // to enter the score screen

        fun intent(c: Context, trialId: String, initialRank: TrialRank? = null) =
            Intent(c, TrialDetailsActivity::class.java).apply {
                putExtra(ARG_TRIAL_ID, trialId)
                initialRank?.let { putExtra(ARG_INITIAL_RANK, it) }
            }
    }
}