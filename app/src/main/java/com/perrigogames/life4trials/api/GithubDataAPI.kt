package com.perrigogames.life4trials.api

import com.perrigogames.life4trials.data.IgnoreLists
import com.perrigogames.life4trials.data.LadderRankData
import com.perrigogames.life4trials.data.TrialData
import com.perrigogames.life4trials.manager.LadderManager
import com.perrigogames.life4trials.manager.SongDataManager
import com.perrigogames.life4trials.manager.TrialManager
import retrofit2.Response
import retrofit2.http.GET

/**
 * API interface for obtaining core application files from Github
 */
interface GithubDataAPI {

    @GET(LadderManager.RANKS_FILE_NAME)
    suspend fun getLadderRanks(): Response<LadderRankData>

    @GET(SongDataManager.SONGS_FILE_NAME)
    suspend fun getSongList(): Response<String>

    @GET(SongDataManager.IGNORES_FILE_NAME)
    suspend fun getIgnoreLists(): Response<IgnoreLists>

    @GET(TrialManager.TRIALS_FILE_NAME)
    suspend fun getTrials(): Response<TrialData>
}