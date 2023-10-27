package com.perrigogames.life4.util

inline fun <T : Any> T?.orElse(block: () -> T) = this ?: block()

inline fun <T : Any> T?.ifNull(block: () -> Unit) = apply { if (this == null) block() }