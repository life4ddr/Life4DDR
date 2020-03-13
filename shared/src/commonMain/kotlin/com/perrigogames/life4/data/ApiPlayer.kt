package com.perrigogames.life4.data

import kotlinx.serialization.SerialName

class ApiPlayer(
    @SerialName("playerID") val id: Int,
    @SerialName("playerName") val name: String,
    @SerialName("playerRank") private val rankString: String,
    @SerialName("playerDateEarned") val playerDateEarned: String, //FIXME
    @SerialName("twitterHandle") val twitterHandle: String?,
    @SerialName("discordHandle") val discordHandle: String?,
    @SerialName("playerRivalCode") val playerRivalCode: String?,
    @SerialName("activeStatus") val activeStatus: Boolean) {

    val pending: Boolean get() = rankString.endsWith("(P)")
    val rank: LadderRank? get() = LadderRank.parse(rankString.replace(" (P)", ""))

    override fun toString() = "($id) $name - $rank ($rankString)"
}
