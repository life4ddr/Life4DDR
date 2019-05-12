package com.perrigogames.life4trials.activity

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_DEBUG_DETAILS_EASY_NAV
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_DETAILS_PHOTO_SELECT
import com.perrigogames.life4trials.data.*
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.PermissionUtils.FLAG_PERMISSION_REQUEST_CAMERA
import com.perrigogames.life4trials.util.PermissionUtils.FLAG_PERMISSION_REQUEST_SELECT
import com.perrigogames.life4trials.util.SharedPrefsUtils
import com.perrigogames.life4trials.util.askForPhotoSelectPermissions
import com.perrigogames.life4trials.util.askForPhotoTakePermissions
import com.perrigogames.life4trials.view.SongView
import com.perrigogames.life4trials.view.TrialJacketView
import kotlinx.android.synthetic.main.content_trial_details.*
import java.io.File
import java.io.IOException


class TrialDetailsActivity: AppCompatActivity() {

    private val trialData: TrialData get() = (application as Life4Application).trialData
    private val trialIndex: Int by lazy { intent.extras!!.getInt(ARG_TRIAL_INDEX) }
    private val trial: Trial get() = trialData.trials[trialIndex]

    private val storedRank: TrialRank? get() = SharedPrefsUtils.getRankForTrial(this, trial)
    private val initialRank: TrialRank by lazy { storedRank?.next
        ?: (intent.extras?.getInt(ARG_INITIAL_RANK)?.let { TrialRank.values()[it] } ?: TrialRank.SILVER)
    }

    private lateinit var trialSession: TrialSession
    private var currentIndex: Int? = null
    private var modified = false
    private var isNewEntry = false

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

        (view_trial_jacket as TrialJacketView).let { jacket ->
            jacket.trial = trial
            jacket.rank = storedRank
        }
        forEachSongView { idx, view ->
            view.tag = idx
            view.song = trial.songs[idx]
            view.setOnClickListener { onSongClicked(idx) }
            registerForContextMenu(view)
        }

        if (SharedPrefsUtils.getDebugFlag(this, KEY_DEBUG_DETAILS_EASY_NAV)) {
            button_navigate_previous.visibility = VISIBLE
            button_navigate_next.visibility = VISIBLE
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

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v is SongView) {
            val idx = v.tag as Int
            currentIndex = idx
            val menuRes = if (trialSession.results[idx]?.photoUriString != null) R.menu.menu_song_replace else R.menu.menu_song_add
            menuInflater.inflate(menuRes, menu)
        }
    }

    fun navigationButtonClicked(v: View) {
        val index = trialIndex + (if (v.id == R.id.button_navigate_previous) -1 else 1)
        startActivity(intent(this, index))
        finish()
    }

    private fun onSongClicked(index: Int) {
        currentIndex = index
        if (currentResult != null) {
            startEditActivity(currentIndex!!)
        } else {
            isNewEntry = true
            if (SharedPrefsUtils.getUserFlag(this, KEY_DETAILS_PHOTO_SELECT, false)) {
                startPhotoSelectActivity(FLAG_IMAGE_SELECT)
            } else {
                startCameraActivity(FLAG_IMAGE_CAPTURE)
            }
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
        forEachSongView { idx, songView ->
            songView.result = trialSession.results[idx]
            if (trialSession.results[idx] == null) {
                button_finalize.isEnabled = false
                return@forEachSongView
            }
        }
    }

    private fun startCameraActivity(intentFlag: Int) {
        askForPhotoTakePermissions(R.string.camera_permission_title,
            R.string.camera_permission_description_popup) { sendCameraIntent(intentFlag) }
    }

    @RequiresPermission(allOf = [CAMERA, WRITE_EXTERNAL_STORAGE])
    private fun sendCameraIntent(intentFlag: Int) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {
                try { // Create the File where the photo should go
                    @Suppress("DEPRECATION")
                    when {
                        currentResult?.photoUriString != null -> File(currentResult!!.photoUri.path)
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> DataUtil.createImageFile(resources.configuration.locales[0])
                        else -> DataUtil.createImageFile(resources.configuration.locale)
                    }
                } catch (ex: IOException) {
                    null // Error occurred while creating the File
                }?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(this, getString(R.string.file_provider_name), it)
                    if (currentIndex != null) {
                        currentResult = currentResult ?: SongResult(trial.songs[currentIndex!!], photoURI.toString())
                    } else {
                        trialSession.finalPhotoUriString = photoURI.toString()
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(intent, intentFlag)
                }
            }
        }
    }

    private fun startPhotoSelectActivity(intentFlag: Int) {
        askForPhotoSelectPermissions(R.string.gallery_permission_title,
            R.string.gallery_permission_description_popup) { sendPhotoSelectIntent(intentFlag) }
    }

    @RequiresPermission(WRITE_EXTERNAL_STORAGE)
    private fun sendPhotoSelectIntent(intentFlag: Int) {
        if (Build.VERSION.SDK_INT < 19) {
            Intent().also { i ->
                i.type = "image/*"
                i.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(i, resources.getString(R.string.add_gallery)), intentFlag)
            }
        } else {
            Intent(Intent.ACTION_OPEN_DOCUMENT).also { i ->
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = "image/jpeg"
                startActivityForResult(i, intentFlag)
            }
        }
    }

    private fun startEditActivity(index: Int) {
        Intent(this, SongEntryActivity::class.java).also { i ->
            i.putExtra(SongEntryActivity.ARG_RESULT, trialSession.results[index])
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
            FLAG_PERMISSION_REQUEST_CAMERA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    sendCameraIntent(FLAG_IMAGE_CAPTURE)
                }
                return
            }
            FLAG_PERMISSION_REQUEST_SELECT -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    sendPhotoSelectIntent(FLAG_IMAGE_SELECT)
                }
                return
            }
            else -> Unit
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            FLAG_IMAGE_CAPTURE,
            FLAG_IMAGE_RECAPTURE -> when (resultCode) {
                RESULT_OK -> {
                    DataUtil.scaleSavedImage(currentResult!!.photoUri.path!!, 1080, 1080, contentResolver)
                    if (SharedPrefsUtils.getDebugFlag(this, SettingsActivity.KEY_DEBUG_BYPASS_STAT_ENTRY)) {
                        currentResult!!.randomize()
                        onEntryFinished(currentResult!!)
                    } else {
                        startEditActivity(currentIndex!!)
                    }
                }
                RESULT_CANCELED -> onEntryCancelled()
            }
            FLAG_IMAGE_SELECT,
            FLAG_IMAGE_RESELECT -> when (resultCode) {
                RESULT_OK -> {
                    val uri = data!!.data
                    val takeFlags = data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        contentResolver.takePersistableUriPermission(uri, takeFlags)
                    }
                    currentResult = currentResult ?: SongResult(trial.songs[currentIndex!!], uri.toString())
                    if (SharedPrefsUtils.getDebugFlag(this, SettingsActivity.KEY_DEBUG_BYPASS_STAT_ENTRY)) {
                        currentResult!!.randomize()
                        onEntryFinished(currentResult!!)
                    } else {
                        startEditActivity(currentIndex!!)
                    }
                }
                RESULT_CANCELED -> onEntryCancelled()
            }
            FLAG_IMAGE_CAPTURE_FINAL -> when (resultCode) {
                RESULT_OK -> {
                    DataUtil.scaleSavedImage(trialSession.finalPhotoUri.path!!, 1080, 1080, contentResolver)
                    startSubmitActivity()
                }
            }
            FLAG_SCORE_ENTER -> when (resultCode) {
                RESULT_OK -> onEntryFinished(data!!.getSerializableExtra(SongEntryActivity.RESULT_DATA) as? SongResult)
                SongEntryActivity.RESULT_RETAKE -> startCameraActivity(if (isNewEntry) FLAG_IMAGE_CAPTURE else FLAG_IMAGE_RECAPTURE)
                RESULT_CANCELED -> onEntryCancelled()
            }
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.action_gallery -> {
                startPhotoSelectActivity(if (isNewEntry) FLAG_IMAGE_SELECT else FLAG_IMAGE_RESELECT)
                true
            }
            R.id.action_camera -> {
                startCameraActivity(if (isNewEntry) FLAG_IMAGE_CAPTURE else FLAG_IMAGE_RECAPTURE)
                true
            }
            R.id.action_edit -> {
                startEditActivity(currentIndex!!)
                true
            }
            else -> {
                currentIndex = null
                false
            }
        }
    }

    private fun onEntryFinished(result: SongResult?) {
        currentResult = result
        updateSongs()
        modified = true
        currentIndex = null
        isNewEntry = false
    }

    private fun onEntryCancelled() {
        if (isNewEntry) {
            currentResult = null
        }
        currentIndex = null
        isNewEntry = false
    }

    companion object {
        const val ARG_TRIAL_INDEX = "ARG_TRIAL_INDEX"
        const val ARG_INITIAL_RANK = "ARG_INITIAL_RANK"

        const val FLAG_IMAGE_CAPTURE = 1 // capturing for a new song
        const val FLAG_IMAGE_RECAPTURE = 2 // retaking a picture for a song that already exists
        const val FLAG_IMAGE_CAPTURE_FINAL = 3 // capturing the final score screen
        const val FLAG_IMAGE_SELECT = 4 // selecting a local photo for a new song
        const val FLAG_IMAGE_RESELECT = 5 // reselecting a local photo for a song that already exists
        const val FLAG_IMAGE_SELECT_FINAL = 6 // selecting a local photo of the final score screen
        const val FLAG_SCORE_ENTER = 7 // to enter the score screen

        fun intent(c: Context, trialIndex: Int, initialRank: TrialRank? = null) =
            Intent(c, TrialDetailsActivity::class.java).apply {
                putExtra(ARG_TRIAL_INDEX, trialIndex)
                initialRank?.let { putExtra(ARG_INITIAL_RANK, it) }
            }
    }
}