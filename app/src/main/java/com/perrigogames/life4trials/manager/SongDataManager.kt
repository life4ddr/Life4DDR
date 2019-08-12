package com.perrigogames.life4trials.manager

import android.content.Context
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.DifficultyClass
import com.perrigogames.life4trials.data.IgnoreList
import com.perrigogames.life4trials.data.IgnoreLists
import com.perrigogames.life4trials.data.PlayStyle
import com.perrigogames.life4trials.db.ChartDB
import com.perrigogames.life4trials.db.ChartDB_
import com.perrigogames.life4trials.db.SongDB
import com.perrigogames.life4trials.db.SongDB_
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.loadRawString

/**
 * A Manager class that keeps track of the available songs
 */
class SongDataManager(context: Context): BaseManager() {

    //
    // Ignore List Data
    //
    private var ignoreList: List<IgnoreList> =
        DataUtil.gson.fromJson(context.loadRawString(R.raw.ignore_lists), IgnoreLists::class.java)!!.lists
    private var selectedIgnoreList: IgnoreList? = ignoreList.first { it.id == "ACE_US" } //FIXME
    var selectedIgnoreSongIds: LongArray? = null
        get() {
            if (field == null) {
                field = selectedIgnoreList?.songs?.map { it.title }?.toTypedArray()?.let { ignoreTitles ->
                    multipleSongTitleQuery.setParameters("titles", ignoreTitles).find().map { it.id }.toLongArray()
                } ?: LongArray(0)
            }
            return field
        }
        private set
    var selectedIgnoreChartIds: LongArray? = null
        get() {
            if (field == null) {
                field = selectedIgnoreList?.charts?.mapNotNull { chart ->
                    val song = songTitleQuery.setParameter("title", chart.title).findFirst()
                    return@mapNotNull song?.charts?.firstOrNull { it.difficultyClass == chart.difficultyClass }?.id
                }?.toLongArray() ?: LongArray(0)
            }
            return field
        }
        private set

    //
    // ObjectBoxes
    //
    private val songBox get() = objectBox.boxFor(SongDB::class.java)
    private val chartBox get() = objectBox.boxFor(ChartDB::class.java)

    //
    // Queries
    //
    private val songTitleQuery = songBox.query()
        .equal(SongDB_.title, "").parameterAlias("title")
        .build()
    private val multipleSongTitleQuery = songBox.query()
        .`in`(SongDB_.title, emptyArray<String>()).parameterAlias("titles")
        .build()
    private val chartDifficultyQuery = chartBox.query().apply {
        equal(ChartDB_.difficultyNumber, 0).parameterAlias("difficulty")
        link(ChartDB_.song).notIn(SongDB_.id, selectedIgnoreSongIds)
        notIn(ChartDB_.id, selectedIgnoreChartIds)
    }.build()

    init {
        selectedIgnoreList = selectedIgnoreList
    }

    fun getSongByName(name: String): SongDB? =
        songTitleQuery.setParameter("title", name).findFirst()

    fun getChartsByDifficulty(difficulty: Int): MutableList<ChartDB> =
        chartDifficultyQuery.setParameter("difficulty", difficulty.toLong()).find()

    fun getChartsByDifficulty(difficultyList: IntArray): MutableList<ChartDB> = mutableListOf<ChartDB>().apply {
        difficultyList.forEach { addAll(chartDifficultyQuery.setParameter("difficulty", it.toLong()).find()) }
    }

    fun getOrCreateSong(name: String, artist: String? = null): SongDB =
        getSongByName(name) ?: SongDB(name, artist).also {
            songBox.put(it)
        }

    /**
     * Nulls out the list of invalid IDs, to regenerate them
     */
    fun invalidateIgnoredIds() {
        selectedIgnoreSongIds = null
        selectedIgnoreChartIds = null
    }

    fun updateOrCreateChartForSong(song: SongDB,
                                   playStyle: PlayStyle,
                                   difficultyClass: DifficultyClass,
                                   difficultyNumber: Int): ChartDB {
        val chart = song.charts.firstOrNull { it.playStyle == playStyle && it.difficultyClass == difficultyClass }
        chart?.let {
            if (it.difficultyNumber != difficultyNumber) {
                Crashlytics.logException(UnexpectedDifficultyNumberException(it, difficultyNumber))
                it.difficultyNumber = difficultyNumber
                chartBox.put(it)
            }
        }
        return chart ?: ChartDB(difficultyClass, difficultyNumber, playStyle).also {
            song.charts.add(it)
            chartBox.put(it)
            songBox.put(song)
        }
    }

    fun updateChart(chart: ChartDB) = chartBox.put(chart)

    fun dumpData() {
        val songStrings = songBox.all.map { song ->
            val builder = StringBuilder("${song.title};")
            val chartsCopy = song.charts.toMutableList()
            DifficultyClass.values().forEach { diff ->
                val chart = chartsCopy.firstOrNull { it.difficultyClass == diff }
                if (chart != null) {
                    chartsCopy.remove(chart)
                    builder.append("${chart.difficultyNumber};")
                } else {
                    builder.append(";")
                }
            }
            builder.toString()
        }.toMutableList()
        with(StringBuilder()) {
            while (songStrings.isNotEmpty()) {
                (0..10).forEach {
                    if (songStrings.isNotEmpty()) {
                        append("${songStrings.removeAt(0)}[][]")
                    }
                }
                Log.v("SongDataManager", this.toString())
                setLength(0)
            }
        }
    }
}

class UnexpectedDifficultyNumberException(chart: ChartDB, newDiff: Int): Exception(
    "Chart ${chart.song.target.title} ${chart.playStyle} ${chart.difficultyClass} changed: ${chart.difficultyNumber} -> $newDiff")
