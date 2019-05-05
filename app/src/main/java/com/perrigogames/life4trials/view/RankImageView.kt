package com.perrigogames.life4trials.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.perrigogames.life4trials.data.TrialRank

class RankImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ImageView(context, attrs, defStyleAttr) {

    var rank: TrialRank? = null
        set(v) {
            field = v
            setImageDrawable(if (v == null) null else
                ContextCompat.getDrawable(context, v.drawableRes))
        }
}