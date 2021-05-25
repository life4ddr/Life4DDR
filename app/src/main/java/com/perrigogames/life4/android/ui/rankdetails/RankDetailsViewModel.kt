package com.perrigogames.life4.android.ui.rankdetails

import android.content.res.Resources
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.nameRes
import com.perrigogames.life4.android.view.LadderGoalItemView
import com.perrigogames.life4.data.BaseRankGoal
import com.perrigogames.life4.data.RankEntry
import com.perrigogames.life4.data.SongsClearGoal
import com.perrigogames.life4.db.GoalState
import com.perrigogames.life4.enums.GoalStatus
import com.perrigogames.life4.enums.GoalStatus.*
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.model.LadderManager
import com.perrigogames.life4.model.LadderProgressManager
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * A [ViewModel] that manages manipulating and displaying a series of [BaseRankGoal]s
 * attributable to a certain rank.
 */
class RankDetailsViewModel(
    private val resources: Resources,
    private val rankEntry: RankEntry?,
    private val options: RankDetailsFragment.Options,
    private val goalListListener: OnGoalListInteractionListener?,
) : ViewModel(), KoinComponent {

    private val ladderManager: LadderManager by inject()
    private val ladderProgressManager: LadderProgressManager by inject()

    private val targetEntry: RankEntry? by lazy { when {
        !options.showNextGoals -> rankEntry
        rankEntry == null -> ladderManager.findRankEntry(LadderRank.values().first())
        else -> ladderManager.nextEntry(rankEntry.rank)
    } }

    private val hidesCompleteTasks = options.hideCompleted // resolves immediately, determines eligibility for toggling hidden on/off

    // All goals for the current target rank
    private val allGoals: List<BaseRankGoal> by lazy { targetEntry!!.goals + targetEntry!!.mandatoryGoals }
    // Goals that should be shown to the user based on completion state
    private val activeGoals: MutableList<BaseRankGoal> by lazy { createActiveGoals() }
    private var mActiveGoalCategories: List<Any>? = null
    private val activeGoalCategories: List<Any>
        get() {
            if (mActiveGoalCategories == null) {
                regenerateCategoriesList()
            }
            return mActiveGoalCategories!!
        }

    private fun createActiveGoals() = when {
        targetEntry == null -> mutableListOf()
        options.hideCompleted -> {
            val completedGoals = ladderManager.getGoalStateList(allGoals).filter { it.status == COMPLETE }.map { it.goalId }
            allGoals.filterNot { completedGoals.contains(it.id.toLong()) }.toMutableList()
        }
        else -> allGoals.toMutableList()
    }

    private fun regenerateCategoriesList() {
        mActiveGoalCategories = activeGoals.groupBy { goal ->
            if (goal is SongsClearGoal) {
                goal.diffNum
            } else {
                null
            }
        }.flatMap { pair ->
            val headerString = pair.key?.let { diffNum ->
                resources.getString(R.string.level_header, diffNum)
            } ?: resources.getString(R.string.other_goals)
            listOf<Any>(headerString) + pair.value
        }
    }

    private val expandedItems = mutableListOf<BaseRankGoal>()
    private val completeItemCount get() = ladderManager.getGoalStateList(allGoals).count { it.status == COMPLETE }
    private val hiddenItemCount get() = ladderManager.getGoalStateList(allGoals).count { it.status == IGNORED }
    private var canIgnoreGoals: Boolean = true
    private val usesDiffNumberSections: Boolean = targetEntry?.let { it.rank >= LadderRank.PLATINUM1 } ?: false

    private val goalItemListener: LadderGoalItemView.LadderGoalItemListener = object: LadderGoalItemView.LadderGoalItemListener {

        override fun onStateToggle(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalState) {
            ladderManager.setGoalState(goalDB.goalId, when(goalDB.status) {
                COMPLETE -> INCOMPLETE
                else -> COMPLETE
            })
            refreshDbItem(itemView, item)
        }

        override fun onIgnoreClicked(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalState) {
            ladderManager.setGoalState(goalDB.goalId, when(goalDB.status) {
                IGNORED -> INCOMPLETE
                else -> IGNORED
            })
            refreshDbItem(itemView, item)
        }

        private fun refreshDbItem(itemView: LadderGoalItemView, item: BaseRankGoal) {
            val newDB = ladderManager.getGoalState(item)!!
            updateVisibility(item, newDB)
            updateCompleteCount()
            updateIgnoredStates()
            goalListListener?.onGoalStateChanged(item, newDB.status, 0)
            itemView.setGoalState(newDB)
        }

        override fun onExpandClicked(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalState) {
            expandedItems.remove(item) || expandedItems.add(item)
            if (usesDiffNumberSections) {
                adapter!!.notifyItemChanged(activeGoalCategories.indexOf(item))
            } else {
                adapter!!.notifyItemChanged(activeGoals.indexOf(item))
            }
        }

        override fun onLongPressed(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalState) {
            Log.v("rank goal", "long pressed")
        }
    }

    private val dataSource = object: RankGoalsAdapter.DataSource {
        override fun getGoals() = activeGoals
        override fun getGoalCategories(): List<Any> = activeGoalCategories
        override fun isGoalExpanded(item: BaseRankGoal) = expandedItems.contains(item)
        override fun isGoalMandatory(item: BaseRankGoal) = targetEntry?.mandatoryGoals?.contains(item) == true
        override fun canIgnoreGoals(): Boolean = canIgnoreGoals
        override fun getGoalStatus(item: BaseRankGoal) = ladderManager.getOrCreateGoalState(item)
        override fun getGoalProgress(item: BaseRankGoal) = ladderProgressManager.getGoalProgress(item)
    }

    val adapter: RankGoalsAdapter? = targetEntry?.let { target ->
        RankGoalsAdapter(
            rank = target,
            dataSource = dataSource,
            diffNumberSections = usesDiffNumberSections,
            listener = goalItemListener,
            goalListListener = goalListListener
        )
    }
    val shouldShowGoals get() = adapter != null

    val directionsText = MutableLiveData<String>()
    val countStatusText = MutableLiveData<String>()
    val countStatusVisibility = MutableLiveData<Int>()
    val countStatusArrowVisibility = MutableLiveData<Int>()
    val countStatusArrowRotation = MutableLiveData<Float>()

    init {
        val rankName = targetEntry?.rank?.nameRes?.let { resources.getString(it) }
        directionsText.value = rankName?.let { resources.getString(R.string.rank_directions, it) } ?: ""
        countStatusArrowVisibility.value = if (hidesCompleteTasks) View.VISIBLE else View.GONE
        if (shouldShowGoals) {
            updateCompleteCount()
            updateIgnoredStates()
        }
    }

    fun onGoalsCountClicked() {
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
            val status = goalStatuses.firstOrNull { it.goalId == goal.id.toLong() }
            if (status?.status == COMPLETE) {
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

        countStatusArrowRotation.value = if (options.hideCompleted) 0f else 90f
    }

    /**
     * Updates the visibility of a single goal
     */
    fun updateVisibility(goal: BaseRankGoal, goalDB: GoalState) {
        if (goalDB.status == COMPLETE && options.hideCompleted) {
            if (usesDiffNumberSections) {
                val index = activeGoalCategories.indexOf(goal)
                fun categoryCount() = activeGoalCategories.count { it is String }

                val previousCategoryCount = categoryCount()
                activeGoals.remove(goal)
                regenerateCategoriesList()
                val categoryRemoved = categoryCount() != previousCategoryCount

                adapter!!.notifyItemRemoved(index)
                if (categoryRemoved) {
                    adapter.notifyItemRemoved(index - 1) // category header is always one cell behind
                }
            } else {
                val index = activeGoals.indexOf(goal)
                activeGoals.removeAt(index)
                adapter!!.notifyItemRemoved(index)
            }
            if (activeGoals.isEmpty()) {
                adapter.notifyItemInserted(0)
            }
        }
    }

    private fun updateCompleteCount() {
        countStatusVisibility.value = View.VISIBLE
        countStatusText.value = resources.getString(
            R.string.goals_completed_format,
            completeItemCount,
            targetEntry?.requirements ?: 0
        )
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
