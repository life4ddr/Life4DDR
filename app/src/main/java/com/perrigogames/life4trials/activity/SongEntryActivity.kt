package com.perrigogames.life4trials.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.Song
import com.perrigogames.life4trials.data.SongResult
import com.perrigogames.life4trials.util.SharedPrefsUtils
import com.perrigogames.life4trials.util.SharedPrefsUtils.getUserFlag
import kotlinx.android.synthetic.main.content_song_entry.*


class SongEntryActivity: AppCompatActivity() {

    val result: SongResult? get() =
        intent?.extras?.getSerializable(ARG_RESULT) as? SongResult

    val song: Song? get() =
        intent?.extras?.getSerializable(ARG_SONG) as? Song

    val advancedDetail: Boolean get() =
        intent?.extras?.getSerializable(ARG_ADVANCED_DETAIL) as? Boolean ?: false

    val newEntry: Boolean get() = result?.score == null
    var modified: Boolean = false

    // Lists of fields for easy iteration
    val normalFields: List<EditText> by lazy { listOf(field_score, field_ex) }
    val advancedFields: List<EditText> by lazy { listOf(field_misses, field_goods, field_greats) }
    val expertFields: List<EditText> by lazy { listOf(field_greats_less, field_perfects) }
    val allFields: List<EditText> by lazy { normalFields + advancedFields + expertFields }

    // Entered field values
    val score: Int? get() = try { field_score.text.toString().toInt() } catch (e: NumberFormatException) { null }
    val ex: Int? get() = try { field_ex.text.toString().toInt() } catch (e: NumberFormatException) { null }
    val misses: Int? get() = try { field_misses.text.toString().toInt() } catch (e: NumberFormatException) { null }
    val goods: Int? get() = try { field_goods.text.toString().toInt() } catch (e: NumberFormatException) { null }
    val greats: Int? get() = try { field_greats.text.toString().toInt() } catch (e: NumberFormatException) { null }
    val greatsLess: Int? get() = try { field_greats_less.text.toString().toInt() } catch (e: NumberFormatException) { null }
    val perfects: Int? get() = try { field_perfects.text.toString().toInt() } catch (e: NumberFormatException) { null }

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
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it.photoUri)
            image_photo.setImageDrawable(BitmapDrawable(resources, bitmap))
            button_retake.setOnClickListener { retakePhoto() }
            button_done.setOnClickListener { completeEntry() }

            checkbox_passed.isChecked = it.passed

            if (it.score != null) {
                field_score.text = SpannableStringBuilder(it.score.toString())
            }
            if (it.exScore != null) {
                field_ex.text = SpannableStringBuilder(it.exScore.toString())
            }
            if (it.misses != null && it.misses != -1) {
                field_misses.text = SpannableStringBuilder(it.misses.toString())
            }
            if (it.badJudges != null && it.misses != -1) {
                checkbox_expert.isChecked = true
                field_greats_less.text = SpannableStringBuilder(it.badJudges.toString())
            }
            if (it.perfects != null && it.misses != -1) {
                checkbox_expert.isChecked = true
                field_perfects.text = SpannableStringBuilder(it.perfects.toString())
            }
            field_score.addTextChangedListener(textWatcher)
            field_ex.addTextChangedListener(textWatcher)

            checkbox_expert.setOnCheckedChangeListener { _, isChecked ->
                SharedPrefsUtils.setUserFlag(this, SettingsActivity.KEY_DETAILS_EXPERT, isChecked)
                updateAdvancedFields(isChecked)
            }
            if (!checkbox_expert.isChecked) {
                checkbox_expert.isChecked = getUserFlag(this, SettingsActivity.KEY_DETAILS_EXPERT, false)
            }
            updateAdvancedFields(checkbox_expert.isChecked)

            if (field_score.requestFocus()) {
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .showSoftInput(field_score, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        if (res == null) {
            finish()
        }
    }

    private fun updateAdvancedFields(isExpert: Boolean) {
        checkbox_expert.visibility = if (advancedDetail) VISIBLE else GONE
        advancedFields.forEach { it.visibility = if (advancedDetail && !isExpert) VISIBLE else GONE }
        expertFields.forEach { it.visibility = if (advancedDetail && isExpert) VISIBLE else GONE }
    }

    private fun completeEntry() {
        allFields.forEach { it.error = null }
        checkErrorForValue(score, field_score)
        checkErrorForValue(ex, field_ex)
        checkErrorForValue(misses, field_misses)
        checkErrorForValue(goods, field_goods)
        checkErrorForValue(greats, field_greats)
        checkErrorForValue(greatsLess, field_greats_less)
        checkErrorForValue(perfects, field_perfects)
        if (!BuildConfig.DEBUG && allFields.any { it.visibility == VISIBLE && it.error != null }) {
            Toast.makeText(this, R.string.make_sure_fields_filled, Toast.LENGTH_SHORT)
        } else {
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(RESULT_DATA, result!!.also {
                    it.score = score
                    it.exScore = ex
                    if (advancedDetail) {
                        if (!checkbox_expert.isChecked) {
                            it.misses = misses
                            it.badJudges = if (misses != null && goods != null && greats != null)
                                misses!! + goods!! + greats!!
                            else null
                            it.perfects = if (it.badJudges != null) -1 else null
                        } else {
                            it.misses = -1
                            it.badJudges = greatsLess
                            it.perfects = perfects
                        }
                    }
                    it.passed = checkbox_passed.isChecked
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

    private fun checkErrorForValue(value: Int?, field: EditText) {
        if (value == null) {
            field.error = getString(R.string.must_enter_number)
        }
    }

    private fun retakePhoto() {
        setResult(RESULT_RETAKE)
        finish()
    }

    private fun cancel() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    companion object {
        const val RESULT_RETAKE = 101

        const val ARG_RESULT = "ARG_RESULT"
        const val ARG_SONG = "ARG_SONG"
        const val ARG_ADVANCED_DETAIL = "ARG_ADVANCED_DETAIL"

        const val RESULT_DATA = "RESULT_DATA"
    }
}