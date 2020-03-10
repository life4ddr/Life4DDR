package com.perrigogames.life4trials.ui.firstrun

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.perrigogames.life4.data.PlacementRank
import com.perrigogames.life4trials.data.Song
import com.perrigogames.life4trials.util.colorRes
import com.perrigogames.life4trials.util.nameRes
import kotlinx.android.synthetic.main.item_placement_overview.view.*

class PlacementOverviewView @JvmOverloads constructor(context: Context,
                                                      attrs: AttributeSet? = null,
                                                      defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    var rank: PlacementRank? = null
        set(v) {
            field = v
            v?.let { rank ->
                image_rank.rank = rank.toLadderRank()
                text_placement_title.text = context.getString(rank.nameRes)
                text_placement_title.setTextColor(ContextCompat.getColor(context, rank.parent.colorRes))
            }
        }

    var songs: List<Song>? = null
        set(v) {
            field = v
            v?.let { songs ->
                Glide.with(this).load(songs[0].url).into(image_song_1)
                text_song_1.text = songs[0].difficultyNumber.toString()
                text_song_1.setTextColor(ContextCompat.getColor(context, songs[0].difficultyClass.colorRes))
                Glide.with(this).load(songs[1].url).into(image_song_2)
                text_song_2.text = songs[1].difficultyNumber.toString()
                text_song_2.setTextColor(ContextCompat.getColor(context, songs[1].difficultyClass.colorRes))
                Glide.with(this).load(songs[2].url).into(image_song_3)
                text_song_3.text = songs[2].difficultyNumber.toString()
                text_song_3.setTextColor(ContextCompat.getColor(context, songs[2].difficultyClass.colorRes))
            }
        }
}
