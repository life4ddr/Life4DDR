package com.perrigogames.life4trials.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_LIST_TINT_COMPLETED
import com.perrigogames.life4trials.data.Trial
import com.perrigogames.life4trials.data.TrialRank
import com.perrigogames.life4trials.util.SharedPrefsUtils
import kotlinx.android.synthetic.main.item_trial_list_item.view.*

/**
 * A custom [View]
 */
class TrialItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    var trial: Trial? = null
        set(v) {
            field = v
            (view_trial_jacket as TrialJacketView).trial = v
            update()
        }

    private fun update() {
        image_badge_1.visibility = GONE
        image_badge_2.visibility = GONE
        image_badge_3.visibility = GONE
        image_badge_4.visibility = GONE
        image_badge_5.visibility = GONE
    }

    fun setupRankList(rank: TrialRank?) = trial?.let { t ->
        t.goals.forEach { goal ->
            (when(goal.rank) {
                TrialRank.SILVER -> image_badge_1
                TrialRank.GOLD -> image_badge_2
                TrialRank.DIAMOND -> image_badge_3
                TrialRank.COBALT -> image_badge_4
                TrialRank.AMETHYST -> image_badge_5
            }).visibility = View.VISIBLE
        }
        setupRank(image_badge_1, TrialRank.SILVER, rank)
        setupRank(image_badge_2, TrialRank.GOLD, rank)
        setupRank(image_badge_3, TrialRank.DIAMOND, rank)
        setupRank(image_badge_4, TrialRank.COBALT, rank)
        setupRank(image_badge_5, TrialRank.AMETHYST, rank)
    }

    fun setupHighestRank(rank: TrialRank?) {
        (view_trial_jacket as TrialJacketView).let { view ->
            view.rank = rank
            if (SharedPrefsUtils.getUserFlag(context, KEY_LIST_TINT_COMPLETED, false)) {
                view.tintOnRank = TrialRank.AMETHYST
            }
        }
    }

    private fun setupRank(imageView: ImageView, rank: TrialRank, target: TrialRank? = null) {
        imageView.setImageDrawable(ContextCompat.getDrawable(context, rank.drawableRes))
        imageView.alpha = if (target == null || rank.ordinal > target.ordinal) 0.2f else 1f
    }

    class TrialViewHolder(val trialItemView: TrialItemView): RecyclerView.ViewHolder(trialItemView)

    companion object {

        fun inflateListItem(context: Context, parent: ViewGroup, attachToRoot: Boolean) =
            LayoutInflater.from(context).inflate(R.layout.item_trial_list_item, parent, attachToRoot) as TrialItemView

        fun inflateTileItem(context: Context, parent: ViewGroup, attachToRoot: Boolean) =
            LayoutInflater.from(context).inflate(R.layout.item_trial_tile_item, parent, attachToRoot) as TrialItemView
    }
}
