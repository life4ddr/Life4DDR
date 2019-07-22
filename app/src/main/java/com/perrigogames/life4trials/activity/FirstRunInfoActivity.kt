package com.perrigogames.life4trials.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_INFO_NAME
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_INFO_RIVAL_CODE
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_INFO_TWITTER_NAME
import com.perrigogames.life4trials.util.SharedPrefsUtil
import kotlinx.android.synthetic.main.activity_first_run_info.*

/**
 * An [AppCompatActivity] shown to the user when their initial stats are empty.
 */
class FirstRunInfoActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_run_info)

        field_rival_code.let { field ->
            field.doAfterTextChanged { text ->
                text?.let {
                    if (it.length == 5) {
                        val firstHalf = it.substring(0..3)
                        field.setText(
                            if (it[4] == '-') firstHalf
                            else "$firstHalf-${it[4]}")
                        field.setSelection(field.text.length)
                    }
                }
            }
        }

        field_twitter.let { field ->
            field.doAfterTextChanged { text ->
                text?.let {
                    if (it.length == 1 && it[0] != '@') {
                        field.setText("@$it")
                        field.setSelection(field.text.length)
                    }
                }
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
        SharedPrefsUtil.setUserString(this, KEY_INFO_NAME, field_name.text.toString())
        if (field_rival_code.text.isNotEmpty()) {
            SharedPrefsUtil.setUserString(this, KEY_INFO_RIVAL_CODE, field_rival_code.text.toString())
        }
        if (field_twitter.text.isNotEmpty()) {
            SharedPrefsUtil.setUserString(this, KEY_INFO_TWITTER_NAME, field_twitter.text.toString())
        }

        val placement = radio_method_placement.isChecked
        val skipRank = radio_method_no_rank.isChecked
        val launchActivity: Class<*> = when {
            placement -> PlacementListActivity::class.java
            skipRank -> PlayerProfileActivity::class.java
            else -> RankListActivity::class.java
        }

        startActivity(Intent(applicationContext, launchActivity))
        finish()
    }
}