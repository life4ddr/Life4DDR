package com.perrigogames.life4trials.ui.ranklist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.LadderRankData
import com.perrigogames.life4trials.data.RankEntry
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.ui.rankdetails.RankListAdapter
import kotlinx.android.synthetic.main.fragment_rank_list.*

/**
 * Fragment displaying the list of ladder ranks that can be obtained.
 */
class RankListActivityFragment : Fragment() {

    private val rankData: LadderRankData get() = context!!.life4app.ladderManager.ladderData

    private val columnCount: Int
        get() = arguments?.getInt(ARG_COLUMN_COUNT) ?: 1

    private var listener: OnRankListInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_rank_list, container, false)

        with(recycler_rank_details) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter = RankListAdapter(rankData.rankRequirements, columnCount == 1, listener)
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnRankListInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnRankListInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnRankListInteractionListener {
        fun onListFragmentInteraction(item: RankEntry)
    }

    companion object {

        const val ARG_COLUMN_COUNT = "ARG_COLUMN_COUNT"

        @JvmStatic
        fun newInstance(columnCount: Int = 1) =
            RankListActivityFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
