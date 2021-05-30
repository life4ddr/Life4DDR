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

    private val hidesCompleteTasks = options.hideNonActive // resolves immediately, determines eligibility for toggling hidden on/off

    // All goals for the current target rank
    private val allGoals: List<BaseRankGoal> by lazy { targetEntry!!.goals + targetEntry!!.mandatoryGoals }
    // Goals that should be shown to the user based on completion state
    private var lastActiveGoals: List<BaseRankGoal> = activeGoals
    private var lastActiveGoalCategories: List<Any> = activeGoalCategories

    private val activeGoals: List<BaseRankGoal>
        get() = when {
            targetEntry == null -> mutableListOf()
            options.hideNonActive -> {
                val completedGoals = ladderManager
                    .getGoalStateList(allGoals)
                    .filter { it.status != INCOMPLETE }
                    .map { it.goalId }
                allGoals.filterNot { completedGoals.contains(it.id.toLong()) }.toMutableList()
            }
            else -> allGoals.toMutableList()
        }

    private val activeGoalCategories: List<Any>
        get() = activeGoals.groupBy { goal ->
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
            refreshAllVisibilities(isGrowing = false) // either shrinking or staying the same, can't uncheck a hidden item
            updateCompleteCount()
            updateIgnoredStates()
            goalListListener?.onGoalStateChanged(item, newDB.status, 0)
            itemView.setGoalState(newDB)
        }

        override fun onExpandClicked(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalState) {
            expandedItems.remove(item) || expandedItems.add(item)
            adapter!!.notifyItemChanged(if (usesDiffNumberSections) {
                lastActiveGoalCategories.indexOf(item)
            } else {
                lastActiveGoals.indexOf(item)
            })
        }

        override fun onLongPressed(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalState) {
            Log.v("rank goal", "long pressed")
        }
    }

    private val dataSource = object: RankGoalsAdapter.DataSource {
        override fun getGoals() = when {
            usesDiffNumberSections -> lastActiveGoalCategories
            else -> lastActiveGoals
        }
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
        options.hideNonActive = !options.hideNonActive
        countStatusArrowRotation.value = if (options.hideNonActive) 0f else 90f
        refreshAllVisibilities(isGrowing = !options.hideNonActive)
    }

    private fun refreshAllVisibilities(isGrowing: Boolean) {
        // Update the live lists so data sources have new information, but keep copies we can operate on
        val oldList = when (usesDiffNumberSections) {
            true -> lastActiveGoalCategories
            false -> lastActiveGoals
        }
        lastActiveGoals = activeGoals
        lastActiveGoalCategories = activeGoalCategories
        val newList = when (usesDiffNumberSections) {
            true -> lastActiveGoalCategories
            false -> lastActiveGoals
        }

        if (isGrowing) { // new list is longer, add items sequentially
            var oldIdx = 0
            newList.forEachIndexed { newIdx, item ->
                when {
                    oldIdx < oldList.count() && oldList[oldIdx] == item -> oldIdx += 1
                    newIdx == 0 && oldList.isEmpty() -> adapter?.notifyItemChanged(0) // remove the Rank Up panel
                    else -> adapter?.notifyItemInserted(newIdx)
                }
            }
        } else { // new list is shorter, remove items reverse-sequentially
            var newIdx = newList.size - 1
            oldList.reversed().forEachIndexed { idx, item ->
                val oldIdx = (oldList.count() - 1) - idx
                when {
                    newIdx >= 0 && newList[newIdx] == item -> newIdx -= 1
                    oldIdx == 0 && newList.isEmpty() -> adapter?.notifyItemChanged(0)
                    else -> adapter?.notifyItemRemoved(oldIdx)
                }
            }
        }
        if (newList.isEmpty()) { // add the Rank Up panel
            adapter?.notifyItemInserted(0)
        }

        if (oldList.isEmpty() || newList.isEmpty()) {
            adapter?.notifyDataSetChanged()
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
