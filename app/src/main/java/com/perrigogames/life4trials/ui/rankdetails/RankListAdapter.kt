package com.perrigogames.life4trials.ui.rankdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.BaseRankGoal
import com.perrigogames.life4.data.LadderRank
import com.perrigogames.life4trials.data.RankEntry
import com.perrigogames.life4trials.ui.ranklist.RankListFragment.OnRankListInteractionListener
import com.perrigogames.life4trials.util.drawableRes
import com.perrigogames.life4trials.util.nameRes
import kotlinx.android.synthetic.main.item_rank_list.view.image_rank_icon
import kotlinx.android.synthetic.main.item_rank_list.view.text_goal_title
import kotlinx.android.synthetic.main.item_rank_list_goals.view.*

/**
 * [RecyclerView.Adapter] that can display a [RankEntry] and makes a call to the
 * specified [OnRankListInteractionListener].
 */
class RankListAdapter(private val mValues: List<RankEntry>,
                      private val selectedRank: LadderRank?,
                      private val showGoals: Boolean,
                      private val mListener: OnRankListInteractionListener?) :
    RecyclerView.Adapter<RankListAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as? RankEntry
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(when {
            viewType == TYPE_NO_RANK -> R.layout.item_no_rank
            showGoals -> R.layout.item_rank_list_goals
            else -> R.layout.item_rank_list
        }, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position > 0) {
            val item = mValues[position - 1]
            holder.setRank(item.rank)
            holder.highlighted = item.rank == selectedRank
            if (showGoals) {
                holder.setGoals(item.goals)
            }

            with(holder.mView) {
                tag = item
                setOnClickListener(mOnClickListener)
            }
        } else {
            holder.mView.setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemViewType(pos: Int) =
        if (pos == 0) TYPE_NO_RANK
        else TYPE_RANK

    override fun getItemCount(): Int = mValues.size + 1

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        private val icon: ImageView get() = mView.image_rank_icon
        private val title: TextView get() = mView.text_goal_title

        var highlighted = false
            set(v) {
                field = v
                if (v) {
                    mView.setBackgroundResource(R.drawable.drawable_rounded_light)
                } else {
                    mView.background = null
                }
            }

        fun setRank(rank: LadderRank) {
            icon.setImageDrawable(ContextCompat.getDrawable(mView.context, rank.drawableRes))
            title.text = mView.context.getString(rank.nameRes)
        }

        fun setGoals(goals: List<BaseRankGoal>) {
            mView.text_goals.text = StringBuilder().apply {
                goals.forEach { append("• ${it.goalString(mView.context)}\n") }
            }.toString()
        }

        override fun toString(): String {
            return super.toString() + " '$title'"
        }
    }

    companion object {
        const val TYPE_NO_RANK = 5
        const val TYPE_RANK = 6
    }
}
