package com.perrigogames.life4.ktor

import kotlinx.serialization.SerialName

interface SanbaiAPI {

    suspend fun getScores(): List<SanbaiScoreResult>
    suspend fun getPlayerId(): String
}

data class SanbaiScoreResult(
    @SerialName("song_id") val songId: String,
    @SerialName("song_name") val songName: String,
    val difficulty: Int,
    val score: Int,
    val lamp: Int,
    @SerialName("time_uploaded") val timeUploaded: Long,
    @SerialName("time_played") val timePlayed: Long?
)