package com.perrigogames.life4trials.ui.triallist

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.data.Trial
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.view.TrialItemView
import com.perrigogames.life4trials.view.TrialItemView.TrialViewHolder

class TrialListAdapter(private val context: Context,
                       private val trials: List<Trial>,
                       private val tiled: Boolean = false,
                       var featureNew: Boolean = false,
                       private val onItemClicked: (String) -> Unit):
    RecyclerView.Adapter<TrialViewHolder>() {

    private val trialManager get() = context.life4app.trialManager

    private val mNewTrials get() = trials.filter { it.new && trialManager.bestTrial(it.id) == null }
    private val mOldTrials get() = trials.filterNot { t -> newTrials.any { it.id == t.id } }

    private var newTrials = mNewTrials
    private var oldTrials = mOldTrials

    fun updateNewTrialsList() {
        newTrials = mNewTrials
        oldTrials = mOldTrials
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TrialViewHolder(when (viewType) {
            ID_LIST -> TrialItemView.inflateListItem(context, parent, false)
            ID_TILE -> TrialItemView.inflateTileItem(context, parent, false)
            else -> throw IllegalArgumentException("Unsupported trial view type: $viewType")
        })

    override fun onBindViewHolder(holder: TrialViewHolder, position: Int) {
        val item = itemForPosition(position)
        holder.trialItemView.trial = item
        holder.trialItemView.showNew = featureNew && item.new && trialManager.bestTrial(item.id) == null
        holder.itemView.setOnClickListener { onItemClicked(item.id) }
        val bestSession = trialManager.bestTrial(item.id)
        if (tiled) {
            holder.trialItemView.setHighestRank(bestSession?.goalRank)
            holder.trialItemView.setExScore(bestSession?.exScore)
        } else {
            holder.trialItemView.setupRankList(bestSession?.goalRank)
            holder.trialItemView.setExScore(bestSession?.exScore)
        }
    }

    override fun getItemCount() = trials.size

    override fun getItemViewType(position: Int) = if (tiled) ID_TILE else ID_LIST

    fun notifyTrialChanged(trial: Trial) {
        notifyItemChanged(positionForItem(trial), trial)
    }

    private fun positionForItem(item: Trial) = when {
        !featureNew -> trials.indexOf(item)
        newTrials.contains(item) -> newTrials.indexOf(item)
        else -> oldTrials.indexOf(item) + newTrials.size
    }

    private fun itemForPosition(position: Int) = when {
        !featureNew -> trials[position] // basic list
        position < newTrials.size -> newTrials[position] // new trials up front
        else -> oldTrials[position - newTrials.size] // followed by the old
    }

    companion object {
        const val ID_LIST = 0
        const val ID_TILE = 1
    }
}