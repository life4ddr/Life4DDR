package com.perrigogames.life4.android.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.perrigogames.life4.data.Song
import com.perrigogames.life4.data.SongResult
import com.perrigogames.life4.enums.ClearType.MARVELOUS_FULL_COMBO
import com.perrigogames.life4.enums.ClearType.PERFECT_FULL_COMBO
import com.perrigogames.life4.longNumberString
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.colorRes
import com.perrigogames.life4.android.databinding.ItemSongListItemBinding

/**
 * A [View] designed to show the qualities of a [Song], including the jacket, difficulty,
 * and a player's score if wanted.
 */
class SongView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = ItemSongListItemBinding.bind(this)

    var song: Song? = null
        set(v) {
            field = v
            update()
        }

    var result: SongResult? = null
        set(v) {
            field = v
            update()
        }

    var shouldShowAdvancedSongDetails: Boolean = false
        set(v) {
            field = v
            update()
        }

    var shouldShowCamera: Boolean = true
        set(v) {
            field = v
            update()
        }

    private var oldColors: ColorStateList? = null

    fun update() {
        if (oldColors == null) {
            oldColors = binding.textSongResult.textColors
        }

        song?.let { s ->
            binding.textSongTitle.text = s.name
            binding.textSongDifficulty.setDifficulty(s.difficultyClass, s.difficultyNumber)

            binding.textSongResult.text = result?.let { r ->
                resources.getString(R.string.score_string_format, r.score?.longNumberString(), r.exScore)
            }
            binding.textSongDetails.text = result?.let { r ->
                if (r.badJudges != null && r.perfects != null && r.perfects != -1) {
                    resources.getString(R.string.score_string_summary_format_expert, r.badJudges, r.perfects, s.ex)
                } else if (r.misses != null && r.badJudges != null) {
                    resources.getString(R.string.score_string_summary_format_advanced, r.misses, r.goods, r.greats, s.ex)
                } else null
            } ?: resources.getString(R.string.ex_score_string_format, s.ex)

            if (shouldShowCamera) {
                binding.imagePhotoIcon.visibility = View.VISIBLE
                val tintColor = if (result?.photoUriString != null) {
                    if (shouldShowAdvancedSongDetails && result?.hasAdvancedStats != true) R.color.orange
                    else R.color.colorPrimary
                } else R.color.gray
                binding.imagePhotoIcon.setColorFilter(ContextCompat.getColor(context, tintColor))
            } else {
                binding.imagePhotoIcon.visibility = View.GONE
            }

            binding.textSongResult.apply {
                setTypeface(null, Typeface.NORMAL)
                when {
                    result?.passed == false -> setTextColor(ContextCompat.getColor(context, R.color.orange))
                    result?.clearType == MARVELOUS_FULL_COMBO -> {
                        setTextColor(ContextCompat.getColor(context, R.color.marvelous))
                        setTypeface(null, Typeface.BOLD)
                    }
                    result?.clearType == PERFECT_FULL_COMBO -> {
                        setTextColor(ContextCompat.getColor(context, R.color.perfect))
                        setTypeface(null, Typeface.BOLD)
                    }
                    else -> binding.textSongResult.setTextColor(oldColors)
                }
            }

                Glide.with(this).load(s.url).into(binding.imageSongJacket)
                binding.imageSongJacket.setBackgroundColor(ContextCompat.getColor(context, s.difficultyClass.colorRes))
        }
    }
}
