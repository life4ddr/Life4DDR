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
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_LIST_SHOW_EX
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_LIST_TINT_COMPLETED
import com.perrigogames.life4trials.data.Trial
import com.perrigogames.life4trials.data.TrialRank
import com.perrigogames.life4trials.util.SharedPrefsUtil
import kotlinx.android.synthetic.main.item_trial_list_item.view.image_badge_1
import kotlinx.android.synthetic.main.item_trial_list_item.view.image_badge_2
import kotlinx.android.synthetic.main.item_trial_list_item.view.image_badge_3
import kotlinx.android.synthetic.main.item_trial_list_item.view.image_badge_4
import kotlinx.android.synthetic.main.item_trial_list_item.view.image_badge_5
import kotlinx.android.synthetic.main.item_trial_list_item.view.image_badge_6
import kotlinx.android.synthetic.main.item_trial_list_item.view.image_rank
import kotlinx.android.synthetic.main.item_trial_tile_item.view.*

/**
 * A custom [View] for displaying a single [Trial].
 */
class TrialItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    val tintCompleted: Boolean
        get() = SharedPrefsUtil.getUserFlag(context, KEY_LIST_TINT_COMPLETED, false)
    val showEx: Boolean
        get() = SharedPrefsUtil.getUserFlag(context, KEY_LIST_SHOW_EX, false)
    var showNew: Boolean = false
        set(v) {
            field = v
            update()
        }

    var trial: Trial? = null
        set(v) {
            field = v
            (image_rank as TrialJacketView).trial = v
            update()
        }

    private fun update() {
        image_badge_1.visibility = GONE
        image_badge_2.visibility = GONE
        image_badge_3.visibility = GONE
        image_badge_4.visibility = GONE
        image_badge_5.visibility = GONE
        image_badge_6.visibility = GONE

        image_new.visibility = if (showNew) View.VISIBLE else View.GONE
        text_new.visibility = if (showNew) View.VISIBLE else View.GONE
    }

    fun setupRankList(rank: TrialRank?) = trial?.let { t ->
        t.goals?.forEach { goal ->
            (when(goal.rank) {
                TrialRank.SILVER -> image_badge_1
                TrialRank.GOLD -> image_badge_2
                TrialRank.DIAMOND -> image_badge_3
                TrialRank.COBALT -> image_badge_4
                TrialRank.AMETHYST -> image_badge_5
                TrialRank.EMERALD -> image_badge_6
            }).visibility = View.VISIBLE
        }
        setupRank(image_badge_1, TrialRank.SILVER, rank)
        setupRank(image_badge_2, TrialRank.GOLD, rank)
        setupRank(image_badge_3, TrialRank.DIAMOND, rank)
        setupRank(image_badge_4, TrialRank.COBALT, rank)
        setupRank(image_badge_5, TrialRank.AMETHYST, rank)
        setupRank(image_badge_6, TrialRank.EMERALD, rank)
    }

    fun setHighestRank(rank: TrialRank?) {
        (image_rank as TrialJacketView).let { view ->
            view.rank = rank
            view.tintOnRank = if (tintCompleted) TrialRank.values().last() else null
        }
    }

    fun setExScore(exScore: Int?) {
        (image_rank as TrialJacketView).let { view ->
            view.exScore = if (showEx) exScore else null
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
