package com.perrigogames.life4trials.ui.triallist

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.data.TrialType
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.view.JacketCornerView.CornerType.EVENT
import com.perrigogames.life4trials.view.JacketCornerView.CornerType.NEW
import com.perrigogames.life4trials.view.TrialItemView
import com.perrigogames.life4trials.view.TrialItemView.TrialViewHolder

class TrialListAdapter(private val context: Context,
                       private val trials: List<Trial>,
                       var featureNew: Boolean = false,
                       private val onItemClicked: (String, TrialType) -> Unit):
    RecyclerView.Adapter<TrialViewHolder>() {

    private val trialManager get() = context.life4app.trialManager

    private val mEventTrials get() = trials.filter { it.isEvent }
    private val mNewTrials get() = trials.filter { !it.isEvent && it.new && trialManager.bestTrial(it.id) == null }
    private val mOldTrials get() = trials.filterNot { t -> t.isEvent || newTrials.any { it.id == t.id } }

    private var eventTrials = mEventTrials
    private var newTrials = mNewTrials
    private var oldTrials = mOldTrials
    private val eSize get() = eventTrials.size
    private val nSize get() = newTrials.size
    private val oSize get() = oldTrials.size

    fun updateNewTrialsList() {
        eventTrials = mEventTrials
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
        holder.trialItemView.setCornerType(when {
            item.eventEnd != null -> EVENT
            featureNew && item.new && trialManager.bestTrial(item.id) == null -> NEW
            else -> null
        })
        holder.itemView.setOnClickListener { onItemClicked(item.id, item.type) }
        val bestSession = trialManager.bestTrial(item.id)
        if (!item.isEvent) {
            holder.trialItemView.setHighestRank(bestSession?.goalRank)
        }
        holder.trialItemView.setExScore(bestSession?.exScore)
    }

    override fun getItemCount() = trials.size

    override fun getItemViewType(position: Int) = ID_TILE

    fun notifyTrialChanged(trial: Trial) {
        notifyItemChanged(positionForItem(trial), trial)
    }

    private fun positionForItem(item: Trial) = when {
        item.isEvent -> eventTrials.indexOf(item)
        !featureNew -> trials.indexOf(item) + eSize
        newTrials.contains(item) -> newTrials.indexOf(item) + eSize
        else -> oldTrials.indexOf(item) + nSize + eSize
    }

    private fun itemForPosition(position: Int) = when {
        position < eSize -> eventTrials[position] // events always at the front
        !featureNew -> trials[position - eSize] // basic list
        position < nSize + eSize -> newTrials[position - eSize] // new trials up front
        else -> oldTrials[position - nSize - eSize] // followed by the old
    }

    companion object {
        const val ID_LIST = 0
        const val ID_TILE = 1
    }
}
