package com.perrigogames.life4.util

import dev.icerock.moko.mvvm.flow.CMutableStateFlow

fun <T> CMutableStateFlow<T>.mutate(block: T.() -> T) {
    value = block(value)
}
