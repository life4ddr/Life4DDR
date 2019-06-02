package com.perrigogames.life4trials.view

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.perrigogames.life4trials.R
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

    private var oldColors: ColorStateList? = null

    fun update() {
        if (oldColors == null) {
            oldColors = text_song_result.textColors
        }

        song?.let {
            text_song_title.text = it.name
            text_song_difficulty.setDifficulty(it.difficultyClass, it.difficultyNumber)

            text_song_result.text = result?.let { r ->
                resources.getString(R.string.score_string_format, r.score?.longNumberString(), r.exScore)
            }

            val tintColor = if (result?.photoUriString != null) {
                if (shouldShowAdvancedSongDetails && result?.hasAdvancedStats != true) R.color.orange
                else R.color.colorPrimary
            } else R.color.gray
            image_photo_icon.setColorFilter(ContextCompat.getColor(context, tintColor))

            if (result?.passed == false) {
                text_song_result.setTextColor(ContextCompat.getColor(context, R.color.orange))
            } else {
                text_song_result.setTextColor(oldColors)
            }

            Glide.with(this).load(it.url).into(image_song_jacket)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                image_song_jacket.setBackgroundColor(ContextCompat.getColor(context, it.difficultyClass.colorRes))
            }
        }
    }
}

fun Int.longNumberString(): String = DecimalFormat("#,###,###").format(this)