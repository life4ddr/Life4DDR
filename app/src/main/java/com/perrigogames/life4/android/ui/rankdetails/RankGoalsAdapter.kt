package com.perrigogames.life4.android.ui.rankdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4.data.BaseRankGoal
import com.perrigogames.life4.data.LadderGoalProgress
import com.perrigogames.life4.data.RankEntry
import com.perrigogames.life4.db.GoalState
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.databinding.ItemNoGoalsBinding
import com.perrigogames.life4.android.databinding.ItemRankGoalV2Binding
import com.perrigogames.life4.android.ui.rankdetails.RankDetailsViewModel.OnGoalListInteractionListener
import com.perrigogames.life4.android.view.LadderGoalItemView
import com.perrigogames.life4.android.view.RankImageView
import kotlin.math.max

/**
 * [RecyclerView.Adapter] that can display a [RankEntry] and makes a call to the
 * specified [OnGoalListInteractionListener].
 */
class RankGoalsAdapter(
    private val rank: RankEntry,
    private val dataSource: DataSource,
    var listener: LadderGoalItemView.LadderGoalItemListener? = null,
    var goalListListener: OnGoalListInteractionListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mNoGoalBinding: ItemNoGoalsBinding? = null
    private fun noGoalBinding(parent: ViewGroup): ItemNoGoalsBinding {
        if (mNoGoalBinding == null) {
            mNoGoalBinding = ItemNoGoalsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }
        return mNoGoalBinding!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_GOAL -> {
                val binding = ItemRankGoalV2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                binding.root.listener = listener
                GoalViewHolder(binding)
            }
            else -> NoGoalViewHolder(noGoalBinding(parent))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder) {
        is GoalViewHolder -> {
            val item = dataSource.getGoals()[position]
            holder.bind(item, dataSource)
            holder.itemView.tag = item
        }
        else -> Unit
    }

    // Used to avoid the duplicate items recycling
    override fun getItemViewType(position: Int): Int = if (dataSource.getGoals().isEmpty()) VIEW_TYPE_NO_GOAL else VIEW_TYPE_GOAL

    override fun getItemCount(): Int = max(dataSource.getGoals().size, 1)

    interface DataSource {
        fun getGoals(): List<BaseRankGoal>
        fun isGoalMandatory(item: BaseRankGoal): Boolean
        fun isGoalExpanded(item: BaseRankGoal): Boolean
        fun canIgnoreGoals(): Boolean
        fun getGoalStatus(item: BaseRankGoal): GoalState
        fun getGoalProgress(item: BaseRankGoal): LadderGoalProgress?
    }

    inner class GoalViewHolder(val binding: ItemRankGoalV2Binding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(goal: BaseRankGoal, dataSource: DataSource) {
            val mandatory = dataSource.isGoalMandatory(goal)
            binding.root.expanded = dataSource.isGoalExpanded(goal)
            binding.root.canIgnore = dataSource.canIgnoreGoals() && !mandatory
            binding.root.setGoal(goal, dataSource.getGoalStatus(goal), dataSource.getGoalProgress(goal), mandatory)
        }
    }

    inner class NoGoalViewHolder(val binding: ItemNoGoalsBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.buttonSubmit.setOnClickListener { goalListListener?.onRankSubmitClicked() }
            binding.imageRank.rank = rank.rank
        }
    }

    companion object {
        const val VIEW_TYPE_GOAL = 5
        const val VIEW_TYPE_NO_GOAL = 6
    }
}
