package com.perrigogames.life4.feature.sanbai

import com.perrigogames.life4.feature.deeplink.IDeeplinkManager.Companion.SANBAI_AUTH_RETURN_PATH_FULL
import com.perrigogames.life4.ktor.SanbaiAPI
import com.perrigogames.life4.ktor.SanbaiAuthTokenRequest
import com.perrigogames.life4.ktor.SanbaiAuthTokenResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.seconds

fun sanbaiHttpClient(
    log: co.touchlab.kermit.Logger,
    sanbaiSettings: ISanbaiAPISettings
) = HttpClient {
    install(ContentNegotiation) {
        json()
    }
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                log.v { message }
            }
        }

        level = LogLevel.INFO
    }
    install(HttpTimeout) {
        val timeout = 30000L
        connectTimeoutMillis = timeout
        requestTimeoutMillis = timeout
        socketTimeoutMillis = timeout
    }
    install(Auth) {
        bearer {
            loadTokens {
                BearerTokens(
                    accessToken = sanbaiSettings.bearerToken,
                    refreshToken = sanbaiSettings.refreshToken
                )
            }
            refreshTokens {
                val tokenResponse = client.post("https://3icecream.com/oauth/token") {
                    setBody(
                        SanbaiAuthTokenRequest(
                            clientId = SanbaiAPI.SANBAI_CLIENT_ID,
                            clientSecret = SanbaiAPI.SANBAI_CLIENT_SECRET,
                            grantType = "refresh_token",
                            refreshToken = sanbaiSettings.refreshToken,
                            redirectUri = SANBAI_AUTH_RETURN_PATH_FULL
                        )
                    )
                }
                val response = tokenResponse.body<SanbaiAuthTokenResponse>()
                sanbaiSettings.setUserProperties(response)
                BearerTokens(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken
                )
            }
        }
    }
}

fun ISanbaiAPISettings.setUserProperties(
    response: SanbaiAuthTokenResponse
) = setUserProperties(
    bearerToken = response.accessToken,
    refreshToken = response.refreshToken,
    refreshExpires = Clock.System.now().plus(response.expiresIn.seconds),
    playerId = response.playerId,
)
