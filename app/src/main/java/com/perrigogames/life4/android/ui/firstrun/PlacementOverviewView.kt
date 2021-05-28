package com.perrigogames.life4.android.ui.firstrun

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.colorRes
import com.perrigogames.life4.android.databinding.MergePlacementOverviewBinding
import com.perrigogames.life4.android.nameRes
import com.perrigogames.life4.android.util.circularProgressDrawable
import com.perrigogames.life4.data.PlacementRank
import com.perrigogames.life4.data.Song

class PlacementOverviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: MergePlacementOverviewBinding

    init {
        LayoutInflater.from(context).inflate(R.layout.merge_placement_overview, this)
        binding = MergePlacementOverviewBinding.bind(this)
    }

    var rank: PlacementRank? = null
        set(v) {
            field = v
            v?.let { rank ->
                binding.imageRank.rank = rank.toLadderRank()
                binding.textPlacementTitle.text = context.getString(rank.nameRes)
                binding.textPlacementTitle.setTextColor(ContextCompat.getColor(context, rank.parent.colorRes))
            }
        }

    var songs: List<Song>? = null
        set(v) {
            field = v
            v?.let { songs ->
                loadSong(songs[0], binding.imageSong1, binding.textSong1)
                loadSong(songs[1], binding.imageSong2, binding.textSong2)
                loadSong(songs[2], binding.imageSong3, binding.textSong3)
            }
        }

    private fun loadSong(song: Song, image: ImageView, text: TextView) {
        Glide.with(this)
            .load(song.url)
            .placeholder(circularProgressDrawable(context))
            .into(image)
        text.text = song.difficultyNumber.toString()
        text.setTextColor(ContextCompat.getColor(context, song.difficultyClass.colorRes))
    }
}
