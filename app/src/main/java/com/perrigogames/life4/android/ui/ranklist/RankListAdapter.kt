package com.perrigogames.life4.android.ui.ranklist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.categoryNameRes
import com.perrigogames.life4.android.databinding.ItemNoRankBinding
import com.perrigogames.life4.android.databinding.ItemRankHeaderBinding
import com.perrigogames.life4.android.databinding.ItemRankListBinding
import com.perrigogames.life4.android.drawableRes
import com.perrigogames.life4.android.nameRes
import com.perrigogames.life4.android.ui.ranklist.RankListAdapter.BaseViewHolder.*
import com.perrigogames.life4.android.ui.ranklist.RankListFragment.OnRankListInteractionListener
import com.perrigogames.life4.data.RankEntry
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.LadderRankClass
import org.koin.core.KoinComponent

/**
 * [RecyclerView.Adapter] that can display a [RankEntry] and makes a call to the
 * specified [OnRankListInteractionListener].
 */
class RankListAdapter(
    mValues: Map<LadderRankClass, List<RankEntry>>,
    private val selectedRank: LadderRank?,
    private val mListener: OnRankListInteractionListener?,
) : RecyclerView.Adapter<RankListAdapter.BaseViewHolder>(), KoinComponent {

    private val itemsList = mValues.flatMap { entry ->
        listOf(entry.key) + entry.value
    }

    private val mOnClickListener: View.OnClickListener = View.OnClickListener { v ->
        val item = v.tag as? RankEntry
        // Notify the active callbacks interface (the activity, if the fragment is attached to
        // one) that an item has been selected.
        mListener?.onListFragmentInteraction(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_NO_RANK -> NoRankViewHolder(ItemNoRankBinding.inflate(inflater, parent, false))
            TYPE_RANK_HEADER -> RankHeaderViewHolder(ItemRankHeaderBinding.inflate(inflater, parent, false))
            TYPE_RANK -> RankViewHolder(ItemRankListBinding.inflate(inflater, parent, false))
            else -> error("Invalid layoutId")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_NO_RANK -> {
                holder.setOnClickListener(mOnClickListener)
            }
            TYPE_RANK_HEADER -> {
                (holder as RankHeaderViewHolder).setRankClass(itemsList[position - 1] as LadderRankClass)
                holder.setOnClickListener(mOnClickListener)
            }
            TYPE_RANK -> {
                val entry = itemsList[position - 1] as RankEntry
                (holder as RankViewHolder).setRank(entry.rank)
                holder.highlighted = entry.rank == selectedRank

                with(holder) {
                    setTag(entry)
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

    sealed class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        protected val context: Context get() = itemView.context

        fun setTag(tag: Any) {
            itemView.tag = tag
        }

        fun setOnClickListener(onClickListener: View.OnClickListener) {
            itemView.setOnClickListener(onClickListener)
        }

        class NoRankViewHolder(val binding: ItemNoRankBinding) : BaseViewHolder(binding.root)

        class RankHeaderViewHolder(val binding: ItemRankHeaderBinding) : BaseViewHolder(binding.root) {

            fun setRankClass(rankClass: LadderRankClass) {
                binding.root.text = context.getString(rankClass.nameRes)
            }
        }

        class RankViewHolder(val binding: ItemRankListBinding) : BaseViewHolder(binding.root) {

            var highlighted = false
                set(v) {
                    field = v
                    if (v) {
                        binding.root.setBackgroundResource(R.drawable.drawable_rounded_light)
                    } else {
                        binding.root.background = null
                    }
                }

            fun setRank(rank: LadderRank) {
                binding.imageRankIcon.setImageDrawable(ContextCompat.getDrawable(context, rank.drawableRes))
                binding.textGoalTitle.text = context.getString(rank.categoryNameRes)
            }

            override fun toString(): String {
                return super.toString() + " '${binding.textGoalTitle}'"
            }
        }
    }

    companion object {
        const val TYPE_NO_RANK = 5
        const val TYPE_RANK_HEADER = 6
        const val TYPE_RANK = 7
    }
}
