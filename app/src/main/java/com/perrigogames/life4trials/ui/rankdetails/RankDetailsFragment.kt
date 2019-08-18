package com.perrigogames.life4trials.ui.rankdetails

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
import com.perrigogames.life4trials.data.RankEntry
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.util.spannedText
import com.perrigogames.life4trials.util.visibilityBool
import com.perrigogames.life4trials.view.RankHeaderView
import kotlinx.android.synthetic.main.fragment_rank_details.view.*

class RankDetailsFragment(private val rankEntry: RankEntry?,
                          private val options: Options = Options(),
                          private val navigationListener: RankHeaderView.NavigationListener? = null,
                          private val goalListListener: RankDetailsViewModel.OnGoalListInteractionListener? = null) : Fragment() {

    private lateinit var viewModel: RankDetailsViewModel

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

//        view.button_use_rank.apply {
//            visibilityBool = options.showSetRank
//            setOnClickListener { goalListListener?.onUseRankClicked() }
//        }

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

    data class Options(val hideCompleted: Boolean = false,
                       val hideIgnored: Boolean = false,
                       val showHeader: Boolean = true,
                       val showNextGoals: Boolean = false,
                       val allowNextSwitcher: Boolean = true)
}
