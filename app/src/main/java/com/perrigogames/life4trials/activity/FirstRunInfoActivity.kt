package com.perrigogames.life4trials.activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.perrigogames.life4.MajorUpdateProcessEvent
import com.perrigogames.life4.SettingsKeys.KEY_INFO_NAME
import com.perrigogames.life4.SettingsKeys.KEY_INFO_RIVAL_CODE
import com.perrigogames.life4.SettingsKeys.KEY_INFO_TWITTER_NAME
import com.perrigogames.life4.data.ApiPlayer
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.manager.FirstRunManager
import com.perrigogames.life4trials.manager.LadderManager
import com.perrigogames.life4trials.manager.PlayerManager
import com.perrigogames.life4trials.manager.SettingsManager
import com.perrigogames.life4trials.util.visibilityBool
import com.perrigogames.life4trials.view.PlayerFoundView
import kotlinx.android.synthetic.main.activity_first_run_info.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * An [AppCompatActivity] shown to the user when their initial stats are empty.
 */
class FirstRunInfoActivity: AppCompatActivity(), KoinComponent {

    private val firstRunManager: FirstRunManager by inject()
    private val ladderManager: LadderManager by inject()
    private val playerManager: PlayerManager by inject()
    private val settingsManager: SettingsManager by inject()
    private val eventBus: EventBus by inject()

    private var lastNameCheck: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_run_info)

        field_name.setText(settingsManager.getUserString(KEY_INFO_NAME, ""))
        field_rival_code.setText(settingsManager.getUserString(KEY_INFO_RIVAL_CODE, ""))
        field_twitter.setText(settingsManager.getUserString(KEY_INFO_TWITTER_NAME, ""))

        field_name.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                onNameFinished(field_name.text.toString())
            }
        }
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

    override fun onStart() {
        super.onStart()
        eventBus.register(this)
    }

    override fun onStop() {
        super.onStop()
        eventBus.unregister(this)
    }

    private fun onNameFinished(name: String) {
        if (lastNameCheck == null || lastNameCheck != name) {
            lastNameCheck = name
            playerManager.importPlayerInfo(name)
            progress_name.visibilityBool = true
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayerImported(e: PlayerManager.PlayerImportedEvent) {
        progress_name.visibilityBool = false
        if (lastNameCheck != null && e.apiPlayer?.name == lastNameCheck) {
            (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            val content = layoutInflater.inflate(R.layout.view_player_found, null, false) as PlayerFoundView
            content.apiPlayer = e.apiPlayer

            AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.player_found)
                .setView(content)
                .setPositiveButton(R.string.yes) { _, _ -> onAcceptImportedPlayer(e.apiPlayer!!) }
                .setNegativeButton(R.string.no) { _, _ ->
                    field_name.setText("")
                    field_name.requestFocus()
                }.show()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMajorVersion(e: MajorUpdateProcessEvent) {
        eventBus.removeStickyEvent(e)
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

        startActivity(launchIntent)
        finish()
    }

    private fun onAcceptImportedPlayer(player: ApiPlayer) {
        field_name.error = null
        firstRunManager.setUserBasics(player.name, player.playerRivalCode, player.twitterHandle)
        ladderManager.setUserRank(player.rank)
        startActivity(firstRunManager.finishProcessIntent)
        finish()
    }

    //TODO this can be a utility function elsewhere too
    private inline fun EditText.onFieldChanged(crossinline block: (EditText, Editable) -> Unit) = this.let { field ->
        field.doAfterTextChanged { text ->
            text?.let { block(this, text) }
        }
    }
}
