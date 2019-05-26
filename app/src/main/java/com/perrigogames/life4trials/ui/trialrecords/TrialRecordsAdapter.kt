package com.perrigogames.life4trials.ui.trialrecords

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
        private val rankImage: RankImageView = view.image_rank_icon
        private val title: TextView = view.text_record_title
        private val date: TextView = view.text_record_date
        private val label1: TextView = view.text_record_label_1
        private val label2: TextView = view.text_record_label_2
        private val label3: TextView = view.text_record_label_3
        private val label4: TextView = view.text_record_label_4
        private val song1: TextView = view.text_record_song_1
        private val song2: TextView = view.text_record_song_2
        private val song3: TextView = view.text_record_song_3
        private val song4: TextView = view.text_record_song_4
        private val jacketBackground: ImageView = view.image_jacket_background

        private var oldColors: ColorStateList= label1.textColors

        init {
            view.isLongClickable = true
        }

        var session: TrialSessionDB? = null
            set(s) {
                field = s
                if (s != null) {
                    val trial = trialManager.findTrial(s.trialId)
                    title.text = trial!!.name
                    rankImage.rank = s.goalRank
                    rankImage.alpha = if (s.goalObtained) 1f else 0.3f
                    date.text = DataUtil.humanTimestamp(view.context.locale, s.date)

                    val miniEntry = s.songs.size == 0
                    if (!miniEntry) {
                        Glide.with(view).load(trial.jacketUrl(view.resources, 350)).into(jacketBackground)
                    }

                    arrayOf(label1, label2, label3, label4, song1, song2, song3, song4, jacketBackground).forEach {
                        it.visibility = if (miniEntry) View.GONE else View.VISIBLE
                    }

                    arrayOf(label1, label2, label3, label4).forEachIndexed { idx, view ->
                        view.text = trial.songs[idx].name
                    }

                    arrayOf(song1, song2, song3, song4).forEachIndexed { idx, v ->
                        val songDb = s.songs.firstOrNull { it.position == idx }
                        if (songDb != null) {
                            v.text = view.context.getString(R.string.score_string_format,
                                songDb.score.longNumberString(), songDb.exScore)
                            if (songDb.passed) {
                                v.setTextColor(oldColors)
                            } else {
                                v.setTextColor(ContextCompat.getColor(view.context, R.color.orange))
                            }
                        } else {
                            v.text = view.context.getString(R.string.not_played)
                            v.setTextColor(ContextCompat.getColor(view.context, R.color.orange))
                        }
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
