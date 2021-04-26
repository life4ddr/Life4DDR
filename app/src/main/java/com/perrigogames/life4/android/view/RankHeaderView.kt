package com.perrigogames.life4.android.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.colorRes
import com.perrigogames.life4.android.databinding.ViewRankHeaderBinding
import com.perrigogames.life4.android.groupNameRes
import com.perrigogames.life4.android.nameRes
import com.perrigogames.life4.android.util.visibilityBool

/**
 * A special layout that displays a [RankImageView] and a corresponding label.
 */
class RankHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var oldColors: ColorStateList

    private val binding = ViewRankHeaderBinding.bind(this)

    override fun onFinishInflate() {
        super.onFinishInflate()
        oldColors = binding.textGoalTitle.textColors
        binding.buttonNavigatePrevious.setOnClickListener { navigationListener?.onPreviousClicked() }
        binding.buttonNavigateNext.setOnClickListener { navigationListener?.onNextClicked() }
    }

    var rank: LadderRank? = null
        set(v) {
            field = v
            binding.imageRank.rank = v
            updateTitle()
        }

    var navigationListener: NavigationListener? = null
        set(v) {
            field = v
            binding.buttonNavigatePrevious.visibilityBool = v != null
            binding.buttonNavigateNext.visibilityBool = v != null
        }

    var genericTitles = false
        set(v) {
            field = v
            updateTitle()
        }

    fun setIconSize(size: Int) {
        binding.imageRank.apply {
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

    private fun updateTitle() = binding.textGoalTitle.apply {
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
