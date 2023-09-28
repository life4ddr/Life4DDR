package com.perrigogames.life4.android.view

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.android.drawableRes

/**
 * A subclass of [ImageView] that can display the icon of a [LadderRank]
 */
class RankImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    AppCompatImageView(context, attrs, defStyleAttr) {

    var rank: LadderRank? = null
        set(v) {
            field = v
            setImageDrawable(ContextCompat.getDrawable(context, (v ?: LadderRank.COPPER1).drawableRes))
            imageAlpha = if (v != null) 255 else 128
            colorFilter = if (v != null) null else
                ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
        }
}
