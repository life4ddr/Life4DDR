package com.perrigogames.life4trials.ui.rankdetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.LadderRank
import com.perrigogames.life4trials.data.RankEntry
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.util.spannedText
import com.perrigogames.life4trials.util.visibilityBool
import com.perrigogames.life4trials.view.RankHeaderView
import kotlinx.android.synthetic.main.fragment_rank_details.view.*
import java.io.Serializable

class RankDetailsFragment : Fragment() {

    private val ladderManager get() = context!!.life4app.ladderManager

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
        val view = inflater.inflate(R.layout.fragment_rank_details, container, false)

        viewModel = RankDetailsViewModel(view.context, rankEntry, options, context!!.life4app.ladderManager, goalListListener)

        if (options.showHeader) {
            (view.stub_rank_header.inflate() as RankHeaderView).let {
                it.rank = rankEntry?.rank
                it.navigationListener = navigationListener
            }
            (view.text_directions.layoutParams as ConstraintLayout.LayoutParams).topToBottom = R.id.layout_rank_header
            (view.text_goals_hidden.layoutParams as ConstraintLayout.LayoutParams).topToBottom = R.id.layout_rank_header
        }

        viewModel.directionsText.observe(this, Observer<String> { view.text_directions.text = it.spannedText })
        viewModel.hiddenStatusText.observe(this, Observer<String> { view.text_goals_hidden.text = it })
        viewModel.hiddenStatusVisibility.observe(this, Observer<Int> { v ->
            view.text_goals_hidden.visibility = v
            (view.recycler_rank_details.layoutParams as ConstraintLayout.LayoutParams).let { params ->
                params.topToBottom = when {
                    v != View.GONE -> R.id.text_goals_hidden
                    options.showHeader -> R.id.layout_rank_header
                    else -> R.id.stub_rank_header
                }
            }
        })

        view.recycler_rank_details.apply {
            visibilityBool = viewModel.shouldShowGoals
            if (viewModel.shouldShowGoals) {
                adapter = viewModel.adapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
        }

        view.text_question.visibilityBool = !viewModel.shouldShowGoals && rankEntry != null
        view.text_no_goals.visibilityBool = !viewModel.shouldShowGoals && rankEntry == null
        view.switch_show_next.isChecked = options.showNextGoals
        view.switch_show_next.visibilityBool = options.allowNextSwitcher
        view.switch_show_next.setOnCheckedChangeListener { _, checked -> goalListListener?.onNextSwitchToggled(checked) }

        return view
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

    data class Options(val hideCompleted: Boolean = false,
                       val hideIgnored: Boolean = false,
                       val showHeader: Boolean = true,
                       val showNextGoals: Boolean = false,
                       val allowNextSwitcher: Boolean = true): Serializable

    companion object {
        const val KEY_RANK = "KEY_RANK"
        const val KEY_OPTIONS = "KEY_OPTIONS"

        fun newInstance(rank: LadderRank?,
                        options: Options = Options()) =
            RankDetailsFragment().apply {
                arguments = Bundle().apply {
                    putLong(KEY_RANK, rank?.stableId ?: 0)
                    putSerializable(KEY_OPTIONS, options)
                }
            }
    }
}
