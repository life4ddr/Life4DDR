package com.perrigogames.life4trials.ui.rankdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.BaseRankGoal
import com.perrigogames.life4trials.data.RankEntry
import com.perrigogames.life4trials.db.GoalStatus
import com.perrigogames.life4trials.db.GoalStatusDB
import com.perrigogames.life4trials.manager.LadderManager
import com.perrigogames.life4trials.ui.rankdetails.RankDetailsFragment.OnGoalListInteractionListener
import com.perrigogames.life4trials.view.LadderGoalItemView

/**
 * [RecyclerView.Adapter] that can display a [RankEntry] and makes a call to the
 * specified [OnGoalListInteractionListener].
 */
class RankGoalsAdapter(private val rank: RankEntry,
                       private val ladderManager: LadderManager,
                       private var listener: OnGoalListInteractionListener? = null) :
    RecyclerView.Adapter<RankGoalsAdapter.ViewHolder>() {

    private val items = rank.goals

    private val goalItemListener = object: LadderGoalItemView.LadderGoalItemListener {

        override fun createGoalDB(item: BaseRankGoal): GoalStatusDB {
            return ladderManager.getOrCreateGoalStatus(item)
        }
        override fun onStateToggle(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalStatusDB) {
            ladderManager.setGoalState(goalDB, when(goalDB.status) {
                GoalStatus.COMPLETE -> GoalStatus.INCOMPLETE
                else -> GoalStatus.COMPLETE
            })
            listener?.onGoalStateChanged(item, goalDB.status)
        }

        override fun onIgnoreClicked(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalStatusDB) {
            ladderManager.setGoalState(goalDB, when(goalDB.status) {
                GoalStatus.IGNORED -> GoalStatus.INCOMPLETE
                else -> GoalStatus.IGNORED
            })
            listener?.onGoalStateChanged(item, goalDB.status)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_rank_goal, parent, false) as LadderGoalItemView
        itemView.listener = goalItemListener
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.view.setGoal(item)

        with(holder.view) {
            tag = item
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val view: LadderGoalItemView) : RecyclerView.ViewHolder(view)
}
