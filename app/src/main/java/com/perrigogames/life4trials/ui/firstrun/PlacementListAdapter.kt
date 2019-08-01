package com.perrigogames.life4trials.ui.firstrun

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.Trial

class PlacementListAdapter(private val placements: List<Trial>,
                           private val onPlacementSelected: (String) -> Unit):
    RecyclerView.Adapter<PlacementListAdapter.PlacementViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacementViewHolder =
        PlacementViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.item_placement_overview, parent, false) as PlacementOverviewView)

    override fun getItemCount() = placements.size

    override fun onBindViewHolder(holder: PlacementViewHolder, position: Int) {
        val item = placements[position]
        holder.bind(item)
        holder.itemView.setOnClickListener { onPlacementSelected(item.id) }
    }

    inner class PlacementViewHolder(val view: PlacementOverviewView) : RecyclerView.ViewHolder(view) {

        fun bind(placement: Trial) {
            view.rank = placement.placement_rank
            view.songs = placement.songs
        }
    }
}