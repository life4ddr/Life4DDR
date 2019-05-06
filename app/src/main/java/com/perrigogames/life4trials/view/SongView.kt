package com.perrigogames.life4trials.view

import android.content.Context
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

    fun update() {
        song?.let {
            text_song_title.text = it.name
            text_song_difficulty.setDifficulty(it.difficultyClass, it.difficultyNumber)

            text_song_result.text = result?.let { r ->
                val scoreString = DecimalFormat("#,###,###").format(r.score)
                resources.getString(R.string.difficulty_string_format, scoreString, r.exScore)
            }

            val tintColor = if (result?.photoPath != null) R.color.colorPrimary else R.color.gray
            image_photo_icon.setColorFilter(ContextCompat.getColor(context, tintColor))

            Glide.with(this).load(it.url).into(image_song_jacket)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                image_song_jacket.setBackgroundColor(ContextCompat.getColor(context, it.difficultyClass.colorRes))
            }
        }
    }
}