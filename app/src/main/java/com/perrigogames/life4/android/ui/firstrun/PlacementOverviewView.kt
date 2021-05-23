package com.perrigogames.life4.android.ui.firstrun

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.colorRes
import com.perrigogames.life4.android.databinding.MergePlacementOverviewBinding
import com.perrigogames.life4.android.nameRes
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
                Glide.with(this).load(songs[0].url).into(binding.imageSong1)
                binding.textSong1.text = songs[0].difficultyNumber.toString()
                binding.textSong1.setTextColor(ContextCompat.getColor(context, songs[0].difficultyClass.colorRes))
                Glide.with(this).load(songs[1].url).into(binding.imageSong2)
                binding.textSong2.text = songs[1].difficultyNumber.toString()
                binding.textSong2.setTextColor(ContextCompat.getColor(context, songs[1].difficultyClass.colorRes))
                Glide.with(this).load(songs[2].url).into(binding.imageSong3)
                binding.textSong3.text = songs[2].difficultyNumber.toString()
                binding.textSong3.setTextColor(ContextCompat.getColor(context, songs[2].difficultyClass.colorRes))
            }
        }
}
