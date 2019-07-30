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
import com.perrigogames.life4trials.view.RankHeaderView
import kotlinx.android.synthetic.main.fragment_rank_details.view.*

class RankDetailsFragment(private val rankEntry: RankEntry,
                          private val options: Options = Options(),
                          private val navigationListener: RankHeaderView.NavigationListener? = null,
                          private val goalListListener: RankDetailsViewModel.OnGoalListInteractionListener? = null) : Fragment() {

    private lateinit var viewModel: RankDetailsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_rank_details, container, false)

        viewModel = RankDetailsViewModel(view.context, rankEntry, options, context!!.life4app.ladderManager, goalListListener)

        if (options.showHeader) {
            (view.stub_rank_header.inflate() as RankHeaderView).let {
                it.rank = rankEntry.rank
                it.navigationListener = navigationListener
            }
            (view.text_goals_hidden.layoutParams as ConstraintLayout.LayoutParams).topToBottom = R.id.layout_rank_header
        }

        viewModel.hiddenStatusText.observe(this, Observer<String> { text -> view.text_goals_hidden.text = text })
        viewModel.hiddenStatusVisibility.observe(this, Observer<Int> { v ->
            view.text_goals_hidden.visibility = v
            (view.fragment_rank_details.layoutParams as ConstraintLayout.LayoutParams).let { params ->
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

        view.fragment_rank_details.apply {
            adapter = viewModel.adapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

        return view
    }

    class Options(val hideCompleted: Boolean = false,
                  val hideIgnored: Boolean = false,
                  val showHeader: Boolean = true)
}
