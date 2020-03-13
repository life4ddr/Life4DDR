package com.perrigogames.life4.ktor

import co.touchlab.stately.ensureNeverFrozen
import com.perrigogames.life4.data.IgnoreListData
import com.perrigogames.life4.data.LadderRankData
import com.perrigogames.life4.data.TrialData
import com.perrigogames.life4.isDebug
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.http.takeFrom

class GithubDataImpl: GithubDataAPI {
    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    init {
        ensureNeverFrozen()
    }

    override suspend fun getLadderRanks(): LadderRankData = client.get { github(GithubDataAPI.RANKS_FILE_NAME) }

    override suspend fun getSongList(): String = client.get { github(GithubDataAPI.SONGS_FILE_NAME) }

    override suspend fun getIgnoreLists(): IgnoreListData = client.get { github(GithubDataAPI.IGNORES_FILE_NAME) }

    override suspend fun getTrials(): TrialData = client.get { github(GithubDataAPI.TRIALS_FILE_NAME) }

    private fun HttpRequestBuilder.github(path: String) {
        val githubTarget = if (isDebug) "remote-data-test" else "remote-data"
        url {
            takeFrom("https://raw.githubusercontent.com/PerrigoGames/Life4DDR-Trials/$githubTarget/app/src/main/res/raw/")
            encodedPath = path
        }
    }
}
