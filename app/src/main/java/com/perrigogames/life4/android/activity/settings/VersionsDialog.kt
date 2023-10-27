package com.perrigogames.life4.android.activity.settings

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.perrigogames.life4.android.BuildConfig
import com.perrigogames.life4.android.databinding.ActivityVersionsBinding
import com.perrigogames.life4.model.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class VersionsDialog: DialogFragment(), KoinComponent {

    lateinit var dialog: AlertDialog
    private lateinit var binding: ActivityVersionsBinding

    private val ignoreListManager: IgnoreListManager by inject()
    private val ladderDataManager: LadderDataManager by inject()
    private val motdManager: MotdManager by inject()
    private val songDataManager: SongDataManager by inject()
    private val trialManager: TrialManager by inject()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = ActivityVersionsBinding.inflate(layoutInflater)
        val textContent = """
            App version: <b>${BuildConfig.VERSION_NAME}</b><br>
            Ignore list version: <b>${ignoreListManager.dataVersionString}</b><br>
            Ladder data version: <b>${ladderDataManager.dataVersionString}</b><br>
            MOTD version: <b>${motdManager.dataVersionString}</b><br>
            Song list version: <b>${songDataManager.dataVersionString}</b><br>
            Trial data version: <b>${trialManager.dataVersionString}</b>
        """.trimIndent()
        binding.textVersions.text = HtmlCompat.fromHtml(textContent, HtmlCompat.FROM_HTML_MODE_LEGACY)
        dialog = AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setCancelable(false)
            .create()
        return dialog
    }

    companion object {
        const val TAG = "VersionsDialog"
    }
}