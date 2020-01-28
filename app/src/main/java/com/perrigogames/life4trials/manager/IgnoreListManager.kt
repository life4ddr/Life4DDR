package com.perrigogames.life4trials.manager

import android.content.Context
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_IMPORT_GAME_VERSION
import com.perrigogames.life4trials.api.GithubDataAPI
import com.perrigogames.life4trials.api.MajorVersionedRemoteData
import com.perrigogames.life4trials.data.IgnoreList
import com.perrigogames.life4trials.data.IgnoreLists
import com.perrigogames.life4trials.util.DataUtil

class IgnoreListManager(private val context: Context,
                        private val githubDataAPI: GithubDataAPI,
                        private val settingsManager: SettingsManager): BaseManager() {

    private val ignoreLists = object: MajorVersionedRemoteData<IgnoreLists>(context, R.raw.ignore_lists,
        SongDataManager.IGNORES_FILE_NAME, 1) {
        override suspend fun getRemoteResponse() = githubDataAPI.getIgnoreLists()
        override fun createLocalDataFromText(text: String) = DataUtil.gson.fromJson(text, IgnoreLists::class.java)
    }

    init {
        ignoreLists.start()
    }

    val ignoreListIds get() = ignoreLists.data.lists.map { it.id }
    val ignoreListTitles get() = ignoreLists.data.lists.map { it.name }

    val selectedVersion: String
        get() = settingsManager.getUserString(KEY_IMPORT_GAME_VERSION, SongDataManager.DEFAULT_IGNORE_VERSION)!!
    val selectedIgnoreList: IgnoreList?
        get() = getIgnoreList(selectedVersion)

    fun getIgnoreList(id: String): IgnoreList =
        ignoreLists.data.lists.firstOrNull { it.id == id } ?: getIgnoreList(SongDataManager.DEFAULT_IGNORE_VERSION)
}