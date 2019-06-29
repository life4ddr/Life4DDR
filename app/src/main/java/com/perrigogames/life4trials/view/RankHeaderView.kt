package com.perrigogames.life4trials.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.perrigogames.life4trials.data.LadderRank
import kotlinx.android.synthetic.main.view_rank_header.view.*

/**
 * A special layout that displays a [RankImageView] and a corresponding label.
 */
class RankHeaderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    var rank: LadderRank? = null
        set(v) {
            field = v
            image_rank.rank = v
            text_rank_title.text = if (v != null) context.getString(v.nameRes) else ""
        }

    var navigationButtonsVisible: Boolean = false
        set(v) {
            field = v
            button_navigate_previous.visibility = if (v) VISIBLE else GONE
            button_navigate_next.visibility = if (v) VISIBLE else GONE
        }

    fun setIconSize(size: Int) {
        image_rank.apply {
            layoutParams = layoutParams.also {
                it.width = size
                it.height = size
            }
        }
    }
}