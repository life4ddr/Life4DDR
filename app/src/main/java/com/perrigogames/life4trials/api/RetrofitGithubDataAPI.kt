package com.perrigogames.life4trials.api

import com.perrigogames.life4.data.TrialData
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.IGNORES_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.RANKS_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.SONGS_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.TRIALS_FILE_NAME
import com.perrigogames.life4trials.data.IgnoreListData
import com.perrigogames.life4trials.data.LadderRankData
import retrofit2.Response
import retrofit2.http.GET

/**
 * API interface for obtaining core application files from Github
 */
interface RetrofitGithubDataAPI {

    @GET(RANKS_FILE_NAME)
    suspend fun getLadderRanks(): Response<LadderRankData>

    @GET(SONGS_FILE_NAME)
    suspend fun getSongList(): Response<String>

    @GET(IGNORES_FILE_NAME)
    suspend fun getIgnoreLists(): Response<IgnoreListData>

    @GET(TRIALS_FILE_NAME)
    suspend fun getTrials(): Response<TrialData>
}
