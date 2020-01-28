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
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_DEBUG_ACCEPT_INVALID
import com.perrigogames.life4trials.data.ClearType
import com.perrigogames.life4trials.data.ClearType.*
import com.perrigogames.life4trials.data.Song
import com.perrigogames.life4trials.data.SongResult
import com.perrigogames.life4trials.data.TrialData
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.util.visibilityBool
import kotlinx.android.synthetic.main.content_song_entry.*


class SongEntryActivity: AppCompatActivity() {

    val result: SongResult? get() =
        intent?.extras?.getSerializable(ARG_RESULT) as? SongResult

    val song: Song? get() =
        intent?.extras?.getSerializable(ARG_SONG) as? Song

    private val requiresAdvancedDetail: Boolean get() =
        intent?.extras?.getSerializable(ARG_ADVANCED_DETAIL) as? Boolean ?: false

    val newEntry: Boolean get() = result?.score == null
    var modified: Boolean = false
    private var advancedFieldVisibility: Boolean = false
        set(v) = advancedFields.forEach { it.visibilityBool = v }

    // Lists of fields for easy iteration
    private val advancedFields: List<EditText> by lazy { listOf(field_misses, field_goods, field_greats, field_perfects) }
    private val allFields: List<EditText> by lazy { listOf(field_score, field_ex) + advancedFields }

    // Entered field values
    private val score: Int? get() = try { field_score.text.toString().toInt() } catch (e: NumberFormatException) { null }
    private val ex: Int? get() = try { field_ex.text.toString().toInt() } catch (e: NumberFormatException) { null }
    private val misses: Int? get() = try { field_misses.text.toString().toInt() } catch (e: NumberFormatException) { null }
    private val goods: Int? get() = try { field_goods.text.toString().toInt() } catch (e: NumberFormatException) { null }
    private val greats: Int? get() = try { field_greats.text.toString().toInt() } catch (e: NumberFormatException) { null }
    private val perfects: Int? get() = try { field_perfects.text.toString().toInt() } catch (e: NumberFormatException) { null }

    private val textWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            modified = true
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    }
    private val perfectTextWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (clearType.stableId >= PERFECT_FULL_COMBO.stableId) {
                modified = true
                val intVal = s?.toString()?.toIntOrNull() ?: 0
                field_score.setText((TrialData.MAX_SCORE - (intVal * TrialData.SCORE_PENALTY_PERFECT)).toString())
                field_ex.setText(((song?.ex ?: 0) - intVal).toString())
            }
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

            button_clear.setOnClickListener { clearType = CLEAR }
            button_fc.isEnabled = requiresAdvancedDetail
            button_fc.setOnClickListener { clearType = GOOD_FULL_COMBO }
            button_pfc.setOnClickListener { clearType = PERFECT_FULL_COMBO }
            button_mfc.setOnClickListener { clearType = MARVELOUS_FULL_COMBO }

            checkbox_passed.isChecked = it.passed

            if (it.score != null) {
                field_score.text = SpannableStringBuilder(it.score.toString())
            }
            if (it.exScore != null) {
                field_ex.text = SpannableStringBuilder(it.exScore.toString())
            }
            if (it.misses != null) {
                field_misses.text = SpannableStringBuilder(it.misses.toString())
            }
            if (it.goods != null) {
                field_goods.text = SpannableStringBuilder(it.goods.toString())
            }
            if (it.greats != null) {
                field_greats.text = SpannableStringBuilder(it.greats.toString())
            }
            if (it.perfects != null) {
                field_perfects.text = SpannableStringBuilder(it.perfects.toString())
            }
            field_score.addTextChangedListener(textWatcher)
            field_ex.addTextChangedListener(textWatcher)

            field_perfects.addTextChangedListener(perfectTextWatcher)

            advancedFieldVisibility = requiresAdvancedDetail

            if (field_score.requestFocus()) {
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .showSoftInput(field_score, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        if (res == null) {
            finish()
        }
    }

    private var clearType = CLEAR
        set(type) {
            field = type
            if (type == PERFECT_FULL_COMBO) {
                advancedFieldVisibility = true
                field_perfects.requestFocus()
            } else {
                advancedFieldVisibility = requiresAdvancedDetail
            }
            updateFieldEditable(field_score, type, PERFECT_FULL_COMBO)
            updateFieldEditable(field_ex, type, PERFECT_FULL_COMBO)
            updateFieldEditable(field_misses, type, GOOD_FULL_COMBO)
            updateFieldEditable(field_goods, type, GREAT_FULL_COMBO)
            updateFieldEditable(field_greats, type, PERFECT_FULL_COMBO)
            updateFieldEditable(field_perfects, type, MARVELOUS_FULL_COMBO, type == PERFECT_FULL_COMBO)
            field_ex.nextFocusDownId = when (type) {
                MARVELOUS_FULL_COMBO -> 0
                PERFECT_FULL_COMBO -> R.id.field_perfects
                GREAT_FULL_COMBO -> R.id.field_greats
                GOOD_FULL_COMBO -> R.id.field_goods
                else -> R.id.field_misses
            }
        }

    private fun updateFieldEditable(field: EditText, type: ClearType, targetType: ClearType, forceEnabled: Boolean = false) {
        field.isEnabled = forceEnabled || type.stableId < targetType.stableId
        field.setText(if (field.isEnabled) "" else "0")
    }

    private fun completeEntry() {
        allFields.forEach { it.error = null }
        checkErrorForValue(score, field_score)
        checkErrorForValue(ex, field_ex)
        checkErrorForValue(misses, field_misses)
        checkErrorForValue(goods, field_goods)
        checkErrorForValue(greats, field_greats)
        checkErrorForValue(perfects, field_perfects)
        if (!life4app.settingsManager.getDebugFlag(KEY_DEBUG_ACCEPT_INVALID) &&
            allFields.any { it.visibility == VISIBLE && it.error != null }) {
            Toast.makeText(this, R.string.make_sure_fields_filled, Toast.LENGTH_SHORT).show()
        } else {
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(RESULT_DATA, result!!.also {
                    it.score = score
                    it.exScore = ex
                    if (requiresAdvancedDetail) {
                        it.misses = misses
                        it.goods = goods
                        it.greats = greats
                        it.perfects = perfects
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