package com.perrigogames.life4trials.data

import com.google.gson.annotations.SerializedName

class IgnoreLists(val lists: List<IgnoreList>,
                  val version: Int)

/**
 * Data class to describe an ignore list, or a set of songs and
 * charts that do not appear in a particular game localization
 */
class IgnoreList(val id: String,
                 val name: String,
                 @SerializedName("base_version") val baseVersion: GameVersion,
                 val songs: List<IgnoredSong>? = null,
                 val charts: List<IgnoredChart>? = null)

class IgnoredSong(val title: String)

class IgnoredChart(val title: String,
                   @SerializedName("difficulty_class") val difficultyClass: DifficultyClass)