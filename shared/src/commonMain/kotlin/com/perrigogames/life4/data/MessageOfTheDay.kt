package com.perrigogames.life4.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageOfTheDay(
    @SerialName("message") override val version: Long,
    @SerialName("image_url") val imageUrl: String,
    val body: String,
    @SerialName("white_close") val useWhiteCloseIcon: Boolean = false,
): Versioned