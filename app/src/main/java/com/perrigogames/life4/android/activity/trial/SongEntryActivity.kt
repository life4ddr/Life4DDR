package com.perrigogames.life4.android.activity.trial

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
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
import com.perrigogames.life4.SettingsKeys.KEY_DEBUG_ACCEPT_INVALID
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.databinding.ContentSongEntryBinding
import com.perrigogames.life4.android.photoUri
import com.perrigogames.life4.android.util.visibilityBool
import com.perrigogames.life4.data.Song
import com.perrigogames.life4.data.SongResult
import com.perrigogames.life4.data.TrialData
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.ClearType.*
import com.perrigogames.life4.getDebugBoolean
import com.perrigogames.life4.model.TrialSessionManager
import com.russhwolf.settings.Settings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class SongEntryActivity: AppCompatActivity(), KoinComponent {

    private lateinit var binding: ContentSongEntryBinding

    private val trialSessionManager: TrialSessionManager by inject()
    private val settings: Settings by inject()
    private val currentSession get() = trialSessionManager.currentSession!!

    val result: SongResult? get() = currentSession.results[songIndex]
    val song: Song get() = currentSession.trial.songs[songIndex]

    private val songIndex: Int get() = intent?.extras?.getInt(ARG_SONG_INDEX) ?: 0

    private val requiresAdvancedDetail: Boolean get() =
        intent?.extras?.getSerializable(ARG_ADVANCED_DETAIL) as? Boolean ?: false

    val newEntry: Boolean get() = result?.score == null
    var modified: Boolean = false
    private var advancedFieldVisibility: Boolean = false
        set(v) {
            field = v
            advancedFields.forEach { it.visibilityBool = v }
        }

    // Lists of fields for easy iteration
    private val advancedFields: List<EditText> by lazy {
        listOf(
            binding.fieldMisses,
            binding.fieldGoods,
            binding.fieldGreats,
            binding.fieldPerfects
        )
    }
    private val allFields: List<EditText> by lazy {
        listOf(
            binding.fieldScore,
            binding.fieldEx
        ) + advancedFields
    }

    // Entered field values
    private val score: Int? get() = try { binding.fieldScore.text.toString().toInt() } catch (e: NumberFormatException) { null }
    private val ex: Int? get() = try { binding.fieldEx.text.toString().toInt() } catch (e: NumberFormatException) { null }
    private val misses: Int? get() = try { binding.fieldMisses.text.toString().toInt() } catch (e: NumberFormatException) { null }
    private val goods: Int? get() = try { binding.fieldGoods.text.toString().toInt() } catch (e: NumberFormatException) { null }
    private val greats: Int? get() = try { binding.fieldGreats.text.toString().toInt() } catch (e: NumberFormatException) { null }
    private val perfects: Int? get() = try { binding.fieldPerfects.text.toString().toInt() } catch (e: NumberFormatException) { null }

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
                binding.fieldScore.setText((TrialData.MAX_SCORE - (intVal * TrialData.SCORE_PENALTY_PERFECT)).toString())
                binding.fieldEx.setText((song.ex - intVal).toString())
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentSongEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val res = result?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it.photoUri)
            binding.imagePhoto.setImageDrawable(BitmapDrawable(resources, bitmap))
            binding.buttonRetake.setOnClickListener { retakePhoto() }
            binding.buttonDone.setOnClickListener { completeEntry() }

            binding.buttonClear.setOnClickListener { clearType = CLEAR }
            binding.buttonFc.isEnabled = requiresAdvancedDetail
            binding.buttonFc.setOnClickListener { clearType = GOOD_FULL_COMBO }
            binding.buttonPfc.setOnClickListener { clearType = PERFECT_FULL_COMBO }
            binding.buttonMfc.setOnClickListener { clearType = MARVELOUS_FULL_COMBO }

            binding.checkboxPassed.isChecked = it.passed

            if (it.score != null) {
                binding.fieldScore.text = SpannableStringBuilder(it.score.toString())
            }
            if (it.exScore != null) {
                binding.fieldEx.text = SpannableStringBuilder(it.exScore.toString())
            }
            if (it.misses != null) {
                binding.fieldMisses.text = SpannableStringBuilder(it.misses.toString())
            }
            if (it.goods != null) {
                binding.fieldGoods.text = SpannableStringBuilder(it.goods.toString())
            }
            if (it.greats != null) {
                binding.fieldGreats.text = SpannableStringBuilder(it.greats.toString())
            }
            if (it.perfects != null) {
                binding.fieldPerfects.text = SpannableStringBuilder(it.perfects.toString())
            }
            binding.fieldScore.addTextChangedListener(textWatcher)
            binding.fieldEx.addTextChangedListener(textWatcher)

            binding.fieldPerfects.addTextChangedListener(perfectTextWatcher)

            advancedFieldVisibility = requiresAdvancedDetail

            if (binding.fieldScore.requestFocus()) {
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .showSoftInput(binding.fieldScore, InputMethodManager.SHOW_IMPLICIT)
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
                binding.fieldPerfects.requestFocus()
            } else {
                advancedFieldVisibility = requiresAdvancedDetail
            }
            updateFieldEditable(binding.fieldScore, type, PERFECT_FULL_COMBO)
            updateFieldEditable(binding.fieldEx, type, PERFECT_FULL_COMBO)
            updateFieldEditable(binding.fieldMisses, type, GOOD_FULL_COMBO)
            updateFieldEditable(binding.fieldGoods, type, GREAT_FULL_COMBO)
            updateFieldEditable(binding.fieldGreats, type, PERFECT_FULL_COMBO)
            updateFieldEditable(binding.fieldPerfects, type, MARVELOUS_FULL_COMBO, type == PERFECT_FULL_COMBO)
            binding.fieldEx.nextFocusDownId = when (type) {
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
        checkErrorForValue(score, binding.fieldScore)
        checkErrorForValue(ex, binding.fieldEx)
        if (!settings.getDebugBoolean(KEY_DEBUG_ACCEPT_INVALID) &&
            allFields.any { it.visibility == VISIBLE && it.error != null }) {
            Toast.makeText(this, R.string.make_sure_fields_filled, Toast.LENGTH_SHORT).show()
        } else {
            result!!.also {
                it.score = score
                it.exScore = ex
                if (advancedFieldVisibility) {
                    it.misses = misses
                    it.goods = goods
                    it.greats = greats
                    it.perfects = perfects
                }
                it.passed = binding.checkboxPassed.isChecked
            }
            setResult(Activity.RESULT_OK, intent)
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

        const val ARG_SONG_INDEX = "ARG_SONG"
        const val ARG_ADVANCED_DETAIL = "ARG_ADVANCED_DETAIL"

        const val RESULT_DATA = "RESULT_DATA"
    }
}
