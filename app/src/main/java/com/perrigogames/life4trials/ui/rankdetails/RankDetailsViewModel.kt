package com.perrigogames.life4trials.ui.rankdetails

import android.util.Log
import androidx.lifecycle.ViewModel
import com.perrigogames.life4trials.data.BaseRankGoal
import com.perrigogames.life4trials.data.RankEntry
import com.perrigogames.life4trials.db.GoalStatus
import com.perrigogames.life4trials.db.GoalStatusDB
import com.perrigogames.life4trials.manager.LadderManager
import com.perrigogames.life4trials.view.LadderGoalItemView

class RankDetailsViewModel(private val rankEntry: RankEntry,
                           private val options: RankDetailsFragment.Options,
                           private val ladderManager: LadderManager,
                           private val goalListListener: OnGoalListInteractionListener?) : ViewModel() {

    private val activeItems: MutableList<BaseRankGoal> by lazy {
        if (options.hideCompleted) {
            val completedGoals = ladderManager.getGoalStatuses(rankEntry.goals).filter { it.status == GoalStatus.COMPLETE }.map { it.goalId }
            rankEntry.goals.filterNot { completedGoals.contains(it.id.toLong()) }.toMutableList()
        } else rankEntry.goals.toMutableList()
    }
    private val expandedItems = mutableListOf<BaseRankGoal>()

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
            updateHiddenCount()
            goalListListener?.onGoalStateChanged(item, goalDB.status, 0)
        }

        override fun onIgnoreClicked(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalStatusDB) {
            ladderManager.setGoalState(goalDB, when(goalDB.status) {
                GoalStatus.IGNORED -> GoalStatus.INCOMPLETE
                else -> GoalStatus.IGNORED
            })
            updateVisibility(item, goalDB)
            updateHiddenCount()
            goalListListener?.onGoalStateChanged(item, goalDB.status, 0)
        }

        override fun onExpandClicked(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalStatusDB) {
            expandedItems.remove(item) || expandedItems.add(item)
            adapter.notifyItemChanged(activeItems.indexOf(item))
        }

        override fun onLongPressed(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalStatusDB) {
            Log.v("rank goal", "long pressed")
        }
    }

    val adapter: RankGoalsAdapter =
        RankGoalsAdapter(activeItems, expandedItems, rankEntry, ladderManager, goalItemListener, goalListListener)

    fun updateVisibility(goal: BaseRankGoal, goalDB: GoalStatusDB) {
        if (goalDB.status == GoalStatus.COMPLETE && options.hideCompleted) {
            val index = activeItems.indexOf(goal)
            activeItems.removeAt(index)
            adapter.notifyItemRemoved(index)
            if (activeItems.isEmpty()) {
                adapter.notifyItemInserted(0)
            }
        }
    }

    private fun updateHiddenCount() {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnGoalListInteractionListener {
        fun onGoalStateChanged(item: BaseRankGoal, goalStatus: GoalStatus, hiddenGoals: Int) {}
        fun onRankSubmitClicked() {}
        fun onUseRankClicked() {}
    }
}
