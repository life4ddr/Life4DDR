package com.perrigogames.life4.util

fun <T> List<T>.split(matchBlock: (T) -> Boolean): Pair<List<T>, List<T>> {
    val match = mutableListOf<T>()
    val noMatch = mutableListOf<T>()
    forEach { if (matchBlock(it)) match.add(it) else noMatch.add(it) }
    return match to noMatch
}

fun Double.toStringWithoutDecimal(): String {
    val intString = this.toInt().toString()
    return if (this % 1.0 == 0.0) {
        intString
    } else {
        this.toString().padEnd(intString.length + 4, '0')
    }
}