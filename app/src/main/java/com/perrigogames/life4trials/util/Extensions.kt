package com.perrigogames.life4trials.util

import android.view.View

var View.visibilityBool: Boolean
    get() = visibility == View.VISIBLE
    set(v) { visibility = if (v) View.VISIBLE else View.GONE }

fun String.indexOfOrEnd(char: Char, startIndex: Int = 0, ignoreCase: Boolean = false): Int {
    val idx = indexOf(char, startIndex, ignoreCase)
    return if (idx >= 0) idx else length
}

fun String.indexOfOrEnd(str: String, startIndex: Int = 0, ignoreCase: Boolean = false): Int {
    val idx = indexOf(str, startIndex, ignoreCase)
    return if (idx >= 0) idx else length
}