package com.perrigogames.life4.android.ui.trial

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4.SettingsKeys.KEY_RECORDS_REMAINING_EX
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.colorRes
import com.perrigogames.life4.android.databinding.ItemTrialRecordBinding
import com.perrigogames.life4.android.ui.trial.TrialRecordsFragment.OnRecordsListInteractionListener
import com.perrigogames.life4.android.util.jacketResId
import com.perrigogames.life4.android.util.visibilityBool
import com.perrigogames.life4.db.TrialSession
import com.perrigogames.life4.db.TrialSong
import com.perrigogames.life4.longNumberString
import com.perrigogames.life4.model.TrialManager
import com.russhwolf.settings.Settings
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * [RecyclerView.Adapter] that can display a [TrialSession] and makes a call to the
 * specified [OnRecordsListInteractionListener].
 */
class TrialRecordsAdapter(
    private val viewModel: TrialRecordsViewModel,
    private val mListener: OnRecordsListInteractionListener?,
) : RecyclerView.Adapter<TrialRecordsAdapter.ViewHolder>(), KoinComponent {

    private val trialManager: TrialManager by inject()
    private val settings: Settings by inject()

    private val recordsList: List<TrialSession> get() = viewModel.records.value ?: emptyList()
    private val mOnClickListener = View.OnClickListener { v ->
        mListener?.onRecordsListInteraction(v.tag as TrialSession)
    }

    override fun getItemId(position: Int) = recordsList[position].id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemTrialRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = recordsList[position]
        holder.set(item, trialManager.getSongsForSession(item.id))
        with(holder.itemView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = recordsList.size

    inner class ViewHolder(val binding: ItemTrialRecordBinding) : RecyclerView.ViewHolder(binding.root) {
        private val context: Context get() = itemView.context

        private val recordLabels = listOf(
            binding.textRecordLabel1,
            binding.textRecordLabel2,
            binding.textRecordLabel3,
            binding.textRecordLabel4,
        )

        private val recordSongs = listOf(
            binding.textRecordSong1,
            binding.textRecordSong2,
            binding.textRecordSong3,
            binding.textRecordSong4,
        )

        private val viewDifficulties = listOf(
            binding.viewDifficulty1,
            binding.viewDifficulty2,
            binding.viewDifficulty3,
            binding.viewDifficulty4,
        )

        private var oldColors: ColorStateList= binding.textRecordLabel1.textColors

        init {
            itemView.isLongClickable = true
        }

        fun set(session: TrialSession, songs: List<TrialSong>) {
            val trial = trialManager.findTrial(session.trialId)!!
            binding.textRecordTitle.text = trial.name

            val sessionEx = songs.sumBy { it.exScore.toInt() }
            val shouldShowRemaining = settings.getBoolean(KEY_RECORDS_REMAINING_EX, false)
            val goalEx = if (shouldShowRemaining) sessionEx - trial.totalEx else trial.totalEx
            binding.textExScore.text = context.getString(R.string.ex_score_fraction_format, sessionEx, goalEx)
            binding.progressExScore.max = trial.totalEx
            val progressPercent = AccelerateInterpolator(0.25f).getInterpolation(sessionEx.toFloat() / trial.totalEx)
            binding.progressExScore.progress = (progressPercent * sessionEx).toInt()

            binding.imageRankIcon.rank = session.goalRank.parent
            binding.imageRankIcon.alpha = if (session.goalObtained) 1f else 0.3f

            val dateTime = session.date.toInstant().toLocalDateTime(TimeZone.currentSystemDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                binding.textRecordDate.text =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd\nhh:mm a").format(
                        dateTime.toJavaLocalDateTime()
                    )
            } else {
                val calendar = Calendar.getInstance()
                calendar.set(
                    dateTime.year,
                    dateTime.monthNumber,
                    dateTime.dayOfMonth,
                    dateTime.hour,
                    dateTime.minute,
                    dateTime.second
                )
                binding.textRecordDate.text = SimpleDateFormat("yyyy-MM-dd\nHH:mm:ss").format(calendar.time)
            }


            val miniEntry = songs.isEmpty()
            if (!miniEntry) {
                binding.imageJacketBackground.setImageResource(trial.jacketResId(context))
            }

            (recordLabels + recordSongs + binding.imageJacketBackground + binding.progressExScore).forEach {
                it.visibilityBool = !miniEntry
            }
            binding.textRecordSongBase.visibility = if (miniEntry) View.GONE else View.INVISIBLE

            recordLabels.forEachIndexed { idx, view ->
                view.text = trial.songs[idx].name
            }

            recordSongs.forEachIndexed { idx, v ->
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

            viewDifficulties.forEachIndexed { idx, view ->
                view.setBackgroundColor(ContextCompat.getColor(context, trial.songs[idx].difficultyClass.colorRes))
            }
        }

        override fun toString(): String {
            return super.toString() + " '$binding.textRecordTitle'"
        }
    }

    companion object {
        const val ID_ACTION_DELETE = 10001
    }
}
