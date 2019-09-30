package com.perrigogames.life4trials.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_LIST_SHOW_EX
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_LIST_TINT_COMPLETED
import com.perrigogames.life4trials.data.Trial
import com.perrigogames.life4trials.data.TrialRank
import com.perrigogames.life4trials.util.SharedPrefsUtil
import kotlinx.android.synthetic.main.item_trial_list_item.view.*

/**
 * A custom [View] for displaying a single [Trial].
 */
class TrialItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    val tintCompleted: Boolean
        get() = SharedPrefsUtil.getUserFlag(context, KEY_LIST_TINT_COMPLETED, false)
    val showEx: Boolean
        get() = trial?.isEvent == true || SharedPrefsUtil.getUserFlag(context, KEY_LIST_SHOW_EX, false)
    private var usesBadgeList = false

    fun setCornerType(v: JacketCornerView.CornerType?) {
        (image_rank as TrialJacketView).setCornerType(v)
    }

    var trial: Trial? = null
        set(v) {
            field = v
            (image_rank as TrialJacketView).trial = v
            update()
        }

    private fun update() {
        if (usesBadgeList) {
            image_badge_1.visibility = GONE
            image_badge_2.visibility = GONE
            image_badge_3.visibility = GONE
            image_badge_4.visibility = GONE
            image_badge_5.visibility = GONE
            image_badge_6.visibility = GONE
        }
    }

    fun setHighestRank(rank: TrialRank?) {
        (image_rank as TrialJacketView).let { view ->
            view.rank = rank
            view.tintOnRank = if (trial?.isEvent != true && tintCompleted) TrialRank.values().last() else null
        }
    }

    fun setExScore(exScore: Int?) {
        (image_rank as TrialJacketView).let { view ->
            view.showExRemaining = trial?.isEvent == true ||
                    SharedPrefsUtil.getUserFlag(context, SettingsActivity.KEY_LIST_SHOW_EX_REMAINING, false)
            view.exScore = if (showEx) exScore else null
        }
    }

    class TrialViewHolder(val trialItemView: TrialItemView): RecyclerView.ViewHolder(trialItemView)

    companion object {

        fun inflateListItem(context: Context, parent: ViewGroup, attachToRoot: Boolean) =
            LayoutInflater.from(context).inflate(R.layout.item_trial_list_item, parent, attachToRoot) as TrialItemView

        fun inflateTileItem(context: Context, parent: ViewGroup, attachToRoot: Boolean) =
            LayoutInflater.from(context).inflate(R.layout.item_trial_tile_item, parent, attachToRoot) as TrialItemView
    }
}
