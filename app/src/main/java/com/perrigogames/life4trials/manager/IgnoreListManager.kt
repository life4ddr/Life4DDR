package com.perrigogames.life4trials.manager

import com.perrigogames.life4.SettingsKeys.KEY_IMPORT_GAME_VERSION
import com.perrigogames.life4.api.IgnoreListRemoteData
import com.perrigogames.life4.api.LocalDataReader
import com.perrigogames.life4.data.IgnoreGroup
import com.perrigogames.life4.data.IgnoreList
import com.perrigogames.life4.data.IgnoredSong
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.IGNORES_FILE_NAME
import com.perrigogames.life4.model.BaseModel
import com.perrigogames.life4trials.event.LadderRanksReplacedEvent
import com.perrigogames.life4trials.repo.SongRepo
import org.greenrobot.eventbus.EventBus
import org.koin.core.inject
import org.koin.core.qualifier.named

class IgnoreListManager: BaseModel() {

    private val songRepo: SongRepo by inject()
    private val eventBus: EventBus by inject()
    private val settingsManager: SettingsManager by inject()
    private val dataReader: LocalDataReader by inject(named(IGNORES_FILE_NAME))

    private val ignoreLists = IgnoreListRemoteData(dataReader)

    init {
        ignoreLists.start()
    }

    //
    // General Ignorelist
    //

    val ignoreListIds get() = ignoreLists.data.lists.map { it.id }
    val ignoreListTitles get() = ignoreLists.data.lists.map { it.name }

    fun getIgnoreList(id: String): IgnoreList =
        ignoreLists.data.lists.firstOrNull { it.id == id } ?: getIgnoreList(SongDataManager.DEFAULT_IGNORE_VERSION)

    //
    // Currently Selected
    //

    val selectedVersion: String
        get() = settingsManager.getUserString(KEY_IMPORT_GAME_VERSION, SongDataManager.DEFAULT_IGNORE_VERSION)!!
    val selectedIgnoreList: IgnoreList?
        get() = getIgnoreList(selectedVersion)
    val selectedIgnoreGroups: List<IgnoreGroup>?
        get() = selectedIgnoreList?.groups?.map { id -> ignoreLists.data.groupsMap[id] ?: error("Invalid group name $id") }

    private var mSelectedIgnoreSongIds: LongArray? = null
    private var mSelectedIgnoreChartIds: LongArray? = null

    val selectedIgnoreSongIds: LongArray
        get() {
            if (mSelectedIgnoreSongIds == null) {
                val unlocks = getAllUnlockedSongs()
                mSelectedIgnoreSongIds = selectedIgnoreList?.resolvedSongs?.filterNot { unlocks.contains(it) }?.map { it.title }?.toTypedArray()?.let { ignoreTitles ->
                    val versionId = selectedIgnoreList!!.baseVersion.stableId
                    songRepo.findBlockedSongs(ignoreTitles, versionId, versionId + 1).map { it.id }.toLongArray()
                } ?: LongArray(0)
            }
            return mSelectedIgnoreSongIds!!
        }
    val selectedIgnoreChartIds: LongArray
        get() {
            if (mSelectedIgnoreChartIds == null) {
                mSelectedIgnoreChartIds = selectedIgnoreList?.resolvedCharts?.mapNotNull { chart ->
                    val song = songRepo.findSongByTitle(chart.title)
                    return@mapNotNull song?.charts?.firstOrNull { it.difficultyClass == chart.difficultyClass }?.id
                }?.toLongArray() ?: LongArray(0)
            }
            return mSelectedIgnoreChartIds!!
        }

    //
    // Unlocks
    //

    fun getUnlockGroup(id: String) = ignoreLists.data.groups.firstOrNull { it.id == id }

    fun getGroupUnlockState(id: String): Long {
        return settingsManager.getUserLong("unlock_$id", 0L)
    }

    fun getGroupUnlockFlags(id: String): List<Boolean>? {
        return getUnlockGroup(id)?.fromStoredState(getGroupUnlockState(id))
    }

    fun getGroupUnlockedSongs(id: String): List<IgnoredSong>? {
        val flags = getGroupUnlockFlags(id)
        return getUnlockGroup(id)?.songs?.filterIndexed { idx, _ -> flags?.get(idx) ?: false }
    }

    fun getAllUnlockedSongs(): List<IgnoredSong> =
        ignoreLists.data.groups.mapNotNull { getGroupUnlockedSongs(it.id) }.flatten()

    fun setGroupUnlockState(id: String, state: Long) {
        settingsManager.setUserLong("unlock_$id", state)
        invalidateIgnoredIds()
        eventBus.post(LadderRanksReplacedEvent())
    }

    /**
     * Nulls out the list of invalid IDs, to regenerate them
     */
    fun invalidateIgnoredIds() {
        mSelectedIgnoreSongIds = null
        mSelectedIgnoreChartIds = null
    }
}
