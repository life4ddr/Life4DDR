package com.perrigogames.life4.android.util

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.perrigogames.life4.android.R

fun circularProgressDrawable(context: Context) = CircularProgressDrawable(context).apply {
    strokeWidth = 5f
    centerRadius = 30f
    setColorSchemeColors(ContextCompat.getColor(context, R.color.white))
    start()
}