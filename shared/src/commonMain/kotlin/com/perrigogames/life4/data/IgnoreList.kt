@file:UseSerializers(
    DifficultyClassSerializer::class,
    PlayStyleSerializer::class,
    ChartTypeSerializer::class,
    ClearTypeSerializer::class)

package com.perrigogames.life4.data

import com.perrigogames.life4.db.ChartInfo
import com.perrigogames.life4.enums.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers

@Serializable
enum class IgnoreUnlockType {
    @SerialName("single") SINGLE, // songs unlock one at a time in any order
    @SerialName("sequence") SEQUENTIAL, // songs unlock one at a time in a predetermined sequence
    @SerialName("all") ALL; // songs unlock all at once

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

@Serializable
class IgnoreListData(val lists: List<IgnoreList>,
                     val groups: List<IgnoreGroup>,
                     override val version: Int,
                     @SerialName("major_version") override val majorVersion: Int):
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
@Serializable
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
@Serializable
class IgnoreList(val id: String,
                 val name: String,
                 @SerialName("base_version") val baseVersion: GameVersion,
                 val groups: List<String>? = null,
                 @SerialName("locked_groups") val lockedGroups: List<String>? = null,
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

@Serializable
class IgnoredSong(val id: Long,
                  val title: String,
                  @SerialName("difficulty_class") val difficultyClass: DifficultyClass? = null,
                  @SerialName("play_style") val playStyle: PlayStyle? = null) {

    fun matches(chart: ChartInfo) =
        chart.songId == id &&
                (difficultyClass == null || difficultyClass == chart.difficultyClass) &&
                (playStyle == null || playStyle == chart.playStyle)

    override fun toString(): String = "$id - $title ($difficultyClass, $playStyle)"
}
