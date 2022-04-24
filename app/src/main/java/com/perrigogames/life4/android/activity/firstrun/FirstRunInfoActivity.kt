package com.perrigogames.life4.android.activity.firstrun

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4.PlayerImportedEvent
import com.perrigogames.life4.SettingsKeys.KEY_INFO_NAME
import com.perrigogames.life4.SettingsKeys.KEY_INFO_RIVAL_CODE
import com.perrigogames.life4.SettingsKeys.KEY_INFO_TWITTER_NAME
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.databinding.ActivityFirstRunInfoBinding
import com.perrigogames.life4.android.manager.finishProcessIntent
import com.perrigogames.life4.android.manager.placementIntent
import com.perrigogames.life4.android.manager.rankListIntent
import com.perrigogames.life4.android.util.onFieldChanged
import com.perrigogames.life4.android.util.visibilityBool
import com.perrigogames.life4.android.view.PlayerFoundView
import com.perrigogames.life4.data.ApiPlayer
import com.perrigogames.life4.model.FirstRunManager
import com.perrigogames.life4.model.LadderManager
import com.perrigogames.life4.model.PlayerManager
import com.russhwolf.settings.Settings
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * An [AppCompatActivity] shown to the user when their initial stats are empty.
 */
class FirstRunInfoActivity: AppCompatActivity(), KoinComponent {

    private val firstRunManager: FirstRunManager by inject()
    private val ladderManager: LadderManager by inject()
    private val playerManager: PlayerManager by inject()
    private val settings: Settings by inject()
    private val eventBus: EventBus by inject()

    private lateinit var binding: ActivityFirstRunInfoBinding

    private var lastNameCheck: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstRunInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fieldName.setText(settings.getString(KEY_INFO_NAME, ""))
        binding.fieldRivalCode.setText(settings.getString(KEY_INFO_RIVAL_CODE, ""))
        binding.fieldTwitter.setText(settings.getString(KEY_INFO_TWITTER_NAME, ""))

        binding.fieldName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                onNameFinished(binding.fieldName.text.toString())
            }
        }
        binding.fieldRivalCode.onFieldChanged { field, text ->
            if (text.length == 5) {
                val firstHalf = text.substring(0..3)
                field.setText(
                    if (text[4] == '-') firstHalf
                    else "$firstHalf-${text[4]}")
                field.setSelection(field.text.length)
            }
        }
        binding.fieldTwitter.onFieldChanged { field, text ->
            if (text.length == 1 && text[0] != '@') {
                field.setText("@$text")
                field.setSelection(field.text.length)
            }
        }

        binding.buttonContinue.setOnClickListener { onSignInClicked() }

        binding.radioMethodPlacement.isChecked = true
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
            binding.progressName.visibilityBool = true
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayerImported(e: PlayerImportedEvent) {
        binding.progressName.visibilityBool = false
        if (lastNameCheck != null && e.apiPlayer?.name == lastNameCheck) {
            (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            val content = PlayerFoundView(this)
            content.apiPlayer = e.apiPlayer

            AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.player_found)
                .setView(content)
                .setPositiveButton(R.string.yes) { _, _ -> onAcceptImportedPlayer(e.apiPlayer!!) }
                .setNegativeButton(R.string.no) { _, _ ->
                    binding.fieldName.setText("")
                    binding.fieldName.requestFocus()
                }.show()
        }
    }

    fun onSignInClicked() {
        if (binding.fieldName.text.isEmpty()) {
            binding.fieldName.error = getString(R.string.error_name_required)
            return
        }

        binding.fieldName.error = null
        firstRunManager.setUserBasics(
            binding.fieldName.text.toString(),
            binding.fieldRivalCode.text.toString(),
            binding.fieldTwitter.text.toString())

        val placement = binding.radioMethodPlacement.isChecked
        val rankList = binding.radioMethodSelection.isChecked
        val launchIntent = when {
            placement -> firstRunManager.placementIntent(this)
            rankList -> firstRunManager.rankListIntent(this)
            else -> firstRunManager.finishProcessIntent(this)
        }

        startActivity(launchIntent)
        finish()
    }

    private fun onAcceptImportedPlayer(player: ApiPlayer) {
        binding.fieldName.error = null
        firstRunManager.setUserBasics(player.name, player.playerRivalCode, player.twitterHandle)
        ladderManager.setUserRank(player.rank)
        startActivity(firstRunManager.finishProcessIntent(this))
        finish()
    }
}
