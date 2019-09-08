package com.perrigogames.life4trials.data

import com.google.gson.annotations.SerializedName
import io.objectbox.converter.PropertyConverter

enum class GameVersion(val stableId: Long) {
    @SerializedName("UNKNOWN") UNKNOWN(0),
    @SerializedName("DDR_1ST_MIX") DDR_1ST_MIX(1),
    @SerializedName("DDR_2ND_MIX") DDR_2ND_MIX(2),
    @SerializedName("DDR_3RD_MIX") DDR_3RD_MIX(3),
    @SerializedName("DDR_4TH_MIX") DDR_4TH_MIX(4),
    @SerializedName("DDR_5TH_MIX") DDR_5TH_MIX(5),
    @SerializedName("MAX") MAX(6),
    @SerializedName("MAX2") MAX2(7),
    @SerializedName("EXTREME") EXTREME(8),
    @SerializedName("SUPERNOVA") SUPERNOVA(9),
    @SerializedName("SUPERNOVA2") SUPERNOVA2(10),
    @SerializedName("X") X(11),
    @SerializedName("X2") X2(12),
    @SerializedName("X3_VS_2ND_MIX") X3_VS_2ND_MIX(13),
    @SerializedName("DDR_2013") DDR_2013(14),
    @SerializedName("DDR_2014") DDR_2014(15),
    @SerializedName("ACE") ACE(16),
    @SerializedName("A20") A20(17);

    companion object {
        fun parse(stableId: Long?) = stableId?.let { id -> values().firstOrNull { it.stableId == id } }
    }
}

class GameVersionConverter: PropertyConverter<GameVersion, Long> {
    override fun convertToDatabaseValue(property: GameVersion?): Long = (property ?: GameVersion.UNKNOWN).stableId
    override fun convertToEntityProperty(id: Long?): GameVersion? = GameVersion.parse(id)
}