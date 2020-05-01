package com.perrigogames.life4trials.manager

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.perrigogames.life4.TrialDialogs
import com.perrigogames.life4.data.TrialRank
import com.perrigogames.life4trials.R
import org.koin.core.inject


class AndroidTrialDialogs: TrialDialogs {

    private val context: Context by inject()

    override fun showRankConfirmation(rank: TrialRank, result: (Boolean) -> Unit) {
        showYesNoDialog(R.string.trial_submit_dialog_title,
            message = context.getString(R.string.trial_submit_dialog_rank_confirmation, rank.toString()),
            result = result)
    }

    override fun showSessionSubmitConfirmation(result: (Boolean) -> Unit) {
        showYesNoDialog(R.string.trial_submit_dialog_title, R.string.trial_submit_dialog_prompt, false, result)
    }

    override fun showTrialSubmissionWeb() {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.url_trial_submission_form))))
    }

    private fun showYesNoDialog(@StringRes titleRes: Int,
                                @StringRes messageRes: Int? = null,
                                cancelable: Boolean = true,
                                result: (Boolean) -> Unit,
                                message: String = messageRes?.let { context.getString(it) } ?: "") =
        AlertDialog.Builder(context)
            .setTitle(titleRes)
            .setMessage(message)
            .setCancelable(cancelable)
            .setPositiveButton(R.string.yes) { _, _ -> result(true) }
            .setNegativeButton(R.string.no) { _, _ -> result(false) }
            .show()
}
