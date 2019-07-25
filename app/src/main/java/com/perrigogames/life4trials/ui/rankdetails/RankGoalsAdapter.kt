package com.perrigogames.life4trials.ui.rankdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.BaseRankGoal
import com.perrigogames.life4trials.data.LadderGoalProgress
import com.perrigogames.life4trials.data.RankEntry
import com.perrigogames.life4trials.db.GoalStatusDB
import com.perrigogames.life4trials.ui.rankdetails.RankDetailsViewModel.OnGoalListInteractionListener
import com.perrigogames.life4trials.view.LadderGoalItemView
import kotlinx.android.synthetic.main.item_no_goals.view.*
import kotlin.math.max

/**
 * [RecyclerView.Adapter] that can display a [RankEntry] and makes a call to the
 * specified [OnGoalListInteractionListener].
 */
class RankGoalsAdapter(private val rank: RankEntry,
                       private val dataSource: DataSource,
                       var listener: LadderGoalItemView.LadderGoalItemListener? = null,
                       var goalListListener: OnGoalListInteractionListener? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mNoGoalView: ConstraintLayout? = null
    private fun noGoalView(parent: ViewGroup): ConstraintLayout {
        if (mNoGoalView == null) {
            mNoGoalView = LayoutInflater.from(parent.context).inflate(R.layout.item_no_goals, parent, false) as ConstraintLayout
        }
        return mNoGoalView!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_GOAL -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_rank_goal, parent, false) as LadderGoalItemView
                itemView.listener = listener
                GoalViewHolder(itemView)
            }
            else -> NoGoalViewHolder(noGoalView(parent))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder) {
        is GoalViewHolder -> {
            val item = dataSource.getGoals()[position]
            holder.bind(item, dataSource.getGoalStatus(item), dataSource.getGoalProgress(item))
            holder.view.tag = item
        }
        else -> Unit
    }

    // Used to avoid the duplicate items recycling
    override fun getItemViewType(position: Int): Int = if (dataSource.getGoals().isEmpty()) VIEW_TYPE_NO_GOAL else VIEW_TYPE_GOAL

    override fun getItemCount(): Int = max(dataSource.getGoals().size, 1)

    interface DataSource {
        fun getGoals(): List<BaseRankGoal>
        fun isGoalExpanded(item: BaseRankGoal): Boolean
        fun getGoalStatus(item: BaseRankGoal): GoalStatusDB
        fun getGoalProgress(item: BaseRankGoal): LadderGoalProgress?
    }

    inner class GoalViewHolder(val view: LadderGoalItemView) : RecyclerView.ViewHolder(view) {
        fun bind(goal: BaseRankGoal, goalDB: GoalStatusDB, goalProgress: LadderGoalProgress? = null) {
            view.expanded = dataSource.isGoalExpanded(goal)
            view.setGoal(goal, goalDB, goalProgress)
        }
    }

    inner class NoGoalViewHolder(val view: ConstraintLayout) : RecyclerView.ViewHolder(view) {
        init {
            view.button_submit.setOnClickListener { goalListListener?.onRankSubmitClicked() }
            view.image_rank.rank = rank.rank
        }
    }

    companion object {
        const val VIEW_TYPE_GOAL = 5
        const val VIEW_TYPE_NO_GOAL = 6
    }
}
