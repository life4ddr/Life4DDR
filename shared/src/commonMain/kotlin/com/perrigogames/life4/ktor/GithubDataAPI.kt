package com.perrigogames.life4.ktor

import com.perrigogames.life4.data.IgnoreListData
import com.perrigogames.life4.data.LadderRankData
import com.perrigogames.life4.data.MessageOfTheDay
import com.perrigogames.life4.data.TrialData

/**
 * API interface for obtaining core application files from Github
 */
interface GithubDataAPI {
    suspend fun getLadderRanks(): LadderRankData

    suspend fun getSongList(): String

    suspend fun getIgnoreLists(): IgnoreListData

    suspend fun getTrials(): TrialData

    suspend fun getMotd(): MessageOfTheDay

    companion object {
        const val IGNORES_FILE_NAME = "ignore_lists.json"
        const val MOTD_FILE_NAME = "motd.json"
        const val PARTIAL_DIFFICULTY_FILE_NAME = "partial_difficulties.json"
        const val PLACEMENTS_FILE_NAME = "placements.json"
        const val RANKS_FILE_NAME = "ranks.json"
        const val SONGS_FILE_NAME = "songs.csv"
        const val TRIALS_FILE_NAME = "trials.json"
    }
}
