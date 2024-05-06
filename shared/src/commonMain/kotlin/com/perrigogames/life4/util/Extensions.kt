package com.perrigogames.life4.util

fun String.indexOfOrEnd(
    char: Char,
    startIndex: Int = 0,
    ignoreCase: Boolean = false,
): Int {
    val idx = indexOf(char, startIndex, ignoreCase)
    return if (idx >= 0) idx else length
}

fun String.indexOfOrEnd(
    str: String,
    startIndex: Int = 0,
    ignoreCase: Boolean = false,
): Int {
    val idx = indexOf(str, startIndex, ignoreCase)
    return if (idx >= 0) idx else length
}

/**
 * For a list of integers, checks to see if another set of integers fits in the cascade of values.
 * Both lists are sorted in descending order, and the values from `other` are lined up with the
 * corresponding "floor" values from this list.  This function returns false if there are elements
 * that remain after cascading.
 *
 * Ex.
 * [9, 7, 6, 5].hasCascade([10, 8]) == true, matches 9, 7
 * [9, 7, 6, 5].hasCascade([6, 6]) == true, matches 6, 5
 * [9, 7, 6, 5].hasCascade([6, 4]) == false, matches 6 / 4 extra
 * [9, 7, 6, 5].hasCascade([6, 6, 5]) == false, matches 6, 5 / 5 extra
 */
fun List<Int>.hasCascade(other: List<Int>): Boolean {
    val sortedThis = this.sortedByDescending { it }
    val sortedOther = other.sortedByDescending { it }
    var targetIdx = 0
    sortedOther.forEach { curr ->
        if (targetIdx >= this.size) {
            return false
        }
        var hasMatch = false
        (targetIdx until this.size).forEach { matchIdx ->
            if (!hasMatch) {
                targetIdx = matchIdx + 1
                if (curr >= sortedThis[matchIdx]) {
                    hasMatch = true
                }
            }
        }
        if (!hasMatch) {
            return false
        }
    }
    return true
}
