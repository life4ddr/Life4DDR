package com.perrigogames.life4trials.ui.trial

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
import com.perrigogames.life4.SettingsKeys.KEY_RECORDS_REMAINING_EX
import com.perrigogames.life4.db.TrialSession
import com.perrigogames.life4.db.TrialSong
import com.perrigogames.life4.longNumberString
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.colorRes
import com.perrigogames.life4.model.TrialManager
import com.perrigogames.life4trials.ui.trial.TrialRecordsFragment.OnRecordsListInteractionListener
import com.perrigogames.life4trials.util.jacketResId
import com.perrigogames.life4trials.util.visibilityBool
import com.perrigogames.life4trials.view.RankImageView
import com.russhwolf.settings.Settings
import kotlinx.android.synthetic.main.item_trial_record.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * [RecyclerView.Adapter] that can display a [TrialSession] and makes a call to the
 * specified [OnRecordsListInteractionListener].
 */
class TrialRecordsAdapter(private val viewModel: TrialRecordsViewModel,
                          private val mListener: OnRecordsListInteractionListener?) :
    RecyclerView.Adapter<TrialRecordsAdapter.ViewHolder>(), KoinComponent {

    private val trialManager: TrialManager by inject()
    private val settings: Settings by inject()

    private val recordsList: List<TrialSession> get() = viewModel.records.value ?: emptyList()
    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            mListener?.onRecordsListInteraction(v.tag as TrialSession)
        }
    }

    override fun getItemId(position: Int) = recordsList[position].id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_trial_record, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = recordsList[position]
        holder.set(item, trialManager.getSongsForSession(item.id))
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

        fun set(session: TrialSession, songs: List<TrialSong>) {
            val trial = trialManager.findTrial(session.trialId)!!
            title.text = trial.name

            val sessionEx = songs.sumBy { it.exScore.toInt() }
            val shouldShowRemaining = settings.getBoolean(KEY_RECORDS_REMAINING_EX, false)
            val goalEx = if (shouldShowRemaining) sessionEx - trial.total_ex else trial.total_ex
            exScore.text = context.getString(R.string.ex_score_fraction_format, sessionEx, goalEx)
            exProgress.max = trial.total_ex
            val progressPercent = AccelerateInterpolator(0.25f).getInterpolation(sessionEx.toFloat() / trial.total_ex)
            exProgress.progress = (progressPercent * sessionEx).toInt()

            rankImage.rank = session.goalRank.parent
            rankImage.alpha = if (session.goalObtained) 1f else 0.3f
            date.text = session.date

            val miniEntry = songs.isEmpty()
            if (!miniEntry) {
                jacketBackground.setImageResource(trial.jacketResId(context))
            }

            arrayOf(label1, label2, label3, label4, song1, song2, song3, song4, jacketBackground, exProgress).forEach {
                it.visibilityBool = !miniEntry
            }
            view.text_record_song_base.visibility = if (miniEntry) View.GONE else View.INVISIBLE

            arrayOf(label1, label2, label3, label4).forEachIndexed { idx, view ->
                view.text = trial.songs[idx].name
            }

            arrayOf(song1, song2, song3, song4).forEachIndexed { idx, v ->
                val songDb = songs.firstOrNull { it.position.toInt() == idx }
                if (songDb != null) {
                    v.text = context.getString(R.string.score_string_format,
                        songDb.score.toInt().longNumberString(), songDb.exScore)
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

        override fun toString(): String {
            return super.toString() + " '$title'"
        }
    }

    companion object {
        const val ID_ACTION_DELETE = 10001
    }
}
