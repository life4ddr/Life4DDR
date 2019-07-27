package com.perrigogames.life4trials.activity

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.util.SharedPrefsUtil
import com.perrigogames.life4trials.util.SharedPrefsUtil.KEY_INIT_STATE
import com.perrigogames.life4trials.util.SharedPrefsUtil.VAL_INIT_STATE_PLACEMENTS
import com.perrigogames.life4trials.util.SharedPrefsUtil.VAL_INIT_STATE_RANKS
import kotlinx.android.synthetic.main.activity_first_run_info.*

/**
 * An [AppCompatActivity] shown to the user when their initial stats are empty.
 */
class FirstRunInfoActivity: AppCompatActivity() {

    private val firstRunManager get() = life4app.firstRunManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_run_info)

        field_rival_code.onFieldChanged { field, text ->
            if (text.length == 5) {
                val firstHalf = text.substring(0..3)
                field.setText(
                    if (text[4] == '-') firstHalf
                    else "$firstHalf-${text[4]}")
                field.setSelection(field.text.length)
            }
        }
        field_twitter.onFieldChanged { field, text ->
            if (text.length == 1 && text[0] != '@') {
                field.setText("@$text")
                field.setSelection(field.text.length)
            }
        }

        radio_method_placement.isChecked = true
    }

    fun onSignInClicked(v: View) {
        if (field_name.text.isEmpty()) {
            field_name.error = getString(R.string.error_name_required)
            return
        }

        field_name.error = null
        firstRunManager.setUserBasics(
            field_name.text.toString(),
            field_rival_code.text.toString(),
            field_twitter.text.toString())

        val placement = radio_method_placement.isChecked
        val rankList = radio_method_selection.isChecked
        val launchIntent = when {
            placement -> firstRunManager.placementIntent
            rankList -> firstRunManager.rankListIntent
            else -> firstRunManager.finishProcessIntent
        }

        if (placement) {
            SharedPrefsUtil.setUserString(this, KEY_INIT_STATE, VAL_INIT_STATE_PLACEMENTS)
        } else if (rankList) {
            SharedPrefsUtil.setUserString(this, KEY_INIT_STATE, VAL_INIT_STATE_RANKS)
        }

        startActivity(launchIntent)
        finish()
    }

    //TODO this can be a utility function elsewhere too
    private inline fun EditText.onFieldChanged(crossinline block: (EditText, Editable) -> Unit) = this.let { field ->
        field.doAfterTextChanged { text ->
            text?.let { block(this, text) }
        }
    }
}