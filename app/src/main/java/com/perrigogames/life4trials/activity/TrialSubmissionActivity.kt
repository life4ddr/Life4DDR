package com.perrigogames.life4trials.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.TrialRank
import com.perrigogames.life4trials.data.TrialSession
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.util.NotificationUtil
import com.perrigogames.life4trials.util.SharedPrefsUtils
import com.perrigogames.life4trials.view.PathImageView
import com.perrigogames.life4trials.view.longNumberString
import kotlinx.android.synthetic.main.content_trial_submission.*


/**
 * Activity for confirming all of the photos taken for the
 */
class TrialSubmissionActivity: AppCompatActivity() {

    private val session: TrialSession by lazy {
        intent.extras?.getSerializable(ARG_SESSION) as TrialSession
    }

    private fun imageViewForIndex(index: Int?): PathImageView? = when(index) {
        0 -> image_song_1
        1 -> image_song_2
        2 -> image_song_3
        3 -> image_song_4
        else -> null
    }

    private fun difficultyViewForIndex(index: Int?): View? = when(index) {
        0 -> view_difficulty_1
        1 -> view_difficulty_2
        2 -> view_difficulty_3
        3 -> view_difficulty_4
        else -> null
    }

    private fun textViewForIndex(index: Int?): TextView? = when(index) {
        0 -> text_song_1
        1 -> text_song_2
        2 -> text_song_3
        3 -> text_song_4
        else -> null
    }

    private inline fun forEachResultImage(block: (Int, PathImageView) -> Unit) = (0..3).forEach { idx -> block(idx, imageViewForIndex(idx)!!) }
    private inline fun forEachResultDifficulty(block: (Int, View) -> Unit) = (0..3).forEach { idx -> block(idx, difficultyViewForIndex(idx)!!) }
    private inline fun forEachResultText(block: (Int, TextView) -> Unit) = (0..3).forEach { idx -> block(idx, textViewForIndex(idx)!!) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_trial_submission)

        image_desired_rank.rank = session.goalRank.parent
        text_ex_score.text = getString(R.string.ex_score_missing_string_format, session.totalExScore, session.missingExScore)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        image_trial_final.layoutParams.height = (displayMetrics.heightPixels * 0.4f).toInt()
        image_song_1.layoutParams.height = (displayMetrics.widthPixels / 4.3f).toInt()

        spinner_desired_rank.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, session.availableRanks)
        spinner_desired_rank.setSelection(session.availableRanks.indexOf(session.goalRank))
        spinner_desired_rank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setRank(session.availableRanks[position])
            }
        }

        session.goalSet?.let { set -> text_goals.text = set.generateSingleGoalString(resources, session.trial) }

        button_submit.setOnClickListener { submitResult() }

        forEachResultText { idx, textView ->
            session.results[idx]?.let { result ->
                textView.text = if (session.shouldShowAdvancedSongDetails) {
                    getString(R.string.score_string_summary_format_advanced,
                        result.score?.longNumberString(), result.exScore, result.misses, result.badJudges)
                } else {
                    getString(R.string.score_string_summary_format, result.score?.longNumberString(), result.exScore)
                }
                if (!result.passed) {
                    textView.setTextColor(ResourcesCompat.getColor(resources, R.color.orange, theme))
                }
            }
        }

        forEachResultDifficulty { idx, view ->
            session.results[idx]?.let { result ->
                view.setBackgroundColor(ContextCompat.getColor(this, result.song.difficultyClass.colorRes))
            }
        }

        image_trial_final.rootView.addOnLayoutChangeListener(object: View.OnLayoutChangeListener {

            override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int,
                oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {

                image_trial_final.uri = session.finalPhotoUri
                forEachResultImage { idx, imageView ->
                    imageView.uri = session.results[idx]?.photoUri
                }
                image_trial_final.rootView.removeOnLayoutChangeListener(this)
            }
        })
    }

    override fun onBackPressed() {
        android.app.AlertDialog.Builder(this).setTitle(R.string.are_you_sure)
            .setMessage(R.string.trial_not_saved)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.okay) { _, _ -> finish() }
            .show()
    }

    private fun setRank(rank: TrialRank) {
        session.goalRank = rank
        image_desired_rank.rank = rank.parent
        text_goals.text = session.goalSet?.generateSingleGoalString(resources, session.trial)
    }

    private fun submitResult() {
        if (session.results.any { it?.passed != true }) {
            submitRankAndFinish(false)
        } else {
            AlertDialog.Builder(this)
                .setTitle(R.string.submit_dialog_title)
                .setMessage(resources.getString(R.string.submit_dialog_rank_confirmation, session.goalRank.toString()))
                .setPositiveButton(R.string.yes) { _, _ -> submitRankAndFinish(true) }
                .setNegativeButton(R.string.no) { _, _ -> submitRankAndFinish(false) }
                .show()
        }
    }

    private fun submitRankAndFinish(passed: Boolean) {
        session.goalObtained = passed
        life4app.trialManager.saveRecord(session)
        if (passed) {
            AlertDialog.Builder(this)
                .setTitle(R.string.submit_dialog_title)
                .setMessage(R.string.submit_dialog_prompt)
                .setNegativeButton(R.string.no) { _, _ -> finish() }
                .setPositiveButton(R.string.yes) { _, _ ->
                    if (SharedPrefsUtils.getUserFlag(this, SettingsActivity.KEY_SUBMISSION_NOTIFICAION, false)) {
                        NotificationUtil.showUserInfoNotifications(this, session.totalExScore)
                    }
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_submission_form))))
                    finish()
                }
                .show()
        } else {
            finish()
        }
    }

    companion object {
        const val ARG_SESSION = "ARG_SESSION"
    }
}