package com.perrigogames.life4trials.data

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import com.perrigogames.life4trials.R
import io.objectbox.converter.PropertyConverter

/**
 * Enum to describe a class of difficulty, inside which more specific difficulties
 * are more or less in the same range as each other.
 */
enum class DifficultyClass(val stableId: Long,
                           @ColorRes val colorRes: Int,
                           @StringRes val abbreviationRes: Int) {
    @SerializedName("beginner") BEGINNER(1, R.color.difficultyBeginner, R.string.bgsp),
    @SerializedName("basic") BASIC(2, R.color.difficultyBasic, R.string.bsp),
    @SerializedName("difficult") DIFFICULT(3, R.color.difficultyDifficult, R.string.dsp),
    @SerializedName("expert") EXPERT(4, R.color.difficultyExpert, R.string.esp),
    @SerializedName("challenge") CHALLENGE(5, R.color.difficultyChallenge, R.string.csp);

    companion object {
        fun parse(stableId: Long?) = stableId?.let { id -> values().firstOrNull { it.stableId == id } }
        fun parse(chartString: String): DifficultyClass? = when {
            chartString.startsWith("b") -> BEGINNER
            chartString.startsWith("B") -> BASIC
            chartString.startsWith("D") -> DIFFICULT
            chartString.startsWith("E") -> EXPERT
            chartString.startsWith("C") -> CHALLENGE
            else -> null
        }
    }
}

class DifficultyClassConverter: PropertyConverter<DifficultyClass, Long> {
    override fun convertToDatabaseValue(property: DifficultyClass?): Long = (property ?: DifficultyClass.BEGINNER).stableId
    override fun convertToEntityProperty(id: Long?): DifficultyClass? = DifficultyClass.parse(id)
}