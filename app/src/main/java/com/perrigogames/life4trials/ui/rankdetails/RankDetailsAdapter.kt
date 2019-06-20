package com.perrigogames.life4trials.ui.rankdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.BaseRankGoal
import com.perrigogames.life4trials.data.RankEntry
import com.perrigogames.life4trials.ui.rankdetails.RankDetailsFragment.OnGoalListInteractionListener
import kotlinx.android.synthetic.main.item_rank_goal.view.*

/**
 * [RecyclerView.Adapter] that can display a [RankEntry] and makes a call to the
 * specified [OnGoalListInteractionListener].
 */
class RankDetailsAdapter(private val rank: RankEntry,
                         private val mListener: OnGoalListInteractionListener?) :
    RecyclerView.Adapter<RankDetailsAdapter.ViewHolder>() {

    private val items = rank.goals

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as BaseRankGoal
            mListener?.onGoalListInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_rank_goal, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.setGoal(item)

        with(holder.view) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val statusIcon: ImageView = view.image_status_icon
        private val title: TextView = view.text_rank_title

        fun setGoal(goal: BaseRankGoal) {
//            statusIcon.setImageDrawable(ContextCompat.getDrawable(view.context, rank.drawableRes))
            title.text = goal.goalString(view.context)
        }

        override fun toString(): String {
            return super.toString() + " '$title'"
        }
    }
}
