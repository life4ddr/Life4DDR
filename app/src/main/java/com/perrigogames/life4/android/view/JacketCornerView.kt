package com.perrigogames.life4.android.view

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.util.visibilityBool
import com.perrigogames.life4.android.view.JacketCornerView.CornerType.EVENT
import com.perrigogames.life4.android.view.JacketCornerView.CornerType.NEW
import kotlinx.android.synthetic.main.view_jacket_corner.view.*

class JacketCornerView @JvmOverloads constructor(context: Context,
                                                 attrs: AttributeSet? = null,
                                                 defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    var cornerType: CornerType? = null
        set(v) {
            field = v
            cornerType.let { when(it) {
                EVENT -> updateCorner(R.drawable.triangle_event, R.string.event_tag, R.color.black_50)
                NEW -> updateCorner(R.drawable.triangle_new, R.string.new_tag, R.color.white)
                else -> hideCorner()
            }}
        }

    private fun hideCorner() {
        image_corner.visibilityBool = false
        text_corner.visibilityBool = false
    }

    private fun updateCorner(@DrawableRes drawableId: Int, @StringRes textId: Int, @ColorRes textColorId: Int) {
        image_corner.visibilityBool = true
        text_corner.visibilityBool = true

        image_corner.setImageDrawable(ResourcesCompat.getDrawable(resources, drawableId, context.theme))
        text_corner.text = resources.getString(textId)
        text_corner.setTextColor(ResourcesCompat.getColor(resources, textColorId, context.theme))
    }

    enum class CornerType { NEW, EVENT }
}