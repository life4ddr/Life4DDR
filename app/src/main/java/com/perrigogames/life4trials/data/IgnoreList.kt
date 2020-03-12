package com.perrigogames.life4trials.data

import com.google.gson.annotations.SerializedName
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.GameVersion
import com.perrigogames.life4.data.MajorVersioned

enum class IgnoreUnlockType {
    @SerializedName("single") SINGLE, // songs unlock one at a time in any order
    @SerializedName("sequence") SEQUENTIAL, // songs unlock one at a time in a predetermined sequence
    @SerializedName("all") ALL; // songs unlock all at once

    fun fromStoredState(stored: Long, listLength: Int): List<Boolean> = when(this) {
        SINGLE -> (0 until listLength).map { idx -> stored and 1L.shl(idx) != 0L}
        SEQUENTIAL -> (0 until listLength).map { idx -> idx < stored }
        ALL -> (0 until listLength).map { stored == 1L }
    }

    fun toStoredState(flags: List<Boolean>): Long = when(this) {
        SINGLE -> flags.mapIndexed { idx, f -> if (f) 1L.shl(idx) else 0 }.sum()
        SEQUENTIAL -> flags.indexOfLast { it } + 1.toLong()
        ALL -> if (flags[0]) 1L else 0L
    }
}

class IgnoreListData(val lists: List<IgnoreList>,
                     val groups: List<IgnoreGroup>,
                     override val version: Int,
                     @SerializedName("major_version") override val majorVersion: Int):
    MajorVersioned {

    private var mGroupMap: Map<String, IgnoreGroup>? = null
    val groupsMap: Map<String, IgnoreGroup>
        get() {
            mGroupMap = mGroupMap ?: groups.associateBy { it.id }
            return mGroupMap!!
        }

    fun evaluateIgnoreLists() = lists.forEach { it.evaluateSongGroups(groups) }
}

/**
 * Data class to describe an ignore group, or a collection of songs
 * that are closely related, like a group that unlocks all at a time
 */
class IgnoreGroup(val id: String,
                  val name: String,
                  val unlock: IgnoreUnlockType? = null,
                  val songs: List<IgnoredSong>) {

    fun fromStoredState(stored: Long) = unlock?.fromStoredState(stored, songs.size)
}

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

    fun evaluateSongGroups(groupMap: List<IgnoreGroup>) {
        resolvedSongs = mutableListOf<IgnoredSong>().also { resolved ->
            songs?.let { resolved.addAll(it) }
            lockedGroups?.map { id -> groupMap.firstOrNull { it.id == id } ?: error("Undefined group $id") }
                ?.flatMap { it.songs }
                ?.let { resolved.addAll(it) }
            // TODO the unlock system will want to alter these lists
            groups?.map { id -> groupMap.firstOrNull { it.id == id } ?: error("Undefined group $id") }
                ?.flatMap { it.songs }
                ?.let { resolved.addAll(it) }
        }

        resolvedCharts = resolvedSongs!!.filter { it.difficultyClass != null }.toMutableList()
        resolvedSongs = resolvedSongs!!.filter { it.difficultyClass == null }.toMutableList()
    }
}

class IgnoredSong(val title: String,
                  @SerializedName("difficulty_class") val difficultyClass: DifficultyClass? = null)
