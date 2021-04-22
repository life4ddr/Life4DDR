package com.perrigogames.life4.android.util

import android.content.res.Resources
import com.perrigogames.life4.android.R

object CommonSizes {
    fun contentPaddingSmall(res: Resources) = res.getDimensionPixelOffset(R.dimen.content_padding_small)
    fun contentPaddingMed(res: Resources) = res.getDimensionPixelOffset(R.dimen.content_padding_med)
    fun contentPaddingLarge(res: Resources) = res.getDimensionPixelOffset(R.dimen.content_padding_large)
    fun contentPaddingHuge(res: Resources) = res.getDimensionPixelOffset(R.dimen.content_padding_huge)
}