package com.perrigogames.life4.android.ui.rankdetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeTransform
import androidx.transition.TransitionManager
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.databinding.FragmentRankDetailsBinding
import com.perrigogames.life4.android.util.CommonSizes
import com.perrigogames.life4.android.util.spannedText
import com.perrigogames.life4.android.util.visibilityBool
import com.perrigogames.life4.android.view.PaddingItemDecoration
import com.perrigogames.life4.android.view.RankHeaderView
import com.perrigogames.life4.data.RankEntry
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.model.LadderManager
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.Serializable

class RankDetailsFragment : Fragment(), KoinComponent {

    private val ladderManager: LadderManager by inject()

    private var _binding: FragmentRankDetailsBinding? = null
    private val binding get() = _binding!!

    private var navigationListener: RankHeaderView.NavigationListener? = null
    private var goalListListener: RankDetailsViewModel.OnGoalListInteractionListener? = null

    private lateinit var viewModel: RankDetailsViewModel
    private var rankEntry: RankEntry? = null
    private lateinit var options: Options

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            rankEntry = ladderManager.findRankEntry(LadderRank.parse(it.getLong(KEY_RANK)))
            options = (it.getSerializable(KEY_OPTIONS) as? Options) ?: Options()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRankDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel = RankDetailsViewModel(resources, rankEntry, options, goalListListener)

        if (options.showHeader) {
            (binding.stubRankHeader.inflate() as RankHeaderView).let {
                it.rank = rankEntry?.rank
                it.navigationListener = navigationListener
            }
            (binding.textDirections.layoutParams as ConstraintLayout.LayoutParams).topToBottom = R.id.layout_rank_header
            (binding.textGoalsCount.layoutParams as ConstraintLayout.LayoutParams).topToBottom = R.id.layout_rank_header
        }

        viewModel.directionsText.observe(viewLifecycleOwner) { binding.textDirections.text = it.spannedText }
        viewModel.countStatusText.observe(viewLifecycleOwner) { binding.textGoalsCount.text = it }
        viewModel.countStatusVisibility.observe(viewLifecycleOwner) { v ->
            binding.textGoalsCount.visibility = v
            (binding.recyclerRankDetails.layoutParams as ConstraintLayout.LayoutParams).let { params ->
                params.topToBottom = when {
                    v != View.GONE -> R.id.text_goals_count
                    options.showHeader -> R.id.layout_rank_header
                    else -> R.id.stub_rank_header
                }
            }
        }
        viewModel.countStatusArrowVisibility.observe(viewLifecycleOwner) { v ->
            binding.imageGoalsCountArrow.visibility = v
        }
        viewModel.countStatusArrowRotation.observe(viewLifecycleOwner) { r ->
            TransitionManager.beginDelayedTransition(
                view,
                ChangeTransform().excludeTarget(binding.recyclerRankDetails, true)
            )
            binding.imageGoalsCountArrow.rotation = r
        }

        binding.recyclerRankDetails.apply {
            visibilityBool = viewModel.shouldShowGoals
            if (viewModel.shouldShowGoals) {
                adapter = viewModel.adapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                val padding = CommonSizes.contentPaddingMed(resources)
                addItemDecoration(PaddingItemDecoration(padding, 0, padding, padding))
            }
        }

        binding.textQuestion.visibilityBool = !viewModel.shouldShowGoals && rankEntry != null
        binding.textNoGoals.visibilityBool = !viewModel.shouldShowGoals && rankEntry == null
        binding.switchShowNext.apply {
            isChecked = options.showNextGoals
            visibilityBool = options.allowNextSwitcher
            setOnCheckedChangeListener { _, checked -> goalListListener?.onNextSwitchToggled(checked) }
        }
        binding.textGoalsCount.setOnClickListener { onGoalsCountTextClicked() }
        binding.imageGoalsCountArrow.setOnClickListener { onGoalsCountTextClicked() }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onGoalsCountTextClicked() {
        viewModel.onGoalsCountClicked()
        binding.recyclerRankDetails.scrollToPosition(0)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RankHeaderView.NavigationListener) {
            navigationListener = context
        }
        if (context is RankDetailsViewModel.OnGoalListInteractionListener) {
            goalListListener = context
        } else {
            throw RuntimeException("$context must implement OnGoalListInteractionListener")
        }
    }

    data class Options(
        var hideNonActive: Boolean = false,
        val showHeader: Boolean = true,
        val showNextGoals: Boolean = false,
        val allowNextSwitcher: Boolean = true
    ): Serializable

    companion object {
        const val KEY_RANK = "KEY_RANK"
        const val KEY_OPTIONS = "KEY_OPTIONS"

        fun newInstance(
            rank: LadderRank?,
            options: Options = Options()
        ) = RankDetailsFragment().apply {
            arguments = Bundle().apply {
                putLong(KEY_RANK, rank?.stableId ?: 0)
                putSerializable(KEY_OPTIONS, options)
            }
        }
    }
}
