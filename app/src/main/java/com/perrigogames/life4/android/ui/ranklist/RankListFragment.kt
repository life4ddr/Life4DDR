package com.perrigogames.life4.android.ui.ranklist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.perrigogames.life4.LadderRanksReplacedEvent
import com.perrigogames.life4.android.databinding.FragmentRankListBinding
import com.perrigogames.life4.data.RankEntry
import com.perrigogames.life4.model.LadderDataManager
import com.perrigogames.life4.model.UserRankManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Fragment displaying the list of ladder ranks that can be obtained.
 */
class RankListFragment : Fragment(), KoinComponent {

    private val ladderDataManager: LadderDataManager by inject()
    private val userRankManager: UserRankManager by inject()

    private var _binding: FragmentRankListBinding? = null
    private val binding get() = _binding!!

    private val rankData get() = ladderDataManager.currentRequirements

    private val columnCount: Int
        get() = arguments?.getInt(ARG_COLUMN_COUNT) ?: 1

    private var listener: OnRankListInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRankListBinding.inflate(inflater, container, false)

        with(binding.recyclerRankList) {
            val ranks = rankData
                .rankRequirements
                .groupBy { it.rank.group }
            adapter = RankListAdapter(ranks, userRankManager.currentRank, listener)
            layoutManager = GridLayoutManager(context, columnCount).apply {
                spanSizeLookup = (adapter as RankListAdapter).spanSizeLookup(columnCount)
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    // FIXME EventBus
    fun onRankListUpdated(e: LadderRanksReplacedEvent) {
        binding.recyclerRankList.adapter?.notifyDataSetChanged()
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
        fun newInstance(columnCount: Int = 5) =
            RankListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
