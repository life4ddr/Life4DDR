package com.perrigogames.life4.ktor

import com.perrigogames.life4.api.sanbaiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.call.receive
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import org.koin.core.component.KoinComponent

class SanbaiAPIImpl(private val log: co.touchlab.kermit.Logger) : SanbaiAPI, KoinComponent {

    private val client: HttpClient = sanbaiHttpClient(log)

    override suspend fun getScores(): List<SanbaiScoreResult> {
        val response: HttpResponse = client.get("https://3icecream.com/dev/api/v1/get_scores") {
            parameter("access_token", "FIXME")
        }

        if (!response.status.isSuccess()) {
            throw RuntimeException("Failed to fetch scores: ${response.status}")
        }

        return response.body<List<SanbaiScoreResult>>()
    }

    override suspend fun getPlayerId(): String {
        val response: HttpResponse = client.get("https://3icecream.com/dev/api/v1/get_player_id") {
            parameter("access_token", "FIXME")
        }

        if (!response.status.isSuccess()) {
            throw RuntimeException("Failed to fetch player id: ${response.status}")
        }

        return response.body<String>()
    }
}