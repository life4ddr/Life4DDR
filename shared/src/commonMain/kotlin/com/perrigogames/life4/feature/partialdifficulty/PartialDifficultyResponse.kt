package com.perrigogames.life4.feature.partialdifficulty

import com.perrigogames.life4.data.Versioned
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PartialDifficultyResponse(
    override val version: Int,
    val data: Map<String, Map<String, List<PartialDifficultyEntry>>>,
) : Versioned

@Serializable
data class PartialDifficultyEntry(
    @SerialName("song_id") val skillId: String,
    val difficulty: Int,
    @SerialName("youtube_link") val youtubeLink: String,
    @SerialName("step_chart_link") val stepChartLink: String,
)