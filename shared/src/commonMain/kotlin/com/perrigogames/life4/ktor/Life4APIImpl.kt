package com.perrigogames.life4.ktor

import co.touchlab.stately.ensureNeverFrozen
import com.perrigogames.life4.isDebug
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.http.takeFrom

class Life4APIImpl: Life4API {
    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    init {
        ensureNeverFrozen()
    }

    //FIXME use the proper endpoint
    override suspend fun getRecords(): RecordResult = client.get { life4Request(GithubDataAPI.RANKS_FILE_NAME) }

    private fun HttpRequestBuilder.life4Request(path: String) {
        val githubTarget = if (isDebug) "remote-data-test" else "remote-data"
        url {
            //FIXME retarget this to LIFE4 API
            takeFrom("https://raw.githubusercontent.com/PerrigoGames/Life4DDR-Trials/$githubTarget/app/src/main/res/raw/")
            encodedPath = path
        }
    }
}
