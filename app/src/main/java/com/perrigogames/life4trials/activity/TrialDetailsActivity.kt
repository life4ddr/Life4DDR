package com.perrigogames.life4trials.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_DEBUG_BYPASS_STAT_ENTRY
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_DETAILS_ENFORCE_EXPERT
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_DETAILS_PHOTO_SELECT
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_DETAILS_UPDATE_GOAL
import com.perrigogames.life4trials.data.*
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.manager.LadderManager
import com.perrigogames.life4trials.manager.TrialManager
import com.perrigogames.life4trials.ui.songlist.SongListFragment
import com.perrigogames.life4trials.util.openWebUrlFromRes
import com.perrigogames.life4trials.util.toListString
import com.perrigogames.life4trials.util.visibilityBool
import com.perrigogames.life4trials.view.JacketCornerView
import com.perrigogames.life4trials.view.RunningEXScoreView
import com.perrigogames.life4trials.view.SongView
import com.perrigogames.life4trials.view.TrialJacketView
import kotlinx.android.synthetic.main.content_trial_details.*
import java.text.SimpleDateFormat
import java.util.*


class TrialDetailsActivity: PhotoCaptureActivity(), SongListFragment.Listener {

    private val ladderManager get() = life4app.ladderManager
    private val trialManager get() = life4app.trialManager
    private val settingsManager get() = life4app.settingsManager

    private val trialId: String by lazy { intent.extras!!.getString(ARG_TRIAL_ID) }
    private val trial: Trial get() = trialManager.findTrial(trialId)!!

    private val storedRank: TrialRank? get() = life4app.trialManager.getRankForTrial(trial.id)
    private val initialRank: TrialRank by lazy {
        if (trial.isEvent)
            TrialRank.fromLadderRank(ladderManager.getUserRank(), true) ?:
            TrialRank.WOOD
        else
            storedRank?.next ?:
            intent.extras?.getInt(ARG_INITIAL_RANK, -1)?.let { if (it >= 0) TrialRank.values()[it] else null } ?:
            TrialRank.fromLadderRank(ladderManager.getUserRank(), false) ?:
            TrialRank.WOOD
    }

    override val snackbarContainer: ViewGroup get() = container

    private lateinit var trialSession: TrialSession
    private var currentIndex: Int? = null
    private var modified = false
        set(v) {
            field = v
            button_navigate_previous.visibilityBool = !v
            button_navigate_next.visibilityBool = !v
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
        updateEXScoreMeter()

        (image_rank as TrialJacketView).let { jacket ->
            jacket.trial = trial
            jacket.rank = storedRank
            jacket.setCornerType(if (trial.isEvent) JacketCornerView.CornerType.EVENT else null)
        }

        text_event_help.visibilityBool = trial.isEvent
        text_event_timer.visibilityBool = trial.isEvent
        image_desired_rank.visibility = if (trial.isEvent) View.INVISIBLE else View.VISIBLE
        spinner_desired_rank.visibility = if (trial.isEvent) View.INVISIBLE else View.VISIBLE
        text_goals_content.visibilityBool = !trial.isEvent

        if (trial.isEvent) {
            val userRank = ladderManager.getUserRank()
            val scoringGroup = trial.findScoringGroup(TrialRank.fromLadderRank(userRank, true) ?: TrialRank.WOOD)
            text_event_timer.text = resources.getString(R.string.event_ends_format,
                SimpleDateFormat("MMMM dd", Locale.US).format(trial.event_end))
            text_event_help.text = resources.getString(R.string.event_directions,
                scoringGroup?.map { resources.getString(it.nameRes) }?.toListString(baseContext))
        } else {
            spinner_desired_rank.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, trialSession.availableRanks)
            spinner_desired_rank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    setRank(trialSession.availableRanks!![position])
                }
            }
            trialSession.availableRanks!!.let { ranks ->
                val initialSpinnerRank = ranks
                    .sortedBy { it.stableId }
                    .lastOrNull { initialRank.stableId >= it.stableId }
                    ?: ranks.first()
                spinner_desired_rank.setSelection(ranks.indexOf(initialSpinnerRank))
            }
        }

        switch_acquire_mode.isChecked = settingsManager.getUserFlag(KEY_DETAILS_PHOTO_SELECT, false)
        switch_acquire_mode.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.setUserFlag(KEY_DETAILS_PHOTO_SELECT, isChecked)
        }

        text_author_credit.visibilityBool = trial.author != null
        trial.author?.let { text_author_credit.text = getString(R.string.author_credit_format, it) }

        songListFragment = SongListFragment.newInstance(trial.id, tiled = false, useCurrentSession = true)
        supportFragmentManager.beginTransaction()
            .add(R.id.container_song_list_fragment, songListFragment)
            .commitNow()
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

    fun onSubmitClick(v: View) {
        if (!trialSession.shouldShowAdvancedSongDetails ||
            !settingsManager.getUserFlag(KEY_DETAILS_ENFORCE_EXPERT, true) ||
            trialSession.results.all { it!!.hasAdvancedStats }) {

            trialManager.submitResult(this) { finish() }
        } else {
            Snackbar.make(container, R.string.breakdown_information_missing, Snackbar.LENGTH_LONG).show()
        }
    }

    fun onFinalizeClick(v: View) {
        isFinal = true
        acquirePhoto()
    }

    private fun setRank(rank: TrialRank) {
        trialSession.goalRank = rank

        image_desired_rank.rank = rank.parent
        text_goals_content.text = trialSession.trialGoalSet?.generateSingleGoalString(resources, trial)

        songListFragment.shouldShowAdvancedSongDetails = trialSession.shouldShowAdvancedSongDetails
    }

    private fun updateCompleteState() {
        val allSongsComplete = trialSession.results.filterNotNull().size == TrialData.TRIAL_LENGTH
        button_concede.visibilityBool = !allSongsComplete
        button_finalize.visibilityBool = allSongsComplete && !trialSession.hasFinalPhoto
        button_submit.visibilityBool = allSongsComplete && trialSession.hasFinalPhoto
    }

    private fun updateHighestPossibleRank() {
        val currentGoal = trialSession.goalRank
        val highestPossible = trialSession.highestPossibleRank
        if (highestPossible == null) {
            AlertDialog.Builder(this).setTitle(R.string.trial_failed)
                .setMessage(getString(R.string.trial_fail_no_rank_confirmation, trial.name))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.okay) { _, _ -> concedeTrial() }
                .show()
        } else if (settingsManager.getUserFlag(KEY_DETAILS_UPDATE_GOAL, true) &&
            currentGoal != null &&
            highestPossible.stableId != currentGoal.stableId) {

            spinner_desired_rank.setSelection(trialSession.availableRanks!!.indexOf(highestPossible))
        }
    }

    private fun updateEXScoreMeter() {
        (include_ex_score as RunningEXScoreView).update(trialSession)
    }

    private fun scrollToBottom() {
        scroll_details.post {
            try {
                scroll_details.scrollTo(0, scroll_details.height)
            } catch (_: NullPointerException) { }
        }
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
            i.putExtra(SongEntryActivity.ARG_SONG, trial.songs[index])
            i.putExtra(SongEntryActivity.ARG_ADVANCED_DETAIL, trialSession.shouldShowAdvancedSongDetails)
            startActivityForResult(i, FLAG_SCORE_ENTER)
        }
    }

    private fun onScoreSummaryPhotoTaken(uri: Uri) {
        trialSession.finalPhotoUri = uri
        songListFragment.addResultsPhotoView(uri)
        updateCompleteState()
        scrollToBottom()
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
        updateEXScoreMeter()
        updateCompleteState()
        scrollToBottom()
        modified = true
        currentIndex = null
        isNewEntry = false
        currentPhotoFile = null
        updateHighestPossibleRank()
    }

    override fun onPhotoTaken(uri: Uri) {
        if (isFinal) {
            onScoreSummaryPhotoTaken(uri)
        } else {
            if (settingsManager.getDebugFlag(KEY_DEBUG_BYPASS_STAT_ENTRY)) {
                currentResult!!.randomize()
                onEntryFinished(currentResult!!)
            } else {
                startEditActivity(currentIndex!!)
            }
        }
    }

    override fun onPhotoChosen(uri: Uri) {
        if (isFinal) {
            onScoreSummaryPhotoTaken(uri)
        } else {
            if (currentResult == null) {
                currentResult = SongResult(trial.songs[currentIndex!!])
            }
            currentResult!!.photoUri = uri

            if (settingsManager.getDebugFlag(KEY_DEBUG_BYPASS_STAT_ENTRY)) {
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
        const val ARG_TRIAL_ID = "ARG_PLACEMENT_ID"
        const val ARG_INITIAL_RANK = "ARG_INITIAL_RANK"

        const val FLAG_SCORE_ENTER = 7 // to enter the score screen

        fun intent(c: Context, trialId: String, initialRank: TrialRank? = null) =
            Intent(c, TrialDetailsActivity::class.java).apply {
                putExtra(ARG_TRIAL_ID, trialId)
                initialRank?.let { putExtra(ARG_INITIAL_RANK, it) }
            }
    }
}