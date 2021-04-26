package com.perrigogames.life4.android.ui.managerimport

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.perrigogames.life4.SettingsKeys.KEY_IMPORT_SKIP_DIRECTIONS
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.databinding.DialogManagerImportDirectionsBinding
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import org.koin.core.KoinComponent
import org.koin.core.inject

class ScoreManagerImportDirectionsDialog(var listener: Listener? = null): DialogFragment(), KoinComponent {

    private val settings: Settings by inject()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireActivity().let {
            val binding = DialogManagerImportDirectionsBinding.inflate(it.layoutInflater)
            binding.checkDontShow.setOnCheckedChangeListener { _, checked ->
                settings[KEY_IMPORT_SKIP_DIRECTIONS] = checked
            }
            AlertDialog.Builder(it)
                .setView(binding.root)
                .setPositiveButton(R.string.copy_and_continue) { _, _ -> listener?.onCopyAndContinue() }
                .setOnCancelListener { listener?.onDialogCancelled() }
                .create()
        }
    }

    interface Listener {
        fun onDialogCancelled()
        fun onCopyAndContinue()
    }

    companion object {
        const val TAG = "ImportEntryDialog"
    }
}
