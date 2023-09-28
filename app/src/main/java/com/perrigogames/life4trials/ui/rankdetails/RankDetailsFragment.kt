package com.perrigogames.life4trials.ui.rankdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.RankEntry
import kotlinx.android.synthetic.main.fragment_rank_details.view.*

class RankDetailsFragment(private val rankEntry: RankEntry) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_rank_details, container, false)
        view.fragment_rank_details.apply {
            adapter = RankDetailsAdapter(rankEntry, null)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        return view
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnGoalListInteractionListener {
        fun onGoalListInteraction(item: RankEntry)
    }

    companion object {
        fun newInstance(rankEntry: RankEntry) = RankDetailsFragment(rankEntry)
    }
}
