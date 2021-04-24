package com.perrigogames.life4.android.ui.ranklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4.data.LadderRank
import com.perrigogames.life4.data.RankEntry
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.categoryNameRes
import com.perrigogames.life4.android.drawableRes
import com.perrigogames.life4.android.nameRes
import com.perrigogames.life4.android.ui.ranklist.RankListFragment.OnRankListInteractionListener
import com.perrigogames.life4.android.view.RankImageView
import com.perrigogames.life4.data.LadderRankClass
import kotlinx.android.synthetic.main.item_rank_list.view.image_rank_icon
import kotlinx.android.synthetic.main.item_rank_list.view.text_goal_title
import kotlinx.android.synthetic.main.item_rank_list_goals.view.*
import org.koin.core.KoinComponent

/**
 * [RecyclerView.Adapter] that can display a [RankEntry] and makes a call to the
 * specified [OnRankListInteractionListener].
 */
class RankListAdapter(private val mValues: Map<LadderRankClass, List<RankEntry>>,
                      private val selectedRank: LadderRank?,
                      private val mListener: OnRankListInteractionListener?) :
    RecyclerView.Adapter<RankListAdapter.ViewHolder>(), KoinComponent {

    private val itemsList = mValues.flatMap { entry ->
        listOf(entry.key) + entry.value
    }

    private val mOnClickListener: View.OnClickListener = View.OnClickListener { v ->
        val item = v.tag as? RankEntry
        // Notify the active callbacks interface (the activity, if the fragment is attached to
        // one) that an item has been selected.
        mListener?.onListFragmentInteraction(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = when (viewType) {
            TYPE_NO_RANK -> R.layout.item_no_rank
            TYPE_RANK_HEADER -> R.layout.item_rank_header
            TYPE_RANK -> R.layout.item_rank_list
            else -> error("Invalid layoutId")
        }
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(layoutId, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_NO_RANK -> {
                holder.mView.setOnClickListener(mOnClickListener)
            }
            TYPE_RANK_HEADER -> {
                holder.setRankClass(itemsList[position - 1] as LadderRankClass)
                holder.mView.setOnClickListener(mOnClickListener)
            }
            TYPE_RANK -> {
                val entry = itemsList[position - 1] as RankEntry
                holder.setRank(entry.rank)
                holder.highlighted = entry.rank == selectedRank

                with(holder.mView) {
                    tag = entry
                    setOnClickListener(mOnClickListener)
                }
            }
        }
    }

    override fun getItemViewType(pos: Int) = when {
        pos == 0 -> TYPE_NO_RANK
        else -> when (itemsList[pos - 1].javaClass) {
            LadderRankClass::class.java -> TYPE_RANK_HEADER
            else -> TYPE_RANK
        }
    }

    override fun getItemCount(): Int = itemsList.size + 1

    fun spanSizeLookup(columnCount: Int) = object: GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(pos: Int) = when (getItemViewType(pos)) {
            TYPE_NO_RANK,
            TYPE_RANK_HEADER -> columnCount
            else -> 1
        }
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        private val icon: ImageView get() = (mView.image_rank_icon as RankImageView)
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
            title.text = mView.context.getString(rank.categoryNameRes)
        }

        fun setRankClass(rankClass: LadderRankClass) {
            (mView as TextView).text = mView.context.getString(rankClass.nameRes)
        }

        override fun toString(): String {
            return super.toString() + " '$title'"
        }
    }

    companion object {
        const val TYPE_NO_RANK = 5
        const val TYPE_RANK_HEADER = 6
        const val TYPE_RANK = 7
    }
}
