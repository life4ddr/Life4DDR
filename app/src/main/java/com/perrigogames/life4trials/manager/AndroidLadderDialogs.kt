package com.perrigogames.life4trials.manager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.perrigogames.life4.LadderDialogs
import com.perrigogames.life4.Notifications
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.ui.managerimport.ScoreManagerImportDirectionsDialog
import com.perrigogames.life4trials.ui.managerimport.ScoreManagerImportEntryDialog
import com.perrigogames.life4trials.ui.managerimport.ScoreManagerImportProcessingDialog
import com.russhwolf.settings.Settings
import org.koin.core.inject

class AndroidLadderDialogs: LadderDialogs {

    private val context: Context by inject()
    private val notifications: Notifications by inject()
    private val ladderManager: LadderManager by inject()
    override val settings: Settings by inject()

    private lateinit var activity: FragmentActivity

    fun showImportFlow(activity: FragmentActivity) {
        this.activity = activity
        super.showImportFlow()
    }

    override fun showImportDirectionsDialog() {
        ScoreManagerImportDirectionsDialog(object: ScoreManagerImportDirectionsDialog.Listener {
            override fun onDialogCancelled() = Unit
            override fun onCopyAndContinue() {
                Toast.makeText(activity, context.getString(R.string.copied), Toast.LENGTH_SHORT).show()
                (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip =
                    ClipData.newPlainText("LIFE4 Data", context.getString(R.string.import_data_format))
                showImportEntryDialog()
            }
        }).show(activity.supportFragmentManager, ScoreManagerImportDirectionsDialog.TAG)
    }

    override fun showImportEntryDialog() {
        val intent = activity.packageManager.getLaunchIntentForPackage("jp.linanfine.dsma")
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)//null pointer check in case package name was not found
        } else {
            Toast.makeText(activity, context.getString(R.string.no_ddra_manager), Toast.LENGTH_SHORT).show()
        }

        ScoreManagerImportEntryDialog(object : ScoreManagerImportEntryDialog.Listener {
            override fun onDialogCancelled() = Unit
            override fun onHelpPressed() = showImportDirectionsDialog()
            override fun onDataSubmitted(data: String) = showImportProcessingDialog(data)
        }).show(activity.supportFragmentManager, ScoreManagerImportEntryDialog.TAG)
    }

    override fun showImportProcessingDialog(dataString: String) {
        val dialog = ScoreManagerImportProcessingDialog(object : ScoreManagerImportProcessingDialog.Listener {
            override fun onDialogLoaded(managerListener: LadderManager.ManagerImportListener) = ladderManager.importManagerData(dataString, managerListener)
            override fun onDialogCancelled() = ladderManager.cancelImportJob()
        })
        dialog.show(activity.supportFragmentManager, ScoreManagerImportEntryDialog.TAG)
    }

    override fun onClearGoalStates(positive: () -> Unit) =
        displayAreYouSureDialog(R.string.confirm_erase_trial_data, positive)

    override fun onClearSongResults(positive: () -> Unit) =
        displayAreYouSureDialog(R.string.confirm_erase_result_data, positive)

    override fun onRefreshSongDatabase(positive: () -> Unit) =
        displayAreYouSureDialog(R.string.confirm_refresh_song_db, positive)

    override fun showLadderUpdateToast() = notifications.showToast(context.getString(R.string.ranks_updated))

    override fun showImportFinishedToast() = notifications.showToast(context.getString(R.string.import_finished))

    private inline fun displayAreYouSureDialog(@StringRes messageText: Int, crossinline positive: () -> Unit) {
        AlertDialog.Builder(context)
            .setTitle(R.string.are_you_sure)
            .setMessage(messageText)
            .setPositiveButton(R.string.yes) { _, _ -> positive() }
            .setNegativeButton(R.string.no, null)
            .show()
    }
}