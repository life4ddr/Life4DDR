package com.perrigogames.life4.util

import kotlin.math.max
import kotlin.math.min

class CompoundIntRange(
    val outerRange: IntRange,
    innerRange: IntRange? = null,
) {
    val innerRange: IntRange

    init {
        val realInner = innerRange ?: outerRange
        val innerBottom = max(outerRange.first, realInner.first)
        val innerTop = min(outerRange.last, realInner.last)
        this.innerRange = IntRange(innerBottom, innerTop)
    }

    val outerFloatRange get() = outerRange.first.toFloat() .. outerRange.last.toFloat()
    val innerFloatRange get() = innerRange.first.toFloat() .. innerRange.last.toFloat()
}