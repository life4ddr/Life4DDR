package com.perrigogames.life4trials.activity

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.TrialSession
import com.perrigogames.life4trials.event.SavedRankUpdatedEvent
import com.perrigogames.life4trials.util.SharedPrefsUtils
import kotlinx.android.synthetic.main.content_trial_submission.*

class TrialSubmissionActivity: AppCompatActivity() {

    private val session: TrialSession?
        get() = intent.extras?.getSerializable(ARG_SESSION) as TrialSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_trial_submission)

        session!!.let {
            image_trial_final.path = it.finalPhoto
            image_song_1.path = it.results[0]?.photoPath
            image_song_2.path = it.results[1]?.photoPath
            image_song_3.path = it.results[2]?.photoPath
            image_song_4.path = it.results[3]?.photoPath

            image_desired_rank.rank = it.goalRank
            text_desired_rank.text = it.goalRank.toString()

            it.goalSet?.let { set -> text_goals.text = set.generateSingleGoalString(resources, it.trial) }
        }

        button_submit.setOnClickListener { submitResult() }
    }

    private fun submitResult() {
        AlertDialog.Builder(this)
            .setTitle(R.string.submit_dialog_title)
            .setMessage(resources.getString(R.string.submit_dialog_rank_confirmation, session!!.goalRank.toString()))
            .setPositiveButton(R.string.yes) { _, _ -> submitRankAndFinish(true) }
            .setNegativeButton(R.string.no) { _, _ -> submitRankAndFinish(false) }
            .show()
    }

    private fun submitRankAndFinish(passed: Boolean) {
        if (passed) {
            SharedPrefsUtils.setRankForTrial(this, session!!.trial, session!!.goalRank)
            Life4Application.eventBus.post(SavedRankUpdatedEvent(session!!.trial))
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.submit_dialog_title)
            .setMessage(R.string.submit_dialog_content)
            .setPositiveButton(R.string.okay) { _, _ -> finish() }
            .show()
    }

    companion object {
        const val ARG_SESSION = "ARG_SESSION"
    }
}