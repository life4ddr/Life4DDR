package com.perrigogames.life4.android.ui.managerimport

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.perrigogames.life4.model.LadderImporter
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.databinding.DialogManagerImportProcessingBinding

/**
 * A custom Dialog class that prompts the user for a data string from an
 * external source to import into LIFE4.
 */
class ScoreManagerImportProcessingDialog(var listener: Listener? = null): DialogFragment() {

    lateinit var contentView: View
    lateinit var dialog: AlertDialog
    private lateinit var binding: DialogManagerImportProcessingBinding

    private var errors = 0
    private var shouldClose = true

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            binding = DialogManagerImportProcessingBinding.inflate(
                requireActivity().layoutInflater,
                null,
                false
            )
            contentView = binding.root
            binding.buttonClose.setOnClickListener {
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

    private val managerListener: LadderImporter.Listener get() = object: LadderImporter.Listener {
        override fun onCountUpdated(current: Int, total: Int) {
            binding.textProgress.text = "$current/$total (${(current.toDouble() / total * 100).toInt()}%)"
            binding.progressAmount.apply {
                max = total
                progress = current
            }
        }

        override fun onError(totalCount: Int, message: String) {
            binding.textErrorLog.append("$message\n----\n")
            errors = totalCount
            shouldClose = false
        }

        override fun onCompleted() {
            context?.let {
                if (!shouldClose) {
                    binding.lottieBackground.pauseAnimation()
                    binding.lottieBackground.visibility = View.GONE
                    binding.textProgress.text = if (errors > 0) {
                        getString(R.string.import_finished_errors, errors)
                    } else {
                        getString(R.string.import_finished)
                    }
                    binding.buttonClose.text = getString(R.string.close)
                } else {
                    dialog.dismiss()
                }
            }
        }
    }

    interface Listener {
        fun onDialogLoaded(managerListener: LadderImporter.Listener)
        fun onDialogCancelled()
    }
}