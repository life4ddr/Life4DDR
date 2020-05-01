package com.perrigogames.life4trials.ui.rankdetails

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.perrigogames.life4.data.BaseRankGoal
import com.perrigogames.life4.data.LadderRank
import com.perrigogames.life4.data.RankEntry
import com.perrigogames.life4.db.GoalState
import com.perrigogames.life4.enums.GoalStatus
import com.perrigogames.life4.enums.GoalStatus.*
import com.perrigogames.life4trials.R
import com.perrigogames.life4.model.LadderManager
import com.perrigogames.life4trials.nameRes
import com.perrigogames.life4trials.view.LadderGoalItemView

/**
 * A [ViewModel] that manages manipulating and displaying a series of [BaseRankGoal]s
 * attributable to a certain rank.
 */
class RankDetailsViewModel(private val context: Context,
                           private val rankEntry: RankEntry?,
                           private val options: RankDetailsFragment.Options,
                           private val ladderManager: LadderManager,
                           private val goalListListener: OnGoalListInteractionListener?) : ViewModel() {

    private val targetEntry: RankEntry? by lazy { when {
        !options.showNextGoals -> rankEntry
        rankEntry == null -> ladderManager.findRankEntry(LadderRank.values().first())
        else -> ladderManager.nextEntry(rankEntry.rank)
    } }

    private val hidesCompleteTasks = options.hideCompleted // resolves immediately, determines eligibility for toggling hidden on/off

    private val allGoals: List<BaseRankGoal> by lazy { targetEntry!!.goals }
    private val activeGoals: MutableList<BaseRankGoal> by lazy { createActiveGoals() }

    private fun createActiveGoals() = when {
        targetEntry == null -> mutableListOf()
        options.hideCompleted -> {
            val completedGoals = ladderManager.getGoalStateList(targetEntry!!.goals).filter { it.status == COMPLETE }.map { it.goalId }
            targetEntry!!.goals.filterNot { completedGoals.contains(it.id.toLong()) }.toMutableList()
        }
        else -> allGoals.toMutableList()
    }

    private val expandedItems = mutableListOf<BaseRankGoal>()
    private val completeItemCount get() = ladderManager.getGoalStateList(allGoals).count { it.status == COMPLETE }
    private val hiddenItemCount get() = ladderManager.getGoalStateList(allGoals).count { it.status == IGNORED }
    private var canIgnoreGoals: Boolean = true

    private val goalItemListener: LadderGoalItemView.LadderGoalItemListener = object: LadderGoalItemView.LadderGoalItemListener {

        override fun onStateToggle(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalState) {
            ladderManager.setGoalState(goalDB.goalId, when(goalDB.status) {
                COMPLETE -> INCOMPLETE
                else -> COMPLETE
            })
            updateVisibility(item, goalDB)
            updateCompleteCount()
            updateIgnoredStates()
            goalListListener?.onGoalStateChanged(item, goalDB.status, 0)
        }

        override fun onIgnoreClicked(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalState) {
            ladderManager.setGoalState(goalDB.goalId, when(goalDB.status) {
                IGNORED -> INCOMPLETE
                else -> IGNORED
            })
            updateVisibility(item, goalDB)
            updateCompleteCount()
            updateIgnoredStates()
            goalListListener?.onGoalStateChanged(item, goalDB.status, 0)
        }

        override fun onExpandClicked(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalState) {
            expandedItems.remove(item) || expandedItems.add(item)
            adapter!!.notifyItemChanged(activeGoals.indexOf(item))
        }

        override fun onLongPressed(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalState) {
            Log.v("rank goal", "long pressed")
        }
    }

    private val dataSource = object: RankGoalsAdapter.DataSource {
        override fun getGoals() = activeGoals
        override fun isGoalExpanded(item: BaseRankGoal) = expandedItems.contains(item)
        override fun canIgnoreGoals(): Boolean = canIgnoreGoals
        override fun getGoalStatus(item: BaseRankGoal) = ladderManager.getOrCreateGoalState(item)
        //FIXME progress
//        override fun getGoalProgress(item: BaseRankGoal, playStyle: PlayStyle) = ladderManager.getGoalProgress(item, playStyle)
    }

    val adapter: RankGoalsAdapter? = targetEntry?.let { RankGoalsAdapter(it, dataSource, goalItemListener, goalListListener) }
    val shouldShowGoals get() = adapter != null

    val directionsText = MutableLiveData<String>()
    val completedStatusText = MutableLiveData<String>()
    val completedStatusVisibility = MutableLiveData<Int>()
    val completedStatusArrowVisibility = MutableLiveData<Int>()
    val completedStatusArrowRotation = MutableLiveData<Float>()

    init {
        val rankName = targetEntry?.rank?.nameRes?.let { context.getString(it) }
        directionsText.value = rankName?.let { context.resources.getString(R.string.rank_directions, it) } ?: ""
        completedStatusArrowVisibility.value = if (hidesCompleteTasks) View.VISIBLE else View.GONE
        if (shouldShowGoals) {
            updateCompleteCount()
            updateIgnoredStates()
        }
    }

    fun onGoalsCompleteClicked() {
        if (hidesCompleteTasks) {
            toggleHideCompletedGoals()
        }
    }

    private fun toggleHideCompletedGoals() {
        options.hideCompleted = !options.hideCompleted

        val goalStatuses = ladderManager.getGoalStateList(allGoals)
        val allComplete = goalStatuses.all { it.status == COMPLETE }
        val goalSequence = when {
            options.hideCompleted -> allGoals.reversed()
            else -> allGoals
        }
        goalSequence.forEachIndexed { idx, goal ->
            val status = goalStatuses.first { it.goalId == goal.id.toLong() }
            if (status.status == COMPLETE) {
                when {
                    options.hideCompleted -> {
                        val targetIdx = allGoals.size - (idx + 1)
                        activeGoals.removeAt(targetIdx)
                        if (!allComplete) {
                            adapter?.notifyItemRemoved(targetIdx)
                        }
                    }
                    else -> {
                        activeGoals.add(idx, allGoals[idx])
                        if (!allComplete) {
                            adapter?.notifyItemInserted(idx)
                        }
                    }
                }
            }
        }
        if (allComplete) {
            adapter?.notifyDataSetChanged()
        }

        completedStatusArrowRotation.value = if (options.hideCompleted) 0f else 90f
    }

    /**
     * Updates the visibility of a single goal
     */
    fun updateVisibility(goal: BaseRankGoal, goalDB: GoalState) {
        if (goalDB.status == COMPLETE && options.hideCompleted) {
            val index = activeGoals.indexOf(goal)
            activeGoals.removeAt(index)
            adapter!!.notifyItemRemoved(index)
            if (activeGoals.isEmpty()) {
                adapter.notifyItemInserted(0)
            }
        }
    }

    private fun updateCompleteCount() {
        completedStatusVisibility.value = View.VISIBLE
        completedStatusText.value = context.getString(R.string.goals_completed_format, completeItemCount, targetEntry?.requirements ?: 0)
    }

    private fun updateIgnoredStates() {
        val prevIgnore = canIgnoreGoals
        canIgnoreGoals = hiddenItemCount < targetEntry?.allowedIgnores ?: 0
        if (prevIgnore != canIgnoreGoals) {
            adapter!!.notifyDataSetChanged()
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnGoalListInteractionListener {
        fun onGoalStateChanged(item: BaseRankGoal, goalStatus: GoalStatus, hiddenGoals: Int) {}
        fun onNextSwitchToggled(enabled: Boolean) {}
        fun onRankSubmitClicked() {}
    }
}
