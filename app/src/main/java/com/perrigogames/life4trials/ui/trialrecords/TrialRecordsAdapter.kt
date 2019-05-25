package com.perrigogames.life4trials.ui.trialrecords

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.db.TrialSessionDB
import com.perrigogames.life4trials.manager.TrialManager
import com.perrigogames.life4trials.ui.trialrecords.TrialRecordsFragment.OnRecordsListInteractionListener
import com.perrigogames.life4trials.util.DataUtil
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

    private val recordsList = trialManager.records
    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            mListener?.onRecordsListInteraction(v.tag as TrialSessionDB)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trial_record, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = recordsList[position]
        holder.session = item
        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = recordsList.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        private val rankImage: RankImageView = mView.image_rank_icon
        private val title: TextView = mView.text_record_title
        private val date: TextView = mView.text_record_date
        private val label1: TextView = mView.text_record_label_1
        private val label2: TextView = mView.text_record_label_2
        private val label3: TextView = mView.text_record_label_3
        private val label4: TextView = mView.text_record_label_4
        private val song1: TextView = mView.text_record_song_1
        private val song2: TextView = mView.text_record_song_2
        private val song3: TextView = mView.text_record_song_3
        private val song4: TextView = mView.text_record_song_4

        var session: TrialSessionDB? = null
            set(s) {
                field = s
                if (s != null) {
                    val trial = trialManager.findTrial(s.trialId)
                    title.text = trial!!.name
                    rankImage.rank = s.goalRank
                    rankImage.alpha = if (s.goalObtained) 1f else 0.3f
                    date.text = DataUtil.humanTimestamp(mView.context.locale, s.date)
                    arrayOf(label1, label2, label3, label4).forEachIndexed { idx, view ->
                        view.text = trial.songs[idx].name
                    }
                    arrayOf(song1, song2, song3, song4).forEachIndexed { idx, view ->
                        val song = s.songs[idx]
                        view.text = mView.context.getString(R.string.score_string_format,
                            song.score.longNumberString(), song.exScore)
                    }
                }
            }

        override fun toString(): String {
            return super.toString() + " '$title'"
        }
    }
}
