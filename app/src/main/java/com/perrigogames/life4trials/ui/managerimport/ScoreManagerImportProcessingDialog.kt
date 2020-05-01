package com.perrigogames.life4trials.ui.managerimport

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.perrigogames.life4trials.R
import com.perrigogames.life4.model.LadderManager.ManagerImportListener
import kotlinx.android.synthetic.main.dialog_manager_import_processing.view.*

/**
 * A custom Dialog class that prompts the user for a data string from an
 * external source to import into LIFE4.
 */
class ScoreManagerImportProcessingDialog(var listener: Listener? = null): DialogFragment() {

    lateinit var contentView: View
    lateinit var dialog: AlertDialog

    private var errors = 0
    private var shouldClose = true

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            contentView = requireActivity().layoutInflater.inflate(R.layout.dialog_manager_import_processing, null)
            contentView.button_close.setOnClickListener {
                listener?.onDialogCancelled()
                dialog.dismiss()
            }
            dialog = AlertDialog.Builder(it)
                .setView(contentView)
                .setCancelable(false)
                .create()
            contentView.post { listener?.onDialogLoaded(managerListener) }
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    val managerListener: ManagerImportListener get() = object: ManagerImportListener {
        override fun onCountUpdated(current: Int, total: Int) {
            contentView.text_progress.text = "$current/$total (${(current.toDouble() / total * 100).toInt()}%)"
            contentView.progress_amount.apply {
                max = total
                progress = current
            }
        }

        override fun onError(totalCount: Int, message: String) {
            contentView.text_error_log.append("$message\n----\n")
            errors = totalCount
            shouldClose = false
        }

        override fun onCompleted() {
            context?.let {
                if (!shouldClose) {
                    contentView.lottie_background.pauseAnimation()
                    contentView.lottie_background.visibility = View.GONE
                    contentView.text_progress.text = if (errors > 0) {
                        getString(R.string.import_finished_errors, errors)
                    } else {
                        getString(R.string.import_finished)
                    }
                    contentView.button_close.text = getString(R.string.close)
                } else {
                    dialog.dismiss()
                }
            }
        }
    }

    interface Listener {
        fun onDialogLoaded(managerListener: ManagerImportListener)
        fun onDialogCancelled()
    }
}