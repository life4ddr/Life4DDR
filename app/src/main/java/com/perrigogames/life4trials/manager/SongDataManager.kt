package com.perrigogames.life4trials.manager

import android.content.Context
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_IMPORT_IGNORE
import com.perrigogames.life4trials.api.GithubDataAPI
import com.perrigogames.life4trials.api.RemoteData
import com.perrigogames.life4trials.data.*
import com.perrigogames.life4trials.db.ChartDB
import com.perrigogames.life4trials.db.ChartDB_
import com.perrigogames.life4trials.db.SongDB
import com.perrigogames.life4trials.db.SongDB_
import com.perrigogames.life4trials.event.MajorUpdateProcessEvent
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.MajorUpdate
import com.perrigogames.life4trials.util.SharedPrefsUtil
import com.perrigogames.life4trials.util.SharedPrefsUtil.KEY_SONG_LIST_VERSION
import com.perrigogames.life4trials.util.loadRawString
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Response

/**
 * A Manager class that keeps track of the available songs
 */
class SongDataManager(private val context: Context,
                      private val githubDataAPI: GithubDataAPI): BaseManager() {

    private val songList = object: RemoteData<String>(context) {
        override suspend fun getRemoteResponse(): Response<String> = githubDataAPI.getSongList()
        override fun onFetchUpdated(data: String) = initializeSongDatabase(data)
    }

    init {
        Life4Application.eventBus.register(this)
        songList.fetch()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMajorVersion(e: MajorUpdateProcessEvent) {
        if (e.version == MajorUpdate.SONG_DB) {
            initializeSongDatabase()
        }
    }

    //
    // Song List Management
    //
    private fun initializeSongDatabase(input: String = context.loadRawString(R.raw.songs)) {
        chartBox.removeAll()
        songBox.removeAll()
        val lines = input.lines()
        if (SharedPrefsUtil.getUserInt(context, KEY_SONG_LIST_VERSION, -1) < lines[0].toInt()) {
            val dbSongs = mutableListOf<SongDB>()
            val dbCharts = mutableListOf<ChartDB>()
            lines.mapIndexedNotNull { idx, line ->
                if (idx == 0 || line.isEmpty()) {
                    return@mapIndexedNotNull null
                }
                val data = line.split(";")
                val id = data[0].toLong()
                val title = data[1]
                var preview = false
                val mix = data[2].let {
                    it.toLongOrNull() ?: it.substring(0, it.length - 1).let { seg ->
                        preview = true
                        seg.toLong()
                    }
                }
                dbSongs.add(SongDB(title, null, GameVersion.parse(mix), preview).also { song ->
                    songBox.attach(song)
                    PlayStyle.values().forEachIndexed { sIdx, style ->
                        DifficultyClass.values().forEachIndexed { dIdx, diff ->
                            val diffStr = data[3 + ((sIdx + 1) * dIdx)]
                            if (diffStr.isNotEmpty()) {
                                ChartDB(diff, diffStr.toInt(), style).also { chart ->
                                    dbCharts.add(chart)
                                    song.charts.add(chart)
                                }
                            }
                        }
                    }
                })
            }
            chartBox.put(dbCharts)
            songBox.put(dbSongs)
            invalidateIgnoredIds()
        }
    }

    //
    // Ignore List Data
    //
    private var ignoreLists: List<IgnoreList> =
        DataUtil.gson.fromJson(context.loadRawString(R.raw.ignore_lists), IgnoreLists::class.java)!!.lists
    val ignoreListIds get() = ignoreLists.map { it.id }
    val ignoreListTitles get() = ignoreLists.map { it.name }

    private val selectedIgnoreList: IgnoreList?
        get() = getIgnoreList(SharedPrefsUtil.getUserString(context, KEY_IMPORT_IGNORE, "ACE_US")!!)

    fun getIgnoreList(id: String) = ignoreLists.first { it.id == id }

    private var mSelectedIgnoreSongIds: LongArray? = null
    private var mSelectedIgnoreChartIds: LongArray? = null

    val selectedIgnoreSongIds: LongArray
        get() {
            if (mSelectedIgnoreSongIds == null) {
                val ignoredSongs = selectedIgnoreList?.songs?.map { it.title }?.toTypedArray()?.let { ignoreTitles ->
                    multipleSongTitleQuery.setParameters("titles", ignoreTitles).find().map { it.id }.toLongArray()
                } ?: LongArray(0)
                val ignoredVersions = selectedIgnoreList?.mixes?.map { it.stableId }?.toLongArray()?.let { versions ->
                    multipleGameVersionQuery.setParameters("versions", versions).find().map { it.id }.toLongArray()
                } ?: LongArray(0)
                mSelectedIgnoreSongIds = ignoredSongs.union(ignoredVersions.asIterable()).toLongArray()
            }
            return mSelectedIgnoreSongIds!!
        }
    val selectedIgnoreChartIds: LongArray
        get() {
            if (mSelectedIgnoreChartIds == null) {
                mSelectedIgnoreChartIds = selectedIgnoreList?.charts?.mapNotNull { chart ->
                    val song = songTitleQuery.setParameter("title", chart.title).findFirst()
                    return@mapNotNull song?.charts?.firstOrNull { it.difficultyClass == chart.difficultyClass }?.id
                }?.toLongArray() ?: LongArray(0)
            }
            return mSelectedIgnoreChartIds!!
        }

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
    private val gameVersionQuery = songBox.query()
        .equal(SongDB_.version, -1).parameterAlias("version")
        .build()
    private val multipleGameVersionQuery = songBox.query()
        .`in`(SongDB_.version, LongArray(0)).parameterAlias("versions")
        .build()
    private val chartDifficultyQuery = chartBox.query().apply {
        equal(ChartDB_.difficultyNumber, 0).parameterAlias("difficulty")
        equal(ChartDB_.playStyle, 0).parameterAlias("playStyle")
        link(ChartDB_.song).notIn(SongDB_.id, selectedIgnoreSongIds)
        notIn(ChartDB_.id, selectedIgnoreChartIds)
    }.build()

    fun getSongById(id: Long): SongDB? = songBox.get(id)

    fun getSongsById(ids: LongArray): MutableList<SongDB> = songBox.get(ids)

    fun getSongByName(name: String): SongDB? =
        songTitleQuery.setParameter("title", name).findFirst()

    fun getChartById(id: Long): ChartDB? = chartBox.get(id)

    fun getChartsById(ids: LongArray): MutableList<ChartDB> = chartBox.get(ids)

    fun getChartsByDifficulty(difficulty: Int, playStyle: PlayStyle): MutableList<ChartDB> =
        chartDifficultyQuery.setParameter("difficulty", difficulty.toLong())
            .setParameter("playStyle", playStyle.stableId)
            .find()

    fun getChartsByDifficulty(difficultyList: IntArray, playStyle: PlayStyle): MutableList<ChartDB> = mutableListOf<ChartDB>().apply {
        difficultyList.forEach {
            addAll(chartDifficultyQuery
                .setParameter("difficulty", it.toLong())
                .setParameter("playStyle", playStyle.stableId)
                .find())
        }
    }

    fun getOrCreateSong(name: String, artist: String? = null): SongDB =
        getSongByName(name) ?: SongDB(name, artist).also {
            songBox.put(it)
        }

    /**
     * Nulls out the list of invalid IDs, to regenerate them
     */
    fun invalidateIgnoredIds() {
        mSelectedIgnoreSongIds = null
        mSelectedIgnoreChartIds = null
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
            PlayStyle.values().forEach { style ->
                DifficultyClass.values().forEach { diff ->
                    val chart = chartsCopy.firstOrNull { it.playStyle == style && it.difficultyClass == diff }
                    if (chart != null) {
                        chartsCopy.remove(chart)
                        builder.append("${chart.difficultyNumber};")
                    } else {
                        builder.append(";")
                    }
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

    companion object {
        const val SONGS_FILE_NAME = "songs.csv"
    }
}

class UnexpectedDifficultyNumberException(chart: ChartDB, newDiff: Int): Exception(
    "Chart ${chart.song.target.title} ${chart.playStyle} ${chart.difficultyClass} changed: ${chart.difficultyNumber} -> $newDiff")
