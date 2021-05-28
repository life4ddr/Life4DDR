package com.perrigogames.life4.android.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.BOTTOM
import androidx.constraintlayout.widget.ConstraintSet.TOP
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.colorRes
import com.perrigogames.life4.android.databinding.MergeTrialJacketBinding
import com.perrigogames.life4.android.drawableRes
import com.perrigogames.life4.android.util.circularProgressDrawable
import com.perrigogames.life4.android.util.jacketResId
import com.perrigogames.life4.android.util.visibilityBool
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.enums.TrialRank

class TrialJacketView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: MergeTrialJacketBinding

    init {
        LayoutInflater.from(context).inflate(R.layout.merge_trial_jacket, this)
        binding = MergeTrialJacketBinding.bind(this)
    }

    var tintOnRank: TrialRank? = null
        set(v) {
            field = v
            updateRankTint()
        }

    var trial: Trial? = null
        set(v) {
            field = v
            binding.textTrialTitle.text = v?.name
            binding.imageTrialDifficulty.visibilityBool = v?.difficulty != null
            binding.textTrialDifficulty.visibilityBool = v?.difficulty != null
            binding.textTrialDifficulty.text = v?.difficulty?.toString()
            binding.textTrialTitle.visibility = View.GONE
            if (v != null) {
                val resId = v.jacketResId(context)
                if (v.coverUrl != null && (resId == R.drawable.trial_default || v.coverOverride)) {
                    Glide.with(this)
                        .load(v.coverUrl)
                        .placeholder(circularProgressDrawable(context))
                        .into(binding.imageTrialJacket)
                } else {
                    if (resId == R.drawable.trial_default) {
                        binding.textTrialTitle.visibility = View.VISIBLE
                    }
                    binding.imageTrialJacket.setImageDrawable(ContextCompat.getDrawable(context, resId))
                }
            }
        }

    var rank: TrialRank? = null
        set(v) {
            field = v
            if (rank != null) {
                binding.imageBadgeHighest.setImageDrawable(ContextCompat.getDrawable(context, rank!!.drawableRes))
                binding.imageBadgeHighestCenter.setImageDrawable(ContextCompat.getDrawable(context, rank!!.drawableRes))
            }
            updateRankTint()
        }

    var exScore: Int? = null
        set(v) {
            field = v
            updateExScore()
        }

    private val shouldTint get() = rank != null && rank == tintOnRank
    var showExRemaining: Boolean = false
        set(v) {
            field = v
            updateExScore()
        }

    fun setCornerType(v: JacketCornerView.CornerType?) {
        binding.viewJacketCorner.cornerType = v
    }

    private fun updateRankTint() {
        when {
            trial?.isEvent == true -> {
                binding.imageBadgeHighest.visibility = GONE
                binding.imageBadgeHighestCenter.visibility = INVISIBLE
            }
            shouldTint -> {
                binding.viewForegroundTint.visibility = VISIBLE
                binding.viewForegroundTint.setImageDrawable(ColorDrawable(ResourcesCompat.getColor(context.resources, rank!!.colorRes, context.theme)))
                binding.imageBadgeHighest.visibility = GONE
                binding.imageBadgeHighestCenter.visibility = VISIBLE
            }
            else -> {
                binding.viewForegroundTint.visibility = GONE
                binding.imageBadgeHighest.visibilityBool = rank != null
                binding.imageBadgeHighestCenter.visibility = INVISIBLE
            }
        }
        updateExScore()
    }

    private fun updateExScore() {
        binding.textExScore.visibility = GONE
        binding.textExScoreCenter.visibility = GONE
        if (exScore != null) {
            if (shouldTint || trial?.isEvent == true) {
                binding.textExScoreCenter.visibility = VISIBLE
                binding.textExScoreCenter.bringToFront()
                binding.textExScoreCenter.background = if (trial?.isEvent == true) ResourcesCompat.getDrawable(resources, R.drawable.drawable_rounded_dark, context.theme) else null
            } else {
                binding.textExScore.visibility = VISIBLE
            }
            if (trial != null && showExRemaining) {
                binding.textExScore.text = context.getString(R.string.ex_score_missing_newline_string_format, exScore, exScore!! - trial!!.totalEx)
                binding.textExScoreCenter.text = context.getString(R.string.ex_score_missing_string_format, exScore, exScore!! - trial!!.totalEx)
            } else {
                context.getString(R.string.ex_score_string_format, exScore!!).let { exText ->
                    binding.textExScore.text = exText
                    binding.textExScoreCenter.text = exText
                }
            }
        } else {
            binding.textExScore.visibility = GONE
            binding.textExScoreCenter.visibility = GONE
        }
        ConstraintSet().also {
            it.clone(this)
            if (exScore != null) {
                it.connect(binding.imageBadgeHighestCenter.id, BOTTOM, binding.textExScoreCenter.id, TOP, 0)
            } else {
                it.connect(binding.imageBadgeHighestCenter.id, BOTTOM, this.id, BOTTOM, 0)
            }
            it.applyTo(this)
        }
    }
}
