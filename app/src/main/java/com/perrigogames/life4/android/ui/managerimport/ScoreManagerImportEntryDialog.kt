package com.perrigogames.life4.android.ui.managerimport

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.databinding.DialogManagerImportEntryBinding

/**
 * A custom Dialog class that prompts the user for a data string from an
 * external source to import into LIFE4.
 */
class ScoreManagerImportEntryDialog(var listener: Listener? = null): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val binding = DialogManagerImportEntryBinding.inflate(it.layoutInflater)
            AlertDialog.Builder(it)
                .setView(binding.root)
                .setNegativeButton(R.string.help) { _, _ -> listener?.onHelpPressed() }
                .setPositiveButton(R.string.okay) { _, _ -> listener?.onDataSubmitted(binding.fieldManagerData.text.toString().split("\n")) }
                .setOnCancelListener { listener?.onDialogCancelled() }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface Listener {
        fun onDialogCancelled()
        fun onHelpPressed()
        fun onDataSubmitted(data: List<String>)
    }

    companion object {
        const val TAG = "ImportEntryDialog"
    }
}