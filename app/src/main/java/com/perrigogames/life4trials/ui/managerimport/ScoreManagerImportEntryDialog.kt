package com.perrigogames.life4trials.ui.managerimport

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.perrigogames.life4trials.R
import kotlinx.android.synthetic.main.dialog_manager_import_entry.view.*

/**
 * A custom Dialog class that prompts the user for a data string from an
 * external source to import into LIFE4.
 */
class ScoreManagerImportEntryDialog(var listener: Listener? = null): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val view = requireActivity().layoutInflater.inflate(R.layout.dialog_manager_import_entry, null)
            AlertDialog.Builder(it)
                .setView(view)
                .setPositiveButton(R.string.okay) { _, _ -> listener?.onDataSubmitted(view.field_manager_data.text.toString()) }
                .setOnCancelListener { listener?.onDialogCancelled() }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface Listener {
        fun onDialogCancelled()
        fun onDataSubmitted(data: String)
    }

    companion object {
        const val TAG = "ImportEntryDialog"
    }
}