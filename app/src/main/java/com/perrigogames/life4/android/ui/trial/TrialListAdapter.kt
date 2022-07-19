package com.perrigogames.life4.android.ui.trial

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.view.HeaderViewHolder
import com.perrigogames.life4.android.view.TrialItemView
import com.perrigogames.life4.enums.TrialType
import com.perrigogames.life4.model.TrialManager
import com.perrigogames.life4.viewmodel.TrialListState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TrialListAdapter(
    initialState: TrialListState,
    private val onItemClicked: (String, TrialType) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), KoinComponent {

    private val context: Context by inject()
    private val trialManager: TrialManager by inject()

    var state: TrialListState = initialState
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        ID_HEADER -> HeaderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_rank_goal_header, parent, false) as TextView
        )
        ID_TILE -> TrialViewHolder(when (viewType) {
            ID_TILE -> TrialItemView.inflate(context, parent, false)
            else -> throw IllegalArgumentException("Unsupported trial view type: $viewType")
        })
        else -> error("Unsupported view type $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val genericItem = state.displayTrials[position]
        when (holder) {
            is HeaderViewHolder -> {
                val headerText = (genericItem as TrialListState.TrialListItem.Header).text
                holder.bind(headerText)
            }
            is TrialViewHolder -> {
                val item = genericItem as TrialListState.TrialListItem.Trial
                holder.trialItemView.trial = item.trial
                holder.trialItemView.setCornerType(item.corner)
                holder.itemView.setOnClickListener { onItemClicked(item.trial.id, item.trial.type) }
                val bestSession = trialManager.bestSession(item.trial.id)
                if (!item.trial.isEvent) {
                    holder.trialItemView.setHighestRank(bestSession?.goalRank)
                }
                holder.trialItemView.setExScore(bestSession?.exScore?.toInt())
            }
        }
    }

    override fun getItemCount() = state.displayTrials.size

    override fun getItemViewType(position: Int) = when(state.displayTrials[position]) {
        is TrialListState.TrialListItem.Header -> ID_HEADER
        is TrialListState.TrialListItem.Trial -> ID_TILE
    }

    val spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return when (state.displayTrials[position]) {
                is TrialListState.TrialListItem.Header -> 2
                is TrialListState.TrialListItem.Trial -> 1
            }
        }
    }

    class TrialViewHolder(val trialItemView: TrialItemView): RecyclerView.ViewHolder(trialItemView)

    companion object {
        const val ID_TILE = 1
        const val ID_HEADER = 2
    }
}
