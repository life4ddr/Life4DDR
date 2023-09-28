package com.perrigogames.life4trials.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.LadderRankData
import com.perrigogames.life4trials.data.RankEntry
import com.perrigogames.life4trials.ui.rankdetails.RankListAdapter

/**
 * Fragment displaying the list of ladder ranks that can be obtained.
 */
class RankListActivityFragment : Fragment() {

    private val rankData: LadderRankData get() = (context!!.applicationContext as Life4Application).ladderRankData

    private val columnCount: Int
        get() = arguments?.getInt(ARG_COLUMN_COUNT) ?: 1

    private var listener: OnRankListInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_rank_details, container, false)

        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = RankListAdapter(rankData.ranks, listener)
            }
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
