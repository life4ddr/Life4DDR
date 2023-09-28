package com.perrigogames.life4.android.manager

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.perrigogames.life4.LadderDialogs
import com.perrigogames.life4.Notifications
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.ui.dialogs.ScoreManagerImportProcessingDialog
import com.perrigogames.life4.model.LadderImporter
import com.perrigogames.life4.model.LadderImporter.OpMode.SA
import com.russhwolf.settings.Settings
import org.koin.core.KoinComponent
import org.koin.core.inject

class AndroidLadderDialogs: LadderDialogs, KoinComponent {

    private val context: Context by inject()
    private val notifications: Notifications by inject()
    override val settings: Settings by inject()

    private var activity: FragmentActivity? = null

    fun handleSkillAttackImport(activity: FragmentActivity, data: List<String>?) {
        if (!data.isNullOrEmpty()) {
            this.activity = activity
            showImportProcessingDialog(data, SA)
        }
    }

    override fun showImportProcessingDialog(dataLines: List<String>, opMode: LadderImporter.OpMode) {
        val importer = LadderImporter(dataLines, opMode)
        val dialog = ScoreManagerImportProcessingDialog(object : ScoreManagerImportProcessingDialog.Listener {
            override fun onDialogLoaded(managerListener: LadderImporter.Listener) = importer.start(managerListener)
            override fun onDialogCancelled() = importer.cancel()
        })
        dialog.show(activity!!.supportFragmentManager, ScoreManagerImportProcessingDialog.TAG)
    }

    override fun onClearGoalStates(positive: () -> Unit) =
        displayAreYouSureDialog(R.string.confirm_erase_goal_data, positive)

    override fun onClearSongResults(positive: () -> Unit) =
        displayAreYouSureDialog(R.string.confirm_erase_result_data, positive)

    override fun onRefreshSongDatabase(positive: () -> Unit) =
        displayAreYouSureDialog(R.string.confirm_refresh_song_db, positive)

    override fun showLadderUpdateToast() = notifications.showToast(context.getString(R.string.ranks_updated))

    override fun showImportFinishedToast() = notifications.showToast(context.getString(R.string.import_finished))

    fun withActivity(activity: FragmentActivity, block: (AndroidLadderDialogs) -> Unit) {
        this.activity = activity
        block.invoke(this)
        this.activity = null
    }

    private inline fun displayAreYouSureDialog(@StringRes messageText: Int, crossinline positive: () -> Unit) {
        AlertDialog.Builder(activity!!)
            .setTitle(R.string.are_you_sure)
            .setMessage(messageText)
            .setPositiveButton(R.string.yes) { _, _ -> positive() }
            .setNegativeButton(R.string.no, null)
            .show()
    }
}