package com.perrigogames.life4trials.util

import android.view.View

var View.visibilityBool: Boolean
    get() = visibility == View.VISIBLE
    set(v) { visibility = if (v) View.VISIBLE else View.GONE }