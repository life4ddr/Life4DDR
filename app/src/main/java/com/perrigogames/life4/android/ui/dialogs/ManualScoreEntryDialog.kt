package com.perrigogames.life4.android.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import com.perrigogames.life4.android.view.compose.ManualScoreInput
import org.koin.core.component.KoinComponent

class ManualScoreEntryDialog: DialogFragment(), KoinComponent {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val composeView = ComposeView(requireActivity())
        composeView.setContent {
            ManualScoreInput()
        }
        return AlertDialog.Builder(requireActivity())
            .setView(composeView)
            .setCancelable(false)
            .create()
    }
}