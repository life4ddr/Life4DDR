package com.perrigogames.life4.ktor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SanbaiAuthTokenRequest(
    @SerialName("client_id") val clientId: String,
    @SerialName("client_secret") val clientSecret: String,
    @SerialName("grant_type") val grantType: String,
    val code: String? = null,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("redirect_uri") val redirectUri: String? = null
)

@Serializable
data class SanbaiAuthTokenResponse(
    @SerialName("player_id") val playerId: String,
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("token_type") val tokenType: String,
    val scope: String,
    @SerialName("refresh_token") val refreshToken: String
)
