package com.perrigogames.life4trials.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.perrigogames.life4trials.data.Trial
import com.perrigogames.life4trials.data.TrialRank
import kotlinx.android.synthetic.main.view_trial_jacket.view.*

class TrialJacketView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    var tintOnRank: TrialRank? = null
        set(v) {
            field = v
            updateRankTint()
        }

    var trial: Trial? = null
        set(v) {
            field = v
            text_trial_title.text = v?.name
            text_trial_difficulty.text = v?.difficulty?.toString()
            if (v != null) {
                if (v.shouldShowTitle) {
                    text_trial_title.visibility = View.VISIBLE
                    image_trial_jacket.setImageDrawable(null)
                } else {
                    text_trial_title.visibility = View.GONE
                    Glide.with(this).load(v.jacketUrl(resources, 350)).into(image_trial_jacket)
                }
            }
        }

    var rank: TrialRank? = null
        set(v) {
            field = v
            if (rank != null) {
                image_badge_highest.setImageDrawable(ContextCompat.getDrawable(context, rank!!.drawableRes))
                image_badge_highest_center.setImageDrawable(ContextCompat.getDrawable(context, rank!!.drawableRes))
            }
            updateRankTint()
        }

    private fun updateRankTint() {
        if (rank != null && rank == tintOnRank) {
            view_foreground_tint.visibility = View.VISIBLE
            (view_foreground_tint.drawable as ColorDrawable).color = ContextCompat.getColor(context, rank!!.color)
            image_badge_highest.visibility = View.GONE
            image_badge_highest_center.visibility = View.VISIBLE
        } else {
            view_foreground_tint.visibility = View.GONE
            image_badge_highest.visibility = if (rank != null) View.VISIBLE else View.GONE
            image_badge_highest_center.visibility = View.GONE
        }
    }
}