package com.perrigogames.life4trials.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.perrigogames.life4trials.util.setScaledBitmapFromFile

class PathImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ImageView(context, attrs, defStyleAttr) {

    var outputSize: Int = 128

    var path: String? = null
        set(v) {
            field = v
            if (path != null) {
                visibility = View.VISIBLE
                setScaledBitmapFromFile(path!!)
            } else {
                setImageDrawable(null)
            }
        }
}