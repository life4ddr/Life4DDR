package com.perrigogames.life4.android.manager

import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.perrigogames.life4.TrialNavigation
import com.perrigogames.life4.enums.TrialRank
import com.perrigogames.life4.android.R


class AndroidTrialNavigation: TrialNavigation() {

    private var activity: FragmentActivity? = null

    fun submitResult(activity: FragmentActivity, onFinish: () -> Unit) {
        this.activity = activity
        super.submitResult {
            this.activity = null
            onFinish()
        }
    }

    override fun showRankConfirmation(rank: TrialRank, result: (Boolean) -> Unit) {
        showYesNoDialog(R.string.trial_submit_dialog_title,
            message = activity!!.getString(R.string.trial_submit_dialog_rank_confirmation, rank.toString()),
            result = result)
    }

    override fun showSessionSubmitConfirmation(result: (Boolean) -> Unit) {
        showYesNoDialog(R.string.trial_submit_dialog_title, R.string.trial_submit_dialog_prompt, false, result)
    }

    override fun showTrialSubmissionWeb() {
        activity!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(activity!!.getString(R.string.url_trial_submission_form))))
    }

    private fun showYesNoDialog(@StringRes titleRes: Int,
                                @StringRes messageRes: Int? = null,
                                cancelable: Boolean = true,
                                result: (Boolean) -> Unit,
                                message: String = messageRes?.let { activity!!.getString(it) } ?: "") =
        AlertDialog.Builder(activity!!)
            .setTitle(titleRes)
            .setMessage(message)
            .setCancelable(cancelable)
            .setPositiveButton(R.string.yes) { _, _ -> result(true) }
            .setNegativeButton(R.string.no) { _, _ -> result(false) }
            .show()
}
