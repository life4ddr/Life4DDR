package com.perrigogames.life4trials.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.GoalSet
import com.perrigogames.life4trials.data.Song
import com.perrigogames.life4trials.data.SongResult
import com.perrigogames.life4trials.util.DataUtil
import kotlinx.android.synthetic.main.content_song_entry.*


class SongEntryActivity: AppCompatActivity() {

    val result: SongResult? get() =
        intent?.extras?.getSerializable(ARG_RESULT) as SongResult

    val song: Song? get() =
        intent?.extras?.getSerializable(ARG_SONG) as? Song

    val goalSet: GoalSet? get() =
        intent?.extras?.getSerializable(ARG_GOAL_SET) as? GoalSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_song_entry)

        val bitmap = BitmapFactory.decodeFile(result!!.photoPath)
        image_photo.setImageDrawable(BitmapDrawable(resources, bitmap))
        button_retake.setOnClickListener { retakePhoto() }
        button_done.setOnClickListener { completeEntry() }

        if (result!!.score != null) {
            field_score.text = SpannableStringBuilder(result!!.score.toString())
        }
        if (result!!.exScore != null) {
            field_ex.text = SpannableStringBuilder(result!!.exScore.toString())
        }
    }

    private fun completeEntry() {
        field_score.error = null
        field_ex.error = null
        val score = try {
            field_score.text.toString().toInt()
        } catch (e: NumberFormatException) { -1 }
        val ex = try {
            field_ex.text.toString().toInt()
        } catch (e: NumberFormatException) { -1 }
        if (!BuildConfig.DEBUG && (score == -1 || ex == -1)) {
            if (score == -1) {
                field_score.error = getString(R.string.must_enter_number)
            }
            if (ex == -1) {
                field_ex.error = getString(R.string.must_enter_number)
            }
        } else {
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(RESULT_DATA, result)
            })
            finish()
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).setTitle(R.string.are_you_sure)
            .setMessage(R.string.camera_close_confirmation)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.okay) { _, _ -> cancel() }
            .show()
    }

    private fun retakePhoto() {
        setResult(STATUS_RETAKE)
        finish()
    }

    private fun cancel() {
        DataUtil.deleteExternalStoragePublicPicture(result!!.photoPath)
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    companion object {
        const val STATUS_RETAKE = 101

        const val ARG_RESULT = "ARG_RESULT"
        const val ARG_SONG = "ARG_SONG"
        const val ARG_GOAL_SET = "ARG_GOAL_SET"

        const val RESULT_DATA = "RESULT_DATA"
    }
}