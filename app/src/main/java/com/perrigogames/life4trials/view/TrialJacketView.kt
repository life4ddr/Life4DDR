package com.perrigogames.life4trials.view

import android.content.Context
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

    var trial: Trial? = null
        set(v) {
            field = v
            text_trial_title.text = v?.name
            text_trial_difficulty.text = v?.difficulty?.toString()
            if (v != null) {
                Glide.with(this).load(v.jacketUrl(resources)).into(image_trial_jacket)
            }
        }

    var rank: TrialRank? = null
        set(v) {
            field = v
            if (rank != null) {
                image_badge_highest.visibility = View.VISIBLE
                image_badge_highest.setImageDrawable(ContextCompat.getDrawable(context, rank!!.drawableRes))
            } else {
                image_badge_highest.visibility = View.GONE
            }
        }
}