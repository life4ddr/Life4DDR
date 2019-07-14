package com.perrigogames.life4trials.ui.rankdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.BaseRankGoal
import com.perrigogames.life4trials.data.RankEntry
import com.perrigogames.life4trials.db.GoalStatus
import com.perrigogames.life4trials.db.GoalStatusDB
import com.perrigogames.life4trials.manager.LadderManager
import com.perrigogames.life4trials.ui.rankdetails.RankDetailsFragment.OnGoalListInteractionListener
import com.perrigogames.life4trials.view.LadderGoalItemView
import kotlinx.android.synthetic.main.item_no_goals.view.*
import kotlin.math.max

/**
 * [RecyclerView.Adapter] that can display a [RankEntry] and makes a call to the
 * specified [OnGoalListInteractionListener].
 */
class RankGoalsAdapter(private val rank: RankEntry,
                       private val options: RankDetailsFragment.Options,
                       private val ladderManager: LadderManager,
                       private var listener: OnGoalListInteractionListener? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = rank.goals
    private val activeItems: MutableList<BaseRankGoal> by lazy {
        if (options.hideCompleted) {
            val completedGoals = ladderManager.getGoalStatuses(rank.goals).filter { it.status == GoalStatus.COMPLETE }.map { it.goalId }
            rank.goals.filterNot { completedGoals.contains(it.id.toLong()) }.toMutableList()
        } else rank.goals.toMutableList()
    }
    private val expandedItems = mutableListOf<BaseRankGoal>()

    private var mNoGoalView: ConstraintLayout? = null
    private fun noGoalView(parent: ViewGroup): ConstraintLayout {
        if (mNoGoalView == null) {
            mNoGoalView = LayoutInflater.from(parent.context).inflate(R.layout.item_no_goals, parent, false) as ConstraintLayout
        }
        return mNoGoalView!!
    }

    private val goalItemListener = object: LadderGoalItemView.LadderGoalItemListener {

        override fun createGoalDB(item: BaseRankGoal): GoalStatusDB {
            return ladderManager.getOrCreateGoalStatus(item)
        }

        override fun onStateToggle(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalStatusDB) {
            ladderManager.setGoalState(goalDB, when(goalDB.status) {
                GoalStatus.COMPLETE -> GoalStatus.INCOMPLETE
                else -> GoalStatus.COMPLETE
            })
            updateVisibility(item, goalDB)
            listener?.onGoalStateChanged(item, goalDB.status)
        }

        override fun onIgnoreClicked(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalStatusDB) {
            ladderManager.setGoalState(goalDB, when(goalDB.status) {
                GoalStatus.IGNORED -> GoalStatus.INCOMPLETE
                else -> GoalStatus.IGNORED
            })
            updateVisibility(item, goalDB)
            listener?.onGoalStateChanged(item, goalDB.status)
        }

        override fun onExpandClicked(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalStatusDB) {
            expandedItems.remove(item) || expandedItems.add(item)
            notifyItemChanged(activeItems.indexOf(item))
        }
    }

    fun updateVisibility(goal: BaseRankGoal, goalDB: GoalStatusDB) {
        if (goalDB.status == GoalStatus.COMPLETE && options.hideCompleted) {
            val index = activeItems.indexOf(goal)
            activeItems.removeAt(index)
            notifyItemRemoved(index)
            if (activeItems.isEmpty()) {
                notifyItemInserted(0)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_GOAL -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_rank_goal, parent, false) as LadderGoalItemView
                itemView.listener = goalItemListener
                GoalViewHolder(itemView)
            }
            else -> NoGoalViewHolder(noGoalView(parent))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder) {
        is NoGoalViewHolder -> {
            holder.view.button_submit.setOnClickListener {
                listener?.onRankSubmitClicked()
            }
            holder.view.image_rank.rank = rank.rank
        }
        is GoalViewHolder -> {
            val item = activeItems[position]
            val goalDB = ladderManager.getGoalStatus(item)
            val progress = ladderManager.getGoalProgress(item)

//            if (progress?.isComplete() == true) {
//                ladderManager.setGoalState(goalDB!!, GoalStatus.COMPLETE)
//                holder.view.post { notifyItemChanged(position) }
//            }

            holder.view.expanded = expandedItems.contains(item)
            holder.view.setGoal(item, goalDB, progress)

            with(holder.view) {
                tag = item
            }
        }
        else -> Unit
    }

    // Used to avoid the duplicate items recycling
    override fun getItemViewType(position: Int): Int = if (activeItems.isEmpty()) VIEW_TYPE_NO_GOAL else VIEW_TYPE_GOAL

    override fun getItemCount(): Int = max(activeItems.size, 1)

    inner class GoalViewHolder(val view: LadderGoalItemView) : RecyclerView.ViewHolder(view)

    inner class NoGoalViewHolder(val view: ConstraintLayout) : RecyclerView.ViewHolder(view)

    companion object {
        const val VIEW_TYPE_GOAL = 5
        const val VIEW_TYPE_NO_GOAL = 6
    }
}
