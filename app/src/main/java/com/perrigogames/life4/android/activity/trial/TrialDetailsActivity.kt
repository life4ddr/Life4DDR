@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.perrigogames.life4.android.activity.trial

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
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.google.android.material.snackbar.Snackbar
import com.perrigogames.life4.GameConstants
import com.perrigogames.life4.PlatformStrings
import com.perrigogames.life4.SettingsKeys.KEY_DEBUG_BYPASS_STAT_ENTRY
import com.perrigogames.life4.SettingsKeys.KEY_DETAILS_ENFORCE_EXPERT
import com.perrigogames.life4.SettingsKeys.KEY_DETAILS_PHOTO_SELECT
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.activity.base.PhotoCaptureActivity
import com.perrigogames.life4.android.databinding.ContentTrialDetailsBinding
import com.perrigogames.life4.android.finalPhotoUri
import com.perrigogames.life4.android.manager.AndroidTrialNavigation
import com.perrigogames.life4.android.nameRes
import com.perrigogames.life4.android.photoUri
import com.perrigogames.life4.android.toListString
import com.perrigogames.life4.android.ui.songlist.SongListFragment
import com.perrigogames.life4.android.util.openWebUrlFromRes
import com.perrigogames.life4.android.util.visibilityBool
import com.perrigogames.life4.android.view.SongView
import com.perrigogames.life4.data.Song
import com.perrigogames.life4.data.SongResult
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.data.TrialState
import com.perrigogames.life4.enums.TrialRank
import com.perrigogames.life4.feature.profile.UserRankManager
import com.perrigogames.life4.feature.trials.TrialManager
import com.perrigogames.life4.feature.trials.TrialSessionManager
import com.perrigogames.life4.getDebugBoolean
import com.russhwolf.settings.set
import kotlinx.datetime.toJavaLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.Locale


class TrialDetailsActivity: PhotoCaptureActivity(), SongListFragment.Listener, KoinComponent {

    private lateinit var binding: ContentTrialDetailsBinding

    private val userRankManager: UserRankManager by inject()
    private val trialManager: TrialManager by inject()
    private val trialSessionManager: TrialSessionManager by inject()
    private val strings: PlatformStrings by inject()
    private val trialNavigation: AndroidTrialNavigation by inject()

    private val trialId: String by lazy { intent.extras!!.getString(ARG_TRIAL_ID)!! }
    private val trial: Trial by lazy { trialManager.findTrial(trialId)!! }

    override val snackbarContainer: ViewGroup get() = binding.container

    private val trialSession get() = trialSessionManager.currentSession!!
    private var currentIndex: Int? = null
    private var modified = false
        set(v) {
            field = v
            binding.buttonNavigatePrevious.visibilityBool = !v
            binding.buttonNavigateNext.visibilityBool = !v
        }
    private var isNewEntry = false
    private var isFinal = false

    private val editEntry = registerForActivityResult(StartActivityForResult()) { result ->
        when (result.resultCode) {
            RESULT_OK -> onEntryFinished(currentResult!!)
            SongEntryActivity.RESULT_RETAKE -> acquirePhoto()
            RESULT_CANCELED -> onEntryCancelled()
        }
    }

    private lateinit var songListFragment: SongListFragment

    private var currentResult: SongResult?
        get() = currentIndex?.let { trialSession.results[it] }
        set(v) {
            currentIndex?.let { trialSession.results[it] = v }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentTrialDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = if (trial.isRetired)
            getString(R.string.retired_suffix_format, trial.name)
        else trial.name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        updateEXScoreMeter()

//        binding.imageRank.bind(
//            TrialJacketViewModel(
//                trial = trial,
//                rank = storedRank,
//                overrideCorner = TrialJacketCorner.NONE,
//            )
//        )

        binding.textEventHelp.visibilityBool = trial.isEvent
        binding.textEventTimer.visibilityBool = trial.isEvent
        binding.imageDesiredRank.visibility = if (trial.isEvent) View.INVISIBLE else View.VISIBLE
        binding.spinnerDesiredRank.visibility = if (trial.isEvent) View.INVISIBLE else View.VISIBLE
        binding.textGoalsContent.visibilityBool = !trial.isEvent

        if (trial.isEvent) {
            val userRank = userRankManager.currentRank
            val scoringGroup = trial.findScoringGroup(TrialRank.fromLadderRank(userRank, true) ?: TrialRank.COPPER)
            val formatString =
                if (trial.isActiveEvent) R.string.event_ends_future_format
                else R.string.event_ends_past_format
            binding.textEventTimer.text = resources.getString(formatString,
                SimpleDateFormat("d MMMM dd", Locale.US)
                    .format(trial.eventEnd!!.toJavaLocalDateTime())
            )
            binding.textEventHelp.text = resources.getString(R.string.event_directions,
                scoringGroup?.map { resources.getString(it.nameRes) }?.toListString(baseContext, useAnd = false, caps = false))
        } else {
            binding.spinnerDesiredRank.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, trialSession.availableRanks)
            binding.spinnerDesiredRank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    setRank(trialSession.availableRanks[position])
                }
            }
//            trialSession.availableRanks.let { ranks ->
//                val initialSpinnerRank = ranks
//                    .sortedBy { it.stableId }
//                    .lastOrNull { initialRank.stableId >= it.stableId }
//                    ?: ranks.first()
//                binding.spinnerDesiredRank.setSelection(ranks.indexOf(initialSpinnerRank))
//            }
        }

        binding.includePhotoSourceSelector.switchAcquireMode.apply {
            isChecked = settings.getBoolean(KEY_DETAILS_PHOTO_SELECT, false)
            setOnCheckedChangeListener { _, isChecked ->
                settings[KEY_DETAILS_PHOTO_SELECT] = isChecked
            }
        }

        binding.textAuthorCredit.visibilityBool = trial.author != null
        trial.author?.let { binding.textAuthorCredit.text = getString(R.string.author_credit_format, it) }

        binding.buttonConcede.setOnClickListener { onConcedeClick() }
        binding.buttonFinalize.setOnClickListener { onFinalizeClick() }
        binding.buttonLeaderboard.setOnClickListener { onLeaderboardClick() }
        binding.buttonSubmit.setOnClickListener { onSubmitClick() }

        if (trial.state == TrialState.RETIRED) {
            onRetiredTrialStart()
        }

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
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

    override fun onFinalImageSelected() {
        onFinalizeClick()
    }

    private fun onLeaderboardClick() = openWebUrlFromRes(R.string.url_trial, trial.id)

    private fun onConcedeClick() {
        AlertDialog.Builder(this).setTitle(R.string.are_you_sure)
            .setMessage(getString(R.string.trial_concede_confirmation_format, trial.name, trialSession.goalRank))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.okay) { _, _ -> concedeTrial() }
            .show()
    }

    private fun onSubmitClick() {
        if (trial.isRetired) {
            onRetiredTrialFinish()
        } else if (!trialSession.shouldShowAdvancedSongDetails ||
            !settings.getBoolean(KEY_DETAILS_ENFORCE_EXPERT, true) ||
            trialSession.results.all { it!!.hasAdvancedStats }) {

            trialNavigation.submitResult(this) { finish() }
        } else {
            Snackbar.make(binding.container, R.string.breakdown_information_missing, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun onRetiredTrialStart() {
        AlertDialog.Builder(this).setTitle(R.string.trial_retired_dialog_title)
            .setMessage(getString(R.string.trial_retired_dialog_description_format, trial.name))
            .setNegativeButton(R.string.cancel) { _, _ -> finish() }
            .setPositiveButton(R.string.okay, null)
            .show()
    }

    private fun onRetiredTrialFinish() {
        AlertDialog.Builder(this).setTitle(R.string.trial_retired_dialog_title)
            .setMessage(getString(R.string.trial_submit_retired_dialog_prompt_format, trial.name))
            .setPositiveButton(R.string.okay) { _, _ -> finish() }
            .show()
    }

    private fun onFinalizeClick() {
        isFinal = true
        acquirePhoto()
    }

    private fun setRank(rank: TrialRank) {
        trialSession.goalRank = rank

        binding.imageDesiredRank.rank = rank.parent
        binding.textGoalsContent.text = trialSession.trialGoalSet?.generateSingleGoalString(strings.trial, trial)

        songListFragment.shouldShowAdvancedSongDetails = trialSession.shouldShowAdvancedSongDetails
    }

    private fun updateCompleteState() {
        val allSongsComplete = trialSession.results.filterNotNull().size == GameConstants.TRIAL_LENGTH
        binding.buttonConcede.visibilityBool = !allSongsComplete
        binding.buttonFinalize.visibilityBool = allSongsComplete && !trialSession.hasFinalPhoto
        binding.buttonSubmit.visibilityBool = allSongsComplete && trialSession.hasFinalPhoto
    }

    private fun updateHighestPossibleRank() {
//        if (highestPossible == null) {
//            AlertDialog.Builder(this).setTitle(R.string.trial_failed)
//                .setMessage(getString(R.string.trial_fail_no_rank_confirmation, trial.name))
//                .setNegativeButton(R.string.cancel, null)
//                .setPositiveButton(R.string.okay) { _, _ -> concedeTrial() }
//                .show()
//        } else if (settings.getBoolean(KEY_DETAILS_UPDATE_GOAL, true) &&
//            currentGoal != null &&
//            highestPossible.stableId != currentGoal.stableId) {
//
//            binding.spinnerDesiredRank.setSelection(trialSession.availableRanks.indexOf(highestPossible))
//        }
    }

    private fun updateEXScoreMeter() {
        binding.includeExScore.update(trialSession.progress)
    }

    private fun scrollToBottom() {
        binding.scrollDetails.post {
            try {
                binding.scrollDetails.scrollTo(0, binding.scrollDetails.height)
            } catch (_: NullPointerException) { }
        }
    }

    private fun concedeTrial() {
        trialSessionManager.saveSession(trialSession)
        finish()
    }

    override fun onNewPhotoCreated(uri: Uri) {
        if (currentIndex != null) {
            if (currentResult == null) {
                currentResult = SongResult(
                    trial.songs[currentIndex!!],
                    uri.toString()
                )
            } else {
                currentResult!!.photoUri = uri
            }
        } else {
            trialSession.finalPhotoUriString = uri.toString()
        }
    }

    private fun startEditActivity(index: Int) {
        editEntry.launch(
            Intent(this, SongEntryActivity::class.java).apply {
                putExtra(SongEntryActivity.ARG_SONG_INDEX, index)
                putExtra(SongEntryActivity.ARG_ADVANCED_DETAIL, trialSession.shouldShowAdvancedSongDetails)
            }
        )
    }

    private fun onScoreSummaryPhotoTaken(uri: Uri) {
        trialSession.finalPhotoUri = uri
        songListFragment.addResultsPhotoView(uri)
        updateCompleteState()
        scrollToBottom()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
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
            if (settings.getDebugBoolean(KEY_DEBUG_BYPASS_STAT_ENTRY)) {
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
                currentResult = SongResult(trial.songs[currentIndex!!], null)
            }
            currentResult!!.photoUri = uri

            if (settings.getDebugBoolean(KEY_DEBUG_BYPASS_STAT_ENTRY)) {
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

        fun intent(c: Context, trialId: String, initialRank: TrialRank? = null) =
            Intent(c, TrialDetailsActivity::class.java).apply {
                putExtra(ARG_TRIAL_ID, trialId)
                initialRank?.let { putExtra(ARG_INITIAL_RANK, it) }
            }
    }
}
