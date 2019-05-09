package com.perrigogames.life4trials.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.SongResult
import com.perrigogames.life4trials.data.Trial
import com.perrigogames.life4trials.data.TrialRank
import com.perrigogames.life4trials.data.TrialSession
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.PermissionUtils.FLAG_PERMISSION_REQUEST
import com.perrigogames.life4trials.util.SharedPrefsUtils
import com.perrigogames.life4trials.util.askForPhotoPermissions
import com.perrigogames.life4trials.view.SongView
import com.perrigogames.life4trials.view.TrialJacketView
import kotlinx.android.synthetic.main.content_trial_details.*
import java.io.IOException

class TrialDetailsActivity: AppCompatActivity() {

    private val trial: Trial by lazy {
        intent.extras?.getSerializable(ARG_TRIAL) as Trial
    }
    private val storedRank: TrialRank? get() = SharedPrefsUtils.getRankForTrial(this, trial)
    private val initialRank: TrialRank by lazy { storedRank?.next
        ?: (intent.extras?.getInt(ARG_INITIAL_RANK)?.let { TrialRank.values()[it] } ?: TrialRank.SILVER)
    }

    private lateinit var trialSession: TrialSession
    private var currentIndex: Int? = null
    private var modified = false

    private fun songViewForIndex(index: Int?) = when(index) {
        0 -> include_song_1
        1 -> include_song_2
        2 -> include_song_3
        3 -> include_song_4
        else -> null
    } as? SongView

    private inline fun forEachSongView(block: (Int, SongView) -> Unit) = (0..3).forEach { idx -> block(idx, songViewForIndex(idx)!!) }

    private var currentResult: SongResult?
        get() = currentIndex?.let { trialSession.results[it] }
        set(v) {
            currentIndex?.let { trialSession.results[it] = v }
        }

    private val currentSongView get() = songViewForIndex(currentIndex)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_trial_details)

        trialSession = TrialSession(trial, initialRank)
        spinner_desired_rank.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, trialSession.availableRanks)
        spinner_desired_rank.setSelection(trialSession.availableRanks.indexOf(initialRank))
        spinner_desired_rank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setRank(trialSession.availableRanks[position])
            }
        }

        button_finalize.isEnabled = false
        button_finalize.setOnClickListener { onFinalizeClick() }

        trial.let { t ->
            (view_trial_jacket as TrialJacketView).let { jacket ->
                jacket.trial = t
                jacket.rank = storedRank
            }
            forEachSongView { idx, view ->
                view.song = t.songs[idx]
                view.setOnClickListener { onSongClicked(idx) }
            }
        }
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

    private fun onSongClicked(index: Int) {
        currentIndex = index
        if (currentResult != null) {
            startEditActivity(currentResult!!)
        } else {
            startCameraActivity(FLAG_IMAGE_CAPTURE)
        }
    }

    private fun onFinalizeClick() {
        startCameraActivity(FLAG_IMAGE_CAPTURE_FINAL)
    }

    private fun setRank(rank: TrialRank) {
        trialSession.goalRank = rank
        image_desired_rank.rank = rank
        text_goals_content.text = trialSession.goalSet?.generateSingleGoalString(resources, trial)
    }

    private fun updateSongs() {
        button_finalize.isEnabled = true
        forEachSongView { _, songView ->
            if (songView.result == null) {
                button_finalize.isEnabled = false
                return@forEachSongView
            }
        }
    }

    private fun startCameraActivity(intentFlag: Int) {
        askForPhotoPermissions(R.string.camera_permission_description_popup) { sendCameraIntent(intentFlag) }
    }

    private fun sendCameraIntent(intentFlag: Int) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {
                try { // Create the File where the photo should go
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        DataUtil.createImageFile(resources.configuration.locales[0])
                    } else {
                        DataUtil.createImageFile(resources.configuration.locale)
                    }
                } catch (ex: IOException) {
                    null // Error occurred while creating the File
                }?.also {
                    if (currentIndex != null) {
                        currentResult = SongResult(trial.songs[currentIndex!!], it.absolutePath)
                    } else {
                        trialSession.finalPhoto = it.absolutePath
                    }
                    val photoURI: Uri = FileProvider.getUriForFile(this, "com.perrigogames.fileprovider", it)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(intent, intentFlag)
                }
            }
        }
    }

    private fun startEditActivity(result: SongResult) {
        Intent(this, SongEntryActivity::class.java).also { i ->
            i.putExtra(SongEntryActivity.ARG_RESULT, result)
            startActivityForResult(i, FLAG_SCORE_ENTER)
        }
    }

    private fun startSubmitActivity() {
        Intent(this, TrialSubmissionActivity::class.java).also { i ->
            i.putExtra(TrialSubmissionActivity.ARG_SESSION, trialSession)
            startActivityForResult(i, FLAG_SCORE_ENTER)
        }
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            FLAG_PERMISSION_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    sendCameraIntent(FLAG_IMAGE_CAPTURE)
                }
                return
            }
            else -> Unit
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FLAG_IMAGE_CAPTURE) when (resultCode) {
            RESULT_OK -> {
                DataUtil.scaleSavedImage(currentResult!!.photoPath, 1080, 1080, contentResolver)
                if (BuildConfig.DEBUG) {
                    currentResult!!.let { result ->
                        result.score = (Math.random() * 70000).toInt() + 930000
                        result.exScore = (Math.random() * 1024).toInt()
                        result.misses = (Math.random() * 6).toInt()
                        result.badJudges = result.misses!! + (Math.random() * 14).toInt()
                        onEntryFinished(result)
                    }
                } else {
                    startEditActivity(currentResult!!)
                }
            }
            RESULT_CANCELED -> onEntryCancelled()
        } else if (requestCode == FLAG_IMAGE_CAPTURE_FINAL && resultCode == RESULT_OK) {
            DataUtil.scaleSavedImage(trialSession.finalPhoto!!, 1080, 1080, contentResolver)
            startSubmitActivity()
        } else if (requestCode == FLAG_SCORE_ENTER) when (resultCode) {
            RESULT_OK -> onEntryFinished(data!!.getSerializableExtra(SongEntryActivity.RESULT_DATA) as? SongResult)
            SongEntryActivity.RESULT_RETAKE -> startCameraActivity(FLAG_IMAGE_CAPTURE)
            RESULT_CANCELED -> onEntryCancelled()
        }
    }

    private fun onEntryFinished(result: SongResult?) {
        currentResult = result
        currentSongView?.result = currentResult
        updateSongs()
        modified = true
        currentIndex = null
    }

    private fun onEntryCancelled() {
        currentResult = null
        currentIndex = null
    }

    companion object {
        const val ARG_TRIAL = "ARG_TRIAL"
        const val ARG_INITIAL_RANK = "ARG_INITIAL_RANK"

        const val FLAG_IMAGE_CAPTURE = 1
        const val FLAG_IMAGE_CAPTURE_FINAL = 2
        const val FLAG_SCORE_ENTER = 3
    }
}