package com.perrigogames.life4trials.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.ClearType.MARVELOUS_FULL_COMBO
import com.perrigogames.life4trials.data.ClearType.PERFECT_FULL_COMBO
import com.perrigogames.life4trials.data.Song
import com.perrigogames.life4trials.data.SongResult
import kotlinx.android.synthetic.main.item_song_list_item.view.*
import java.text.DecimalFormat

/**
 * A [View] designed to show the qualities of a [Song], including the jacket, difficulty,
 * and a player's score if wanted.
 */
class SongView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

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
            oldColors = text_song_result.textColors
        }

        song?.let { s ->
            text_song_title.text = s.name
            text_song_difficulty.setDifficulty(s.difficultyClass, s.difficultyNumber)

            text_song_result.text = result?.let { r ->
                resources.getString(R.string.score_string_format, r.score?.longNumberString(), r.exScore)
            }
            text_song_details.text = result?.let { r ->
                if (r.badJudges != null && r.perfects != null && r.perfects != -1) {
                    resources.getString(R.string.score_string_summary_format_expert, r.badJudges, r.perfects, s.ex)
                } else if (r.misses != null && r.badJudges != null) {
                    resources.getString(R.string.score_string_summary_format_advanced, r.misses, r.badJudges, s.ex)
                } else null
            } ?: resources.getString(R.string.ex_score_string_format, s.ex)

            if (shouldShowCamera) {
                image_photo_icon.visibility = View.VISIBLE
                val tintColor = if (result?.photoUriString != null) {
                    if (shouldShowAdvancedSongDetails && result?.hasAdvancedStats != true) R.color.orange
                    else R.color.colorPrimary
                } else R.color.gray
                image_photo_icon.setColorFilter(ContextCompat.getColor(context, tintColor))
            } else {
                image_photo_icon.visibility = View.GONE
            }

            text_song_result.apply {
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
                    else -> text_song_result.setTextColor(oldColors)
                }
            }

                Glide.with(this).load(s.url).into(image_song_jacket)
                image_song_jacket.setBackgroundColor(ContextCompat.getColor(context, s.difficultyClass.colorRes))
        }
    }
}

fun Int.longNumberString(): String = DecimalFormat("#,###,###").format(this)