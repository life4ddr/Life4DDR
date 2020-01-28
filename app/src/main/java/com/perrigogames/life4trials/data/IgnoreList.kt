package com.perrigogames.life4trials.data

import com.google.gson.annotations.SerializedName

enum class IgnoreUnlockType {
    @SerializedName("single") SINGLE, // songs unlock one at a time in any order
    @SerializedName("sequential") SEQUENTIAL, // songs unlock one at a time in a predetermined sequence
    @SerializedName("all") ALL // songs unlock all at once
}

class IgnoreLists(val lists: List<IgnoreList>,
                  val groups: Map<String, IgnoreGroup>,
                  override val version: Int,
                  @SerializedName("major_version") override val majorVersion: Int): MajorVersioned {

    fun evaluateIgnoreLists() = lists.forEach { it.evaluateSongGroups(groups) }
}

/**
 * Data class to describe an ignore group, or a collection of songs
 * that are closely related, like a group that unlocks all at a time
 */
class IgnoreGroup(val name: String,
                  val unlock: IgnoreUnlockType? = null,
                  val songs: List<IgnoredSong>)

/**
 * Data class to describe an ignore list, or a set of songs and
 * charts that do not appear in a particular game localization
 */
class IgnoreList(val id: String,
                 val name: String,
                 @SerializedName("base_version") val baseVersion: GameVersion,
                 val groups: List<String>? = null,
                 @SerializedName("locked_groups") val lockedGroups: List<String>? = null,
                 val songs: List<IgnoredSong>? = null) {

    @Transient var resolvedSongs: MutableList<IgnoredSong>? = null
    @Transient var resolvedCharts: MutableList<IgnoredSong>? = null

    fun evaluateSongGroups(groupMap: Map<String, IgnoreGroup>) {
        resolvedSongs = mutableListOf<IgnoredSong>().also { resolved ->
            songs?.let { resolved.addAll(it) }
            lockedGroups?.map { groupMap[it] ?: error("Undefined group $it") }
                ?.flatMap { it.songs }
                ?.let { resolved.addAll(it) }
            // TODO the unlock system will want to alter these lists
            groups?.map { groupMap[it] ?: error("Undefined group $it") }
                ?.flatMap { it.songs }
                ?.let { resolved.addAll(it) }
        }

        resolvedCharts = resolvedSongs!!.filter { it.difficultyClass != null }.toMutableList()
        resolvedSongs = resolvedSongs!!.filter { it.difficultyClass == null }.toMutableList()
    }
}

class IgnoredSong(val title: String,
                  @SerializedName("difficulty_class") val difficultyClass: DifficultyClass? = null)