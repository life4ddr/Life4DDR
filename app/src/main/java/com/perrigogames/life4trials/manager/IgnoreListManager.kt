package com.perrigogames.life4trials.manager

import android.content.Context
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_IMPORT_GAME_VERSION
import com.perrigogames.life4trials.api.GithubDataAPI
import com.perrigogames.life4trials.api.MajorVersionedRemoteData
import com.perrigogames.life4trials.data.IgnoreList
import com.perrigogames.life4trials.data.IgnoreLists
import com.perrigogames.life4trials.db.SongDB
import com.perrigogames.life4trials.db.SongDB_
import com.perrigogames.life4trials.util.DataUtil

class IgnoreListManager(private val context: Context,
                        private val githubDataAPI: GithubDataAPI,
                        private val settingsManager: SettingsManager): BaseManager() {

    private val ignoreLists = object: MajorVersionedRemoteData<IgnoreLists>(context, R.raw.ignore_lists_v2,
        SongDataManager.IGNORES_FILE_NAME, 1) {
        override suspend fun getRemoteResponse() = githubDataAPI.getIgnoreLists()
        override fun createLocalDataFromText(text: String) = DataUtil.gson.fromJson(text, IgnoreLists::class.java)
        override fun onNewDataLoaded(newData: IgnoreLists) {
            super.onNewDataLoaded(newData)
            newData.evaluateIgnoreLists()
        }
    }

    init {
        ignoreLists.start()
    }

    val ignoreListIds get() = ignoreLists.data.lists.map { it.id }
    val ignoreListTitles get() = ignoreLists.data.lists.map { it.name }

    fun getIgnoreList(id: String): IgnoreList =
        ignoreLists.data.lists.firstOrNull { it.id == id } ?: getIgnoreList(SongDataManager.DEFAULT_IGNORE_VERSION)

    val selectedVersion: String
        get() = settingsManager.getUserString(KEY_IMPORT_GAME_VERSION, SongDataManager.DEFAULT_IGNORE_VERSION)!!
    val selectedIgnoreList: IgnoreList?
        get() = getIgnoreList(selectedVersion)

    private var mSelectedIgnoreSongIds: LongArray? = null
    private var mSelectedIgnoreChartIds: LongArray? = null

    val selectedIgnoreSongIds: LongArray
        get() {
            if (mSelectedIgnoreSongIds == null) {
                mSelectedIgnoreSongIds = selectedIgnoreList?.resolvedSongs?.map { it.title }?.toTypedArray()?.let { ignoreTitles ->
                    val versionId = selectedIgnoreList!!.baseVersion.stableId
                    blockedSongQuery(ignoreTitles, versionId, versionId + 1).find().map { it.id }.toLongArray()
                } ?: LongArray(0)
            }
            return mSelectedIgnoreSongIds!!
        }
    val selectedIgnoreChartIds: LongArray
        get() {
            if (mSelectedIgnoreChartIds == null) {
                mSelectedIgnoreChartIds = selectedIgnoreList?.resolvedCharts?.mapNotNull { chart ->
                    val song = songTitleQuery.setParameter("title", chart.title).findFirst()
                    return@mapNotNull song?.charts?.firstOrNull { it.difficultyClass == chart.difficultyClass }?.id
                }?.toLongArray() ?: LongArray(0)
            }
            return mSelectedIgnoreChartIds!!
        }

    /**
     * Nulls out the list of invalid IDs, to regenerate them
     */
    fun invalidateIgnoredIds() {
        mSelectedIgnoreSongIds = null
        mSelectedIgnoreChartIds = null
    }

    // FIXME these need to be a separate repo, copied from SongDataManager
    private val songBox get() = objectBox.boxFor(SongDB::class.java)
    private val songTitleQuery = songBox.query()
        .equal(SongDB_.title, "").parameterAlias("title")
        .build()
    private fun blockedSongQuery(titles: Array<String>, version: Long, previewVersion: Long) = songBox.query()
        .greater(SongDB_.version, previewVersion) // block everything higher than preview version
        .or().greater(SongDB_.version, version).and().equal(SongDB_.preview, false) // block non-preview songs in preview versions
        .or().`in`(SongDB_.title, titles) // block songs in the supplied list
        .build()
}