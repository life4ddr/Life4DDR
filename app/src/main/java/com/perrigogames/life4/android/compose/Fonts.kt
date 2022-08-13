package com.perrigogames.life4.android.compose

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.perrigogames.life4.android.R

object FontSizes {
    val TINY = 10.sp
    val SMALL = 12.sp
    val MEDIUM = 14.sp
    val LARGE = 16.sp
    val HUGE = 22.sp
    val GIANT = 28.sp
}

object FontFamilies {
    val AVENIR = FontFamily(
        Font(R.font.avenir),
    )
    val AVENIR_NEXT = FontFamily(
        Font(R.font.avenir_next),
    )
}