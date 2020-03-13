package com.perrigogames.life4trials.ui.ranklist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.perrigogames.life4.LadderRanksReplacedEvent
import com.perrigogames.life4.data.RankEntry
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.manager.LadderManager
import com.perrigogames.life4trials.ui.rankdetails.RankListAdapter
import kotlinx.android.synthetic.main.fragment_rank_list.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Fragment displaying the list of ladder ranks that can be obtained.
 */
class RankListFragment : Fragment(), KoinComponent {

    private val ladderManager: LadderManager by inject()
    private val eventBus: EventBus by inject()
    private val rankData get() = ladderManager.currentRequirements

    private val columnCount: Int
        get() = arguments?.getInt(ARG_COLUMN_COUNT) ?: 1

    private var listener: OnRankListInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_rank_list, container, false)

        with(view.recycler_rank_list) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount).apply {
                    spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(pos: Int) =
                            if (pos == 0) columnCount
                            else 1
                    }
                }
            }
            adapter = RankListAdapter(rankData.rankRequirements, ladderManager.getUserRank(), columnCount == 1, listener)
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
        eventBus.register(this)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        eventBus.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRankListUpdated(e: LadderRanksReplacedEvent) {
        view?.recycler_rank_list?.adapter?.notifyDataSetChanged()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnRankListInteractionListener {
        fun onListFragmentInteraction(item: RankEntry?)
    }

    companion object {

        const val ARG_COLUMN_COUNT = "ARG_COLUMN_COUNT"

        @JvmStatic
        fun newInstance(columnCount: Int = 1) =
            RankListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
