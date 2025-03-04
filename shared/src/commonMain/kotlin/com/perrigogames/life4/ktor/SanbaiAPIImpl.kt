package com.perrigogames.life4.ktor

import co.touchlab.kermit.Logger
import com.perrigogames.life4.feature.deeplink.IDeeplinkManager.Companion.SANBAI_AUTH_RETURN_PATH_FULL
import com.perrigogames.life4.feature.partialdifficulty.PartialDifficultyResponse
import com.perrigogames.life4.feature.sanbai.ISanbaiAPISettings
import com.perrigogames.life4.feature.sanbai.sanbaiHttpClient
import com.perrigogames.life4.feature.sanbai.setUserProperties
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.ktor.SanbaiAPI.Companion.SANBAI_CLIENT_ID
import com.perrigogames.life4.ktor.SanbaiAPI.Companion.SANBAI_CLIENT_SECRET
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SanbaiAPIImpl : SanbaiAPI, KoinComponent {

    private val json: Json by inject()
    private val logger: Logger by injectLogger("SanbaiAPI")
    private val sanbaiSettings: ISanbaiAPISettings by inject()
    private val client: HttpClient = sanbaiHttpClient(logger, sanbaiSettings)

    override suspend fun getSongData(): SanbaiSongListResponse {
        val response = client.get("https://3icecream.com/js/songdata.js") {
            contentType(ContentType.Application.JavaScript)
        }
        val lines = response.bodyAsText().split(";")
        val songData = lines[0].let { it.substring(startIndex = it.indexOf('[')) }
        val songs = json.decodeFromString<List<SanbaiSongListResponseItem>>(songData)
        val lastUpdatedLine = lines.subList(1, lines.size).find { it.contains("SONG_DATA_LAST_UPDATED") }!!
        val lastUpdated = lastUpdatedLine.substring(startIndex = lastUpdatedLine.indexOf('=') + 1).toLong()
        return SanbaiSongListResponse(
            songs = songs,
            lastUpdated = Instant.fromEpochMilliseconds(lastUpdated)
        )
    }

    override fun getAuthorizeUrl(): String {
        return "https://3icecream.com/oauth/authorize" +
                "?client_id=$SANBAI_CLIENT_ID" +
                "&response_type=code" +
                "&scope=read_scores" +
                "&redirect_uri=$SANBAI_AUTH_RETURN_PATH_FULL"
    }

    override suspend fun getSessionToken(code: String): SanbaiAuthTokenResponse {
        val response = client.post("https://3icecream.com/oauth/token") {
            setBody(SanbaiAuthTokenRequest(
                clientId = SANBAI_CLIENT_ID,
                clientSecret = SANBAI_CLIENT_SECRET,
                code = code,
                redirectUri = SANBAI_AUTH_RETURN_PATH_FULL,
                grantType = "authorization_code"
            ))
            contentType(ContentType.Application.Json)
        }
        return response.body<SanbaiAuthTokenResponse>().also { responseBody ->
            sanbaiSettings.setUserProperties(responseBody)
        }
    }

    override suspend fun getScores(): List<SanbaiScoreResult>? {
        val response: HttpResponse = client.post("https://3icecream.com/dev/api/v1/get_scores") {
            setBody(mapOf(
                "access_token" to sanbaiSettings.bearerToken
            ))
            contentType(ContentType.Application.Json)
        }

        if (!response.status.isSuccess()) {
            throw RuntimeException("Failed to fetch scores: ${response.status}")
        }

        return response.body<List<SanbaiScoreResult>>()
    }

    override suspend fun getPlayerId(): String {
        val response: HttpResponse = client.get("https://3icecream.com/dev/api/v1/get_player_id") {
        }

        if (!response.status.isSuccess()) {
            throw RuntimeException("Failed to fetch player id: ${response.status}")
        }

        return response.body<String>()
    }

    override suspend fun getPartialDifficulties(): PartialDifficultyResponse {
        val response: HttpResponse = client.get("https://3icecream.com/dev/api/v1/get_partial_difficulties") {
        }

        if (!response.status.isSuccess()) {
            throw RuntimeException("Failed to fetch partial difficulties: ${response.status}")
        }

        return response.body<PartialDifficultyResponse>()
    }
}