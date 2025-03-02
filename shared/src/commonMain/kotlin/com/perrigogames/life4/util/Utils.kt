package com.perrigogames.life4.util

fun <T> List<T>.split(matchBlock: (T) -> Boolean): Pair<List<T>, List<T>> {
    val match = mutableListOf<T>()
    val noMatch = mutableListOf<T>()
    forEach { if (matchBlock(it)) match.add(it) else noMatch.add(it) }
    return match to noMatch
}

fun Double.toStringWithoutDecimal(): String {
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        this.toString()
    }
}