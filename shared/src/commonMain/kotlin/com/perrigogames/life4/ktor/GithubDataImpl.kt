package com.perrigogames.life4.ktor

import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
import com.perrigogames.life4.AppInfo
import com.perrigogames.life4.api.baseHttpClient
import com.perrigogames.life4.data.LadderRankData
import com.perrigogames.life4.data.MessageOfTheDay
import com.perrigogames.life4.feature.trials.data.TrialData
import com.perrigogames.life4.injectLogger
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GithubDataImpl: GithubDataAPI, KoinComponent {

    private val appInfo: AppInfo by inject()
    private val log: Logger by injectLogger("GithubData")
    private val client = baseHttpClient(log)

    init {
        ensureNeverFrozen()
    }

    override suspend fun getLadderRanks(): LadderRankData =
        client.get { webGithubithub(GithubDataAPI.RANKS_FILE_NAME) }.body()

    override suspend fun getSongList(): String =
        client.get { appGithub(GithubDataAPI.SONGS_FILE_NAME) }.body()

    override suspend fun getTrials(): TrialData =
        client.get { appGithub(GithubDataAPI.TRIALS_FILE_NAME) }.body()

    override suspend fun getMotd(): MessageOfTheDay =
        client.get { appGithub(GithubDataAPI.MOTD_FILE_NAME) }.body()

    private fun HttpRequestBuilder.appGithub(filename: String) {
        val githubTarget = if (appInfo.isDebug) "remote-data-test" else "remote-data"
        url {
            takeFrom("https://raw.githubusercontent.com/")
            encodedPath = "PerrigoGames/Life4DDR-Trials/$githubTarget/app/src/main/res/raw/$filename"
        }
    }

    private fun HttpRequestBuilder.webGithubithub(filename: String) {
        url {
            takeFrom("https://raw.githubusercontent.com/") //https://github.com/life4ddr/life4ddr.com/blob/main/life4/json/ranks.json
            encodedPath = "life4ddr/life4ddr.com/blob/main/life4/json/$filename"
        }
    }
}
