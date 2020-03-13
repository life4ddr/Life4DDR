package com.perrigogames.life4trials.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.perrigogames.life4.data.ApiPlayer
import com.perrigogames.life4trials.util.visibilityBool
import kotlinx.android.synthetic.main.view_player_found.view.*

class PlayerFoundView @JvmOverloads constructor(context: Context,
                                                attrs: AttributeSet? = null,
                                                defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    var apiPlayer: ApiPlayer? = null
        set(v) {
            field = v
            if (v != null) {
                image_rank.rank = v.rank
                setField(text_player_name, v.name)
                setField(text_player_rival_code, v.playerRivalCode)
                setField(text_player_twitter, v.twitterHandle)
            }
        }

    private fun setField(field: TextView, text: String?) {
        field.text = text
        field.visibilityBool = !text.isNullOrEmpty()
    }
}
