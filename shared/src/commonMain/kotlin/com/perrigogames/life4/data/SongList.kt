package com.perrigogames.life4.data

data class SongList(
    override val version: Int,
    val songLines: List<String>,
): Versioned {

    override fun toString(): String =
        "$version\n${songLines.joinToString(separator = "\n")}"

    companion object {
        fun parse(s: String): SongList {
            val lines = s.lines().toMutableList()
            return SongList(
                version = lines.removeAt(0).trim().toInt(),
                songLines = lines,
            )
        }
    }
}