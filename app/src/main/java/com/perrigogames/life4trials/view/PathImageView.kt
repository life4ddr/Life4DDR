package com.perrigogames.life4trials.view

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

class PathImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ImageView(context, attrs, defStyleAttr) {

    var outputSize: Int = 128

    var path: String? = null
        set(v) {
            field = v
            if (path != null) {
                val bitmap = BitmapFactory.decodeFile(path, BitmapFactory.Options().apply {
                    outWidth = outputSize
                    outHeight = outputSize
                })
                visibility = View.VISIBLE
                setImageDrawable(BitmapDrawable(resources, bitmap))
            } else {
                setImageDrawable(null)
            }
        }
}