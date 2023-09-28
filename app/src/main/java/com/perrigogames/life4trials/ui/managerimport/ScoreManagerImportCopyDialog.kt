package com.perrigogames.life4trials.ui.managerimport

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.perrigogames.life4trials.R

class ScoreManagerImportCopyDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater = requireActivity().layoutInflater
            AlertDialog.Builder(it)
                .setPositiveButton(R.string.okay) { _, _ -> }
                .setView(inflater.inflate(R.layout.dialog_manager_import_copy_code, null))
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}