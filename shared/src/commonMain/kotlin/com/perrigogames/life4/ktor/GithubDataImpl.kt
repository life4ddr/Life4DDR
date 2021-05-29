package com.perrigogames.life4.ktor

import co.touchlab.stately.ensureNeverFrozen
import com.perrigogames.life4.data.IgnoreListData
import com.perrigogames.life4.data.LadderRankData
import com.perrigogames.life4.data.MessageOfTheDay
import com.perrigogames.life4.data.TrialData
import com.perrigogames.life4.isDebug
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.core.KoinComponent
import org.koin.core.inject

class GithubDataImpl: GithubDataAPI, KoinComponent {

    private val json: Json by inject()

    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    init {
        ensureNeverFrozen()
    }

    override suspend fun getLadderRanks(): LadderRankData {
        val dataString: String = client.get { github(GithubDataAPI.RANKS_FILE_NAME) }
        return json.decodeFromString(dataString)
    }

    override suspend fun getSongList(): String = client.get { github(GithubDataAPI.SONGS_FILE_NAME) }

    override suspend fun getIgnoreLists(): IgnoreListData {
        val dataString: String = client.get { github(GithubDataAPI.IGNORES_FILE_NAME) }
        return json.decodeFromString(dataString)
    }

    override suspend fun getTrials(): TrialData {
        val dataString: String = client.get { github(GithubDataAPI.TRIALS_FILE_NAME) }
        return json.decodeFromString(dataString)
    }

    override suspend fun getMotd(): MessageOfTheDay {
        val dataString: String = client.get { github(GithubDataAPI.MOTD_FILE_NAME) }
        return json.decodeFromString(dataString)
    }

    private fun HttpRequestBuilder.github(filename: String) {
        val githubTarget = if (isDebug) "remote-data-test" else "remote-data"
        url {
            takeFrom("https://raw.githubusercontent.com/")
            encodedPath = "PerrigoGames/Life4DDR-Trials/$githubTarget/app/src/main/res/raw/$filename"
        }
    }
}
