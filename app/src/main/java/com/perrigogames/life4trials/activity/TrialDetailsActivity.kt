package com.perrigogames.life4trials.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.Trial
import com.perrigogames.life4trials.data.TrialRank
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.PermissionUtils.FLAG_PERMISSION_REQUEST
import com.perrigogames.life4trials.util.askForPhotoPermissions
import com.perrigogames.life4trials.view.SongView
import kotlinx.android.synthetic.main.content_trial_details.*
import java.io.IOException

class TrialDetailsActivity: AppCompatActivity() {

    private var currentIndex: Int? = null
    private val trial: Trial? by lazy {
        intent.extras?.getSerializable(ARG_TRIAL) as Trial
    }

    private val initialRank: TrialRank by lazy {
        intent.extras?.getInt(ARG_INITIAL_RANK)?.let { TrialRank.values()[it] } ?: TrialRank.SILVER
    }

    private lateinit var rank: TrialRank
    private var cameraPhotoPath: String? = null
    private var modified = false

    private val availableRanks: Array<TrialRank> by lazy {
        TrialRank.values().let { ranks ->
            val targetRank = trial?.goals?.get(0)?.rank ?: TrialRank.SILVER
            ranks.copyOfRange(targetRank.ordinal, ranks.size)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_trial_details)

        rank = initialRank
        spinner_desired_rank.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, availableRanks)
        spinner_desired_rank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                rank = availableRanks[position]
                updateRank()
            }
        }

        button_finalize.isEnabled = false
        button_finalize.setOnClickListener { onFinalizeClick() }

        trial?.let { t ->
            Glide.with(this).load(t.jacketUrl(resources)).into(image_trial_jacket)
            text_trial_name.text = resources.getString(R.string.difficulty_string_format, t.name, t.difficulty)
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

    private fun songForIndex(index: Int) = when(index) {
        0 -> include_trial_1
        1 -> include_trial_2
        2 -> include_trial_3
        3 -> include_trial_4
        else -> null
    } as? SongView

    private inline fun forEachSongView(block: (Int, SongView) -> Unit) = (0..3).forEach { idx -> block(idx, songForIndex(idx)!!) }

    private fun onSongClicked(index: Int) {
        currentIndex = index
        val songView = songForIndex(index)!!
        if (songView.photoPath != null) {
            cameraPhotoPath = songView.photoPath
            startEditActivity(songView.photoPath!!, songView.score, songView.ex)
        } else {
            startCameraActivity(FLAG_IMAGE_CAPTURE)
        }
    }

    private fun onFinalizeClick() {
        startCameraActivity(FLAG_IMAGE_CAPTURE_FINAL)
    }

    private fun updateRank() = trial?.let { t ->
        image_desired_rank.setImageDrawable(ContextCompat.getDrawable(this, rank.drawableRes))
        StringBuilder().let { builder ->
            t.goals.forEach { goalSet ->
                if (goalSet.rank == rank) {
                    goalSet.generateGoalStrings(resources, t).forEach { s ->
                        builder.append("$s\n")
                    }
                }
            }
            text_goals_content.text = builder.toString()
        }
    }

    private fun updateSongs() {
        button_finalize.isEnabled = true
        forEachSongView { _, songView ->
            if (songView.photoPath == null) {
                button_finalize.isEnabled = false
                return@forEachSongView
            }
        }
    }

    private fun startCameraActivity(intentFlag: Int) {
        askForPhotoPermissions(R.string.camera_permission_description_popup) { sendCameraIntent(intentFlag) }
    }

    private fun startEditActivity(path: String, score: Int? = null, ex: Int? = null) {
        Intent(this, SongEntryActivity::class.java).also { i ->
            i.putExtra(SongEntryActivity.ARG_PHOTO_PATH, path)
            score?.let { i.putExtra(SongEntryActivity.ARG_SCORE, it) }
            ex?.let { i.putExtra(SongEntryActivity.ARG_EX, it) }
            startActivityForResult(i, FLAG_SCORE_ENTER)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
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

    private fun sendCameraIntent(intentFlag: Int) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {
                try { // Create the File where the photo should go
                    DataUtil.createImageFile()
                } catch (ex: IOException) {
                    null // Error occurred while creating the File
                }?.also {
                    cameraPhotoPath = it.absolutePath
                    val photoURI: Uri = FileProvider.getUriForFile(this, "com.perrigogames.fileprovider", it)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(intent, intentFlag)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FLAG_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            startEditActivity(cameraPhotoPath!!)
        } else if (requestCode == FLAG_IMAGE_CAPTURE_FINAL && resultCode == RESULT_OK) {
            startEditActivity(cameraPhotoPath!!)
        } else if (requestCode == FLAG_SCORE_ENTER) when (resultCode) {
            RESULT_OK -> {
                songForIndex(currentIndex!!)?.let {
                    it.photoPath = cameraPhotoPath
                    it.score = data!!.getIntExtra(SongEntryActivity.RESULT_SCORE, -1)
                    it.ex = data.getIntExtra(SongEntryActivity.RESULT_EX, -1)
                    updateSongs()
                    modified = true
                    currentIndex = null
                    cameraPhotoPath = null
                }
            }
            SongEntryActivity.STATUS_RETAKE -> startCameraActivity(FLAG_IMAGE_CAPTURE)
        }
    }

    companion object {
        const val ARG_TRIAL = "ARG_TRIAL"
        const val ARG_INITIAL_RANK = "ARG_INITIAL_RANK"

        const val FLAG_IMAGE_CAPTURE = 1
        const val FLAG_IMAGE_CAPTURE_FINAL = 2
        const val FLAG_SCORE_ENTER = 3
    }
}