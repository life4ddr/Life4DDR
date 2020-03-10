package com.perrigogames.life4trials.api

import com.google.gson.annotations.SerializedName
import com.perrigogames.life4.data.LadderRank
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface Life4API{

    @GET("api/player/{player}")
    suspend fun getPlayer(@Path("player") playerName: String): Response<List<ApiPlayer>>

    @GET("api/players/all")
    suspend fun getAllPlayers(): Response<List<ApiPlayer>>
}

class ApiPlayer(
    @SerializedName("playerID") val id: Int,
    @SerializedName("playerName") val name: String,
    @SerializedName("playerRank") private val rankString: String,
    @SerializedName("playerDateEarned") val playerDateEarned: String, //FIXME
    @SerializedName("twitterHandle") val twitterHandle: String?,
    @SerializedName("discordHandle") val discordHandle: String?,
    @SerializedName("playerRivalCode") val playerRivalCode: String?,
    @SerializedName("activeStatus") val activeStatus: Boolean) {

    val pending: Boolean get() = rankString.endsWith("(P)")
    val rank: LadderRank? get() = LadderRank.parse(rankString.replace(" (P)", ""))

    override fun toString() = "($id) $name - $rank ($rankString)"
}
