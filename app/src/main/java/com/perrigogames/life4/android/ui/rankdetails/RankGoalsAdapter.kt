package com.perrigogames.life4.android.ui.rankdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.databinding.ItemNoGoalsBinding
import com.perrigogames.life4.android.ui.rankdetails.RankDetailsViewModel.OnGoalListInteractionListener
import com.perrigogames.life4.android.view.LadderGoalItemView
import com.perrigogames.life4.data.BaseRankGoal
import com.perrigogames.life4.data.LadderGoalProgress
import com.perrigogames.life4.data.RankEntry
import com.perrigogames.life4.data.SongsClearGoal
import com.perrigogames.life4.db.GoalState
import com.perrigogames.life4.enums.ClearType
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
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_rank_goal_v2, parent, false) as LadderGoalItemView
                view.listener = listener
                GoalViewHolder(view)
            }
            VIEW_TYPE_GOAL_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_rank_goal_header, parent, false) as TextView
                GoalHeaderViewHolder(view)
            }
            else -> NoGoalViewHolder(noGoalBinding(parent))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder) {
        is GoalViewHolder -> {
            val item = dataSource.getGoals()[position] as BaseRankGoal
            holder.bind(item, dataSource)
            holder.itemView.tag = item
        }
        is GoalHeaderViewHolder -> {
            holder.bind(dataSource.getGoals()[position] as String)
        }
        else -> Unit
    }

    override fun getItemViewType(position: Int): Int = when {
        dataSource.getGoals().isEmpty() -> VIEW_TYPE_NO_GOAL
        dataSource.getGoals()[position] is String -> VIEW_TYPE_GOAL_HEADER
        else -> VIEW_TYPE_GOAL
    }

    override fun getItemCount(): Int {
        return max(1, dataSource.getGoals().size)
    }

    interface DataSource {
        fun getGoals(): List<Any> // either BaseRankGoal or String
        fun isGoalMandatory(item: BaseRankGoal): Boolean
        fun isGoalExpanded(item: BaseRankGoal): Boolean
        fun canIgnoreGoals(): Boolean
        fun getGoalStatus(item: BaseRankGoal): GoalState
        fun getGoalProgress(item: BaseRankGoal): LadderGoalProgress?
    }

    inner class GoalHeaderViewHolder(val view: TextView) : RecyclerView.ViewHolder(view) {
        fun bind(headerText: String) {
            view.text = headerText
        }
    }

    inner class GoalViewHolder(val view: LadderGoalItemView) : RecyclerView.ViewHolder(view) {
        fun bind(goal: BaseRankGoal, dataSource: DataSource) {
            val mandatory = dataSource.isGoalMandatory(goal)
            view.expanded = dataSource.isGoalExpanded(goal)
            view.canIgnore = dataSource.canIgnoreGoals() && !mandatory
            view.setGoal(
                goal = goal,
                goalDB = dataSource.getGoalStatus(goal),
                goalProgress = dataSource.getGoalProgress(goal),
                mandatory = mandatory,
                backgroundTintId = goal.goalBackgroundColor(),
            )
        }

        @ColorRes private fun BaseRankGoal.goalBackgroundColor(): Int? {
            (this as? SongsClearGoal)?.let { goal ->
                if (goal.songCount == null &&
                    goal.songs == null &&
                    goal.score == null &&
                    goal.averageScore == null
                ) {
                    return when(goal.clearType) {
                        ClearType.CLEAR -> R.color.color_goal_background_clear
                        ClearType.LIFE4_CLEAR -> R.color.color_goal_background_life4
                        ClearType.GOOD_FULL_COMBO -> R.color.color_goal_background_good
                        ClearType.GREAT_FULL_COMBO -> R.color.color_goal_background_great
                        ClearType.PERFECT_FULL_COMBO -> R.color.color_goal_background_perfect
                        ClearType.MARVELOUS_FULL_COMBO -> R.color.color_goal_background_marvelous
                        else -> null
                    }
                }
            }
            return null
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
        const val VIEW_TYPE_GOAL_HEADER = 7
    }
}
