package com.perrigogames.life4trials.ui.rankdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.BaseRankGoal
import com.perrigogames.life4trials.data.RankEntry
import com.perrigogames.life4trials.db.GoalStatus
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.util.visibilityBool
import com.perrigogames.life4trials.view.RankHeaderView
import kotlinx.android.synthetic.main.fragment_rank_details.view.*

class RankDetailsFragment(private val rankEntry: RankEntry,
                          private val options: Options = Options(),
                          private val navigationListener: RankHeaderView.NavigationListener? = null,
                          private val goalListListener: OnGoalListInteractionListener? = null) : Fragment() {

    private val shouldShowGoals get() = rankEntry.allowedIgnores == 0 && options.showHeader

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_rank_details, container, false)
        val listener = object: OnGoalListInteractionListener {
            override fun onGoalStateChanged(item: BaseRankGoal, goalStatus: GoalStatus, hiddenGoals: Int) {
                updateHiddenCount(view, hiddenGoals)
                goalListListener?.onGoalStateChanged(item, goalStatus, hiddenGoals)
            }

            override fun onRankSubmitClicked() {
                goalListListener?.onRankSubmitClicked()
            }
        }
        if (options.showHeader) {
            (view.stub_rank_header.inflate() as RankHeaderView).let {
                it.rank = rankEntry.rank
                it.navigationListener = navigationListener
            }
            (view.fragment_rank_details.layoutParams as ConstraintLayout.LayoutParams).topToBottom = R.id.layout_rank_header
        }

        view.text_goals_hidden.visibilityBool = shouldShowGoals

        view.button_use_rank.apply {
            visibilityBool = options.showSetRank
            setOnClickListener { goalListListener?.onUseRankClicked() }
        }

        var hidden = 0
        view.fragment_rank_details.apply {
            adapter = RankGoalsAdapter(rankEntry, options, context.life4app.ladderManager, listener)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
//            hidden = (adapter as RankGoalsAdapter).ignoredItems
        }
        updateHiddenCount(view, hidden)

        return view
    }

    private fun updateHiddenCount(v: View, hidden: Int) {
        v.text_goals_hidden.visibility = if (shouldShowGoals) View.GONE else View.VISIBLE
        v.text_goals_hidden.text = getString(R.string.goals_hidden_format, hidden, rankEntry.allowedIgnores)
        if (rankEntry.allowedIgnores == hidden) {
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnGoalListInteractionListener {
        fun onGoalStateChanged(item: BaseRankGoal, goalStatus: GoalStatus, hiddenGoals: Int) {}
        fun onRankSubmitClicked() {}
        fun onUseRankClicked() {}
    }

    class Options(val hideCompleted: Boolean = false,
                  val hideIgnored: Boolean = false,
                  val showHeader: Boolean = true,
                  val showSetRank: Boolean = false)
}
