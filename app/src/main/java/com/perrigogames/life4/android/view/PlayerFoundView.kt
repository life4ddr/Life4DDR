package com.perrigogames.life4.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.databinding.MergePlayerFoundBinding
import com.perrigogames.life4.data.ApiPlayer
import com.perrigogames.life4.android.util.visibilityBool

class PlayerFoundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: MergePlayerFoundBinding

    init {
        LayoutInflater.from(context).inflate(R.layout.merge_player_found, this)
        binding = MergePlayerFoundBinding.bind(this)
    }

    var apiPlayer: ApiPlayer? = null
        set(v) {
            field = v
            if (v != null) {
                binding.imageRank.rank = v.rank
                setField(binding.textPlayerName, v.name)
                setField(binding.textPlayerRivalCode, v.playerRivalCode)
                setField(binding.textPlayerTwitter, v.twitterHandle)
            }
        }

    private fun setField(field: TextView, text: String?) {
        field.text = text
        field.visibilityBool = !text.isNullOrEmpty()
    }
}
