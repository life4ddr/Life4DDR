package com.perrigogames.life4.ktor

import co.touchlab.stately.ensureNeverFrozen
import com.perrigogames.life4.api.baseHttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class Life4APIImpl(
    private val log: co.touchlab.kermit.Logger,
    private val isDebug: Boolean
): Life4API {

    private val client = baseHttpClient(log)

    init {
        ensureNeverFrozen()
    }

    //FIXME use the proper endpoint
    override suspend fun getRecords(): RecordResult =
        client.get { life4Request(GithubDataAPI.RANKS_FILE_NAME) }.body()

    private fun HttpRequestBuilder.life4Request(path: String) {
        val githubTarget = if (isDebug) "remote-data-test" else "remote-data"
        url {
            //FIXME retarget this to LIFE4 API
            takeFrom("https://raw.githubusercontent.com/PerrigoGames/Life4DDR-Trials/$githubTarget/app/src/main/res/raw/")
            encodedPath = path
        }
    }
}
