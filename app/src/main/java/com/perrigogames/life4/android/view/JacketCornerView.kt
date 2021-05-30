package com.perrigogames.life4.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.databinding.ViewJacketCornerBinding
import com.perrigogames.life4.android.util.visibilityBool
import com.perrigogames.life4.android.view.JacketCornerView.CornerType.EVENT
import com.perrigogames.life4.android.view.JacketCornerView.CornerType.NEW

class JacketCornerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewJacketCornerBinding

    init {
        LayoutInflater.from(context).inflate(R.layout.view_jacket_corner, this)
        binding = ViewJacketCornerBinding.bind(this)
    }

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
        binding.imageCorner.visibilityBool = false
        binding.textCorner.visibilityBool = false
    }

    private fun updateCorner(@DrawableRes drawableId: Int, @StringRes textId: Int, @ColorRes textColorId: Int) {
        binding.imageCorner.visibilityBool = true
        binding.textCorner.visibilityBool = true

        binding.imageCorner.setImageDrawable(ResourcesCompat.getDrawable(resources, drawableId, context.theme))
        binding.textCorner.text = resources.getString(textId)
        binding.textCorner.setTextColor(ResourcesCompat.getColor(resources, textColorId, context.theme))
    }

    enum class CornerType { NEW, EVENT }
}