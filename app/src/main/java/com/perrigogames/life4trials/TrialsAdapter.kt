package com.perrigogames.life4trials

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.data.Trial
import com.perrigogames.life4trials.view.TrialItemView
import com.perrigogames.life4trials.view.TrialItemView.TrialViewHolder

class TrialsAdapter(private val context: Context,
                    private val trials: List<Trial>,
                    private val tiled: Boolean = false,
                    private val onItemClicked: (Int) -> Unit):
    RecyclerView.Adapter<TrialViewHolder>() {

    private val trialManager get() = context.life4app.trialManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TrialViewHolder(
            when (viewType) {
                ID_LIST -> TrialItemView.inflateListItem(context, parent, false)
                ID_TILE -> TrialItemView.inflateTileItem(context, parent, false)
                else -> throw IllegalArgumentException("Unsupported trial view type: $viewType")
            }
        )

    override fun onBindViewHolder(holder: TrialViewHolder, position: Int) {
        holder.trialItemView.trial = trials[position]
        holder.itemView.setOnClickListener { onItemClicked(position) }
        val bestSession = trialManager.bestTrial(trials[position].id)
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

    companion object {
        const val ID_LIST = 0
        const val ID_TILE = 1
    }
}