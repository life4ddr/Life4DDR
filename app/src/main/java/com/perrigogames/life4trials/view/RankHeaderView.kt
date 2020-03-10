package com.perrigogames.life4trials.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.perrigogames.life4trials.R
import com.perrigogames.life4.data.LadderRank
import com.perrigogames.life4trials.util.colorRes
import com.perrigogames.life4trials.util.groupNameRes
import com.perrigogames.life4trials.util.nameRes
import com.perrigogames.life4trials.util.visibilityBool
import kotlinx.android.synthetic.main.view_rank_header.view.*

/**
 * A special layout that displays a [RankImageView] and a corresponding label.
 */
class RankHeaderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var oldColors: ColorStateList

    override fun onFinishInflate() {
        super.onFinishInflate()
        oldColors = text_goal_title.textColors
        button_navigate_previous.setOnClickListener { navigationListener?.onPreviousClicked() }
        button_navigate_next.setOnClickListener { navigationListener?.onNextClicked() }
    }

    var rank: LadderRank? = null
        set(v) {
            field = v
            image_rank.rank = v
            updateTitle()
        }

    var navigationListener: NavigationListener? = null
        set(v) {
            field = v
            button_navigate_previous.visibilityBool = v != null
            button_navigate_next.visibilityBool = v != null
        }

    var genericTitles = false
        set(v) {
            field = v
            updateTitle()
        }

    fun setIconSize(size: Int) {
        image_rank.apply {
            layoutParams = layoutParams.also {
                it.width = size
                it.height = size
            }
        }
    }

    fun navigationButtonClicked(v: View) {
        val prev = v.id == R.id.button_navigate_previous
        if (prev) navigationListener?.onPreviousClicked() else
            navigationListener?.onNextClicked()
    }

    private fun updateTitle() = text_goal_title.apply {
        text = rank?.let { context.getString(if (genericTitles) it.groupNameRes else it.nameRes) } ?: context.getString(R.string.no_rank)
        if (rank == null) {
            setTextColor(oldColors)
        } else {
            setTextColor(rank?.let { ContextCompat.getColor(context, it.colorRes) } ?: 0)
        }
    }

    interface NavigationListener {

        fun onPreviousClicked()
        fun onNextClicked()
    }
}
