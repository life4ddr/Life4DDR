package com.perrigogames.life4trials.view

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.Song
import kotlinx.android.synthetic.main.item_song_list_item.view.*

class SongView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    var song: Song? = null
        set(v) {
            field = v
            update()
        }

    var photoPath: String? = null
        set(v) {
            field = v
            if (v != null) {
                val bitmap = BitmapFactory.decodeFile(v, BitmapFactory.Options().apply {
                    outWidth = 128
                    outHeight = 128
                })
                image_photo.visibility = View.VISIBLE
                image_photo.setImageDrawable(BitmapDrawable(resources, bitmap))
            } else {
                image_photo.visibility = View.GONE
            }
        }

    var score: Int = -1
        set(v) {
            field = v
            update()
        }

    var ex: Int = -1
        set(v) {
            field = v
            update()
        }

    fun update() {
        song?.let {
            text_song_title.text = it.name
            text_song_difficulty.setDifficulty(it.difficultyClass, it.difficultyNumber)
            text_song_result.text = if (score > 0 && ex > 0) {
                resources.getString(R.string.difficulty_string_format, score.toString(), ex)
            } else ""

            Glide.with(this).load(it.url).into(image_song_jacket)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                image_song_jacket.setBackgroundColor(ContextCompat.getColor(context, it.difficultyClass.colorRes))
            }
        }
    }
}