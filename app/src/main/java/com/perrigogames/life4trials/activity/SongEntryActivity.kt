package com.perrigogames.life4trials.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.method.KeyListener
import android.view.KeyEvent
import android.view.View
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
        intent?.extras?.getSerializable(ARG_RESULT) as? SongResult

    val song: Song? get() =
        intent?.extras?.getSerializable(ARG_SONG) as? Song

    val goalSet: GoalSet? get() =
        intent?.extras?.getSerializable(ARG_GOAL_SET) as? GoalSet

    val newEntry: Boolean get() = result?.score == null
    var modified: Boolean = false

    private val textWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            modified = true
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_song_entry)

        val res = result?.let {
            val bitmap = BitmapFactory.decodeFile(it.photoPath)
            image_photo.setImageDrawable(BitmapDrawable(resources, bitmap))
            button_retake.setOnClickListener { retakePhoto() }
            button_done.setOnClickListener { completeEntry() }

            if (it.score != null) {
                field_score.text = SpannableStringBuilder(it.score.toString())
            }
            if (it.exScore != null) {
                field_ex.text = SpannableStringBuilder(it.exScore.toString())
            }
            field_score.addTextChangedListener(textWatcher)
            field_ex.addTextChangedListener(textWatcher)
        }
        if (res == null) {
            finish()
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
                putExtra(RESULT_DATA, result!!.also {
                    it.score = score
                    it.exScore = ex
                })
            })
            finish()
        }
    }

    override fun onBackPressed() {
        if (newEntry || modified) {
            AlertDialog.Builder(this).setTitle(R.string.are_you_sure)
                .setMessage(when {
                    newEntry -> R.string.photo_save_confirmation
                    modified -> R.string.details_save_confirmation
                    else -> R.string.no // why?
                })
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.okay) { _, _ -> cancel() }
                .show()
        } else {
            cancel()
        }
    }

    private fun retakePhoto() {
        setResult(RESULT_RETAKE)
        finish()
    }

    private fun cancel() {
        DataUtil.deleteExternalStoragePublicPicture(result!!.photoPath)
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    companion object {
        const val RESULT_RETAKE = 101

        const val ARG_RESULT = "ARG_RESULT"
        const val ARG_SONG = "ARG_SONG"
        const val ARG_GOAL_SET = "ARG_GOAL_SET"

        const val RESULT_DATA = "RESULT_DATA"
    }
}