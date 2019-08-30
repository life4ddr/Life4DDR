package com.perrigogames.life4trials.data

import com.google.gson.annotations.SerializedName
import io.objectbox.converter.PropertyConverter

enum class PlayStyle(val stableId: Long) {
    @SerializedName("single") SINGLE(1),
    @SerializedName("double") DOUBLE(2);

    companion object {
        fun parse(stableId: Long?) = stableId?.let { id -> values().firstOrNull { it.stableId == id } }

        fun parse(chartString: String): PlayStyle? = when {
            chartString.endsWith("SP") -> SINGLE
            chartString.endsWith("DP") -> DOUBLE
            else -> null
        }
    }
}

class PlayStyleConverter: PropertyConverter<PlayStyle, Long> {
    override fun convertToDatabaseValue(property: PlayStyle?): Long = (property ?: PlayStyle.SINGLE).stableId
    override fun convertToEntityProperty(id: Long?): PlayStyle? = PlayStyle.parse(id)
}