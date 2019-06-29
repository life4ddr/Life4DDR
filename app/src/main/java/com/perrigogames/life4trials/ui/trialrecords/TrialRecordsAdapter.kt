package com.perrigogames.life4trials.ui.trialrecords

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_RECORDS_REMAINING_EX
import com.perrigogames.life4trials.db.TrialSessionDB
import com.perrigogames.life4trials.manager.TrialManager
import com.perrigogames.life4trials.ui.trialrecords.TrialRecordsFragment.OnRecordsListInteractionListener
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.SharedPrefsUtil
import com.perrigogames.life4trials.util.locale
import com.perrigogames.life4trials.view.RankImageView
import com.perrigogames.life4trials.view.longNumberString
import kotlinx.android.synthetic.main.item_trial_record.view.*

/**
 * [RecyclerView.Adapter] that can display a [TrialSessionDB] and makes a call to the
 * specified [OnRecordsListInteractionListener].
 */
class TrialRecordsAdapter(private val trialManager: TrialManager,
                          private val mListener: OnRecordsListInteractionListener?) :
    RecyclerView.Adapter<TrialRecordsAdapter.ViewHolder>() {

    private lateinit var recordsList: List<TrialSessionDB>
    private val mOnClickListener: View.OnClickListener

    init {
        refreshTrials()
        mOnClickListener = View.OnClickListener { v ->
            mListener?.onRecordsListInteraction(v.tag as TrialSessionDB)
        }
    }

    fun refreshTrials() {
        recordsList = trialManager.records.reversed()
    }

    override fun getItemId(position: Int): Long {
        return recordsList[position].id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trial_record, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = recordsList[position]
        holder.session = item
        with(holder.view) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = recordsList.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val context: Context get() = view.context

        private val container: ConstraintLayout = view as ConstraintLayout
        private val rankImage: RankImageView = view.image_rank_icon
        private val title: TextView = view.text_record_title
        private val exScore: TextView = view.text_ex_score
        private val exProgress: ProgressBar = view.progress_ex_score
        private val date: TextView = view.text_record_date
        private val label1: TextView = view.text_record_label_1
        private val label2: TextView = view.text_record_label_2
        private val label3: TextView = view.text_record_label_3
        private val label4: TextView = view.text_record_label_4
        private val song1: TextView = view.text_record_song_1
        private val song2: TextView = view.text_record_song_2
        private val song3: TextView = view.text_record_song_3
        private val song4: TextView = view.text_record_song_4
        private val difficulty1: View = view.view_difficulty_1
        private val difficulty2: View = view.view_difficulty_2
        private val difficulty3: View = view.view_difficulty_3
        private val difficulty4: View = view.view_difficulty_4
        private val jacketBackground: ImageView = view.image_jacket_background

        private var oldColors: ColorStateList= label1.textColors

        init {
            view.isLongClickable = true
        }

        var session: TrialSessionDB? = null
            set(s) {
                field = s
                if (s != null) {
                    val trial = trialManager.findTrial(s.trialId)!!
                    title.text = trial.name

                    val sessionEx = s.exScore ?: 0
                    val shouldShowRemaining = SharedPrefsUtil.getUserFlag(context, KEY_RECORDS_REMAINING_EX, false)
                    val goalEx = if (shouldShowRemaining) sessionEx - trial.total_ex!! else trial.total_ex
                    exScore.text = context.getString(R.string.ex_score_fraction_format, sessionEx, goalEx)
                    exProgress.max = trial.total_ex!!
                    val progressPercent = AccelerateInterpolator(0.25f).getInterpolation(sessionEx.toFloat() / trial.total_ex)
                    exProgress.progress = (progressPercent * sessionEx).toInt()

                    rankImage.rank = s.goalRank!!.parent
                    rankImage.alpha = if (s.goalObtained) 1f else 0.3f
                    date.text = DataUtil.humanNewlineTimestamp(context.locale, s.date)

                    val miniEntry = s.songResults.size == 0
                    if (!miniEntry) {
                        jacketBackground.setImageResource(trial.jacketResId(context))
                    }

                    arrayOf(label1, label2, label3, label4, song1, song2, song3, song4, jacketBackground, exProgress).forEach {
                        it.visibility = if (miniEntry) View.GONE else View.VISIBLE
                    }
                    view.text_record_song_base.visibility = if (miniEntry) View.GONE else View.INVISIBLE

                    arrayOf(label1, label2, label3, label4).forEachIndexed { idx, view ->
                        view.text = trial.songs[idx].name
                    }

                    arrayOf(song1, song2, song3, song4).forEachIndexed { idx, v ->
                        val songDb = s.songResults.firstOrNull { it.position == idx }
                        if (songDb != null) {
                            v.text = context.getString(R.string.score_string_format,
                                songDb.score.longNumberString(), songDb.exScore)
                            if (songDb.passed) {
                                v.setTextColor(oldColors)
                            } else {
                                v.setTextColor(ContextCompat.getColor(context, R.color.orange))
                            }
                        } else {
                            v.text = context.getString(R.string.not_played)
                            v.setTextColor(ContextCompat.getColor(context, R.color.orange))
                        }
                    }

                    arrayOf(difficulty1, difficulty2, difficulty3, difficulty4).forEachIndexed { idx, view ->
                        view.setBackgroundColor(ContextCompat.getColor(context, trial.songs[idx].difficultyClass.colorRes))
                    }
                }
            }

        override fun toString(): String {
            return super.toString() + " '$title'"
        }
    }

    companion object {
        const val ID_ACTION_DELETE = 10001
    }
}
