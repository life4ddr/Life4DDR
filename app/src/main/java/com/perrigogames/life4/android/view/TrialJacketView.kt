package com.perrigogames.life4.android.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.perrigogames.life4.android.util.shouldFetchJacket
import com.perrigogames.life4.android.util.visibilityBool
import com.perrigogames.life4.viewmodel.TrialJacketViewModel

class TrialJacketView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: MergeTrialJacketBinding

    init {
        LayoutInflater.from(context).inflate(R.layout.merge_trial_jacket, this)
        binding = MergeTrialJacketBinding.bind(this)
    }

    fun bind(item: TrialJacketViewModel) {
        val trial = item.trial
        binding.textTrialTitle.text = trial.name
        binding.imageTrialDifficulty.visibilityBool = trial.difficulty != null
        binding.textTrialDifficulty.visibilityBool = trial.difficulty != null
        binding.textTrialDifficulty.text = trial.difficulty?.toString()
        binding.textTrialTitle.visibility = View.GONE
        binding.root.alpha = item.viewAlpha
        binding.viewJacketCorner.cornerType = item.cornerType

        val resId = trial.jacketResId(context)
        if (trial.shouldFetchJacket(context)) {
            Glide.with(this)
                .load(trial.coverUrl)
                .placeholder(circularProgressDrawable(context))
                .into(binding.imageTrialJacket)
        } else {
            if (resId == R.drawable.trial_default) {
                binding.textTrialTitle.visibility = View.VISIBLE
            }
            binding.imageTrialJacket.setImageDrawable(ContextCompat.getDrawable(context, resId))
        }

        val rank = item.rank
        if (rank != null) {
            binding.imageBadgeHighest.setImageDrawable(ContextCompat.getDrawable(context, rank.drawableRes))
            binding.imageBadgeHighestCenter.setImageDrawable(ContextCompat.getDrawable(context, rank.drawableRes))
        }
        updateRankTint(item)

        updateExScore(item)
    }

    private fun updateRankTint(item: TrialJacketViewModel) {
        when {
            item.trial.isEvent -> {
                binding.imageBadgeHighest.visibility = GONE
                binding.imageBadgeHighestCenter.visibility = INVISIBLE
            }
            item.shouldTint -> {
                binding.viewForegroundTint.visibility = VISIBLE
                binding.viewForegroundTint.setImageDrawable(ColorDrawable(ResourcesCompat.getColor(context.resources, item.rank!!.colorRes, context.theme)))
                binding.imageBadgeHighest.visibility = GONE
                binding.imageBadgeHighestCenter.visibility = VISIBLE
            }
            else -> {
                binding.viewForegroundTint.visibility = GONE
                binding.imageBadgeHighest.visibilityBool = item.rank != null
                binding.imageBadgeHighestCenter.visibility = INVISIBLE
            }
        }
    }

    private fun updateExScore(item: TrialJacketViewModel) {
        binding.textExScore.visibility = GONE
        binding.textExScoreCenter.visibility = GONE
        if (item.exScore != null) {
            if (item.shouldTint || item.trial.isEvent) {
                binding.textExScoreCenter.visibility = VISIBLE
                binding.textExScoreCenter.bringToFront()
                binding.textExScoreCenter.background = if (item.trial.isEvent) ResourcesCompat.getDrawable(resources, R.drawable.drawable_rounded_dark, context.theme) else null
            } else {
                binding.textExScore.visibility = VISIBLE
            }
            if (item.showExRemaining) {
                binding.textExScore.text = context.getString(R.string.ex_score_missing_newline_string_format, item.exScore, item.exScore!! - item.trial.totalEx)
                binding.textExScoreCenter.text = context.getString(R.string.ex_score_missing_string_format, item.exScore, item.exScore!! - item.trial.totalEx)
            } else {
                context.getString(R.string.ex_score_string_format, item.exScore!!).let { exText ->
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
            if (item.exScore != null) {
                it.connect(binding.imageBadgeHighestCenter.id, BOTTOM, binding.textExScoreCenter.id, TOP, 0)
            } else {
                it.connect(binding.imageBadgeHighestCenter.id, BOTTOM, this.id, BOTTOM, 0)
            }
            it.applyTo(this)
        }
    }

    companion object {

        fun inflate(context: Context, parent: ViewGroup, attachToRoot: Boolean) =
            LayoutInflater.from(context).inflate(R.layout.item_trial_tile_item, parent, attachToRoot) as TrialJacketView
    }
}
