package com.perrigogames.life4trials.ui.rankdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.LadderRank
import com.perrigogames.life4trials.data.RankEntry
import com.perrigogames.life4trials.ui.ranklist.RankListActivityFragment.OnRankListInteractionListener
import kotlinx.android.synthetic.main.item_rank.view.*

/**
 * [RecyclerView.Adapter] that can display a [RankEntry] and makes a call to the
 * specified [OnRankListInteractionListener].
 */
class RankListAdapter(private val mValues: List<RankEntry>,
                      private val mListener: OnRankListInteractionListener?) :
    RecyclerView.Adapter<RankListAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as RankEntry
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rank, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.setRank(mValues[position].rank)

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        private val icon: ImageView = mView.image_rank_icon
        private val title: TextView = mView.text_goal_title

        fun setRank(rank: LadderRank) {
            icon.setImageDrawable(ContextCompat.getDrawable(mView.context, rank.drawableRes))
            title.text = mView.context.getString(rank.nameRes)
        }

        override fun toString(): String {
            return super.toString() + " '$title'"
        }
    }
}
