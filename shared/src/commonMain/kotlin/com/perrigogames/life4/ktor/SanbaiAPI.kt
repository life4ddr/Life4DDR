package com.perrigogames.life4.ktor

import com.perrigogames.life4.db.ChartResult
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface SanbaiAPI {
    suspend fun getSongData(): SanbaiSongListResponse

    fun getAuthorizeUrl(): String
    suspend fun getSessionToken(code: String): SanbaiAuthTokenResponse
    suspend fun getScores(): List<SanbaiScoreResult>?
    suspend fun getPlayerId(): String

    companion object {
        const val SANBAI_CLIENT_ID = "FIXME" // FIXME
        const val SANBAI_CLIENT_SECRET = "FIXME" // FIXME
    }
}

@Serializable
data class SanbaiSongListResponse(
    val lastUpdated: Instant,
    val songs: List<SanbaiSongListResponseItem>
)

@Serializable
data class SanbaiSongListResponseItem(
    @SerialName("song_id") val songId: String,
    @SerialName("song_name") val songName: String,
    @SerialName("alternate_name") val alternateName: String? = null,
    @SerialName("searchable_name") val searchableName: String? = null,
    @SerialName("romanized_name") val romanizedName: String? = null,
    val alphabet: String,
    val deleted: Int? = null,
    @SerialName("version_num") val versionNum: Int,
    val ratings: List<Int>,
    val tiers: List<Double>,
    @SerialName("lock_types") val lockTypes: List<Int>? = null
)

@Serializable
data class SanbaiScoreResult(
    @SerialName("song_id") val songId: String,
    @SerialName("song_name") val songName: String,
    val difficulty: Int,
    val score: Int,
    val lamp: Int,
    val flare: Int? = null,
    @SerialName("flare_skill") val flareSkill: Int? = null,
    @SerialName("time_uploaded") val timeUploaded: Long,
    @SerialName("time_played") val timePlayed: Long?
)

fun SanbaiScoreResult.toChartResult(): ChartResult {
    val (playStyle, difficultyClass) = when (difficulty) {
        0 -> PlayStyle.SINGLE to DifficultyClass.BEGINNER
        1 -> PlayStyle.SINGLE to DifficultyClass.BASIC
        2 -> PlayStyle.SINGLE to DifficultyClass.DIFFICULT
        3 -> PlayStyle.SINGLE to DifficultyClass.EXPERT
        4 -> PlayStyle.SINGLE to DifficultyClass.CHALLENGE
        5 -> PlayStyle.DOUBLE to DifficultyClass.BASIC
        6 -> PlayStyle.DOUBLE to DifficultyClass.DIFFICULT
        7 -> PlayStyle.DOUBLE to DifficultyClass.EXPERT
        8 -> PlayStyle.DOUBLE to DifficultyClass.CHALLENGE
        else -> throw IllegalArgumentException("Invalid difficulty value: $difficulty")
    }
    return ChartResult(
        skillId = songId,
        difficultyClass = difficultyClass,
        playStyle = playStyle,
        clearType = when(lamp) {
            0 -> ClearType.FAIL
            1 -> ClearType.CLEAR
            2 -> ClearType.LIFE4_CLEAR
            3 -> ClearType.GOOD_FULL_COMBO
            4 -> ClearType.GREAT_FULL_COMBO
            5 -> ClearType.PERFECT_FULL_COMBO
            6 -> ClearType.MARVELOUS_FULL_COMBO
            else -> ClearType.NO_PLAY
        },
        score = score.toLong(),
        exScore = null,
        flare = flare?.toLong(),
        flareSkill = flareSkill?.toLong()
    )
}