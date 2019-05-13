package com.perrigogames.life4trials.view

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

class PathImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ImageView(context, attrs, defStyleAttr) {

    var outputSize: Int = 128

    var uri: Uri? = null
        set(v) {
            field = v
            if (v != null) {
                visibility = View.VISIBLE
                setImageBitmap(MediaStore.Images.Media.getBitmap(context.contentResolver, v))
            } else {
                setImageDrawable(null)
            }
        }
}