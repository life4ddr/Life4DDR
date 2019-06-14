package com.perrigogames.life4trials.data

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import com.perrigogames.life4trials.R

enum class DifficultyClass(@ColorRes val colorRes: Int,
                           @StringRes val abbreviationRes: Int) {
    @SerializedName("beginner") BEGINNER(R.color.difficultyBeginner, R.string.bgsp),
    @SerializedName("basic") BASIC(R.color.difficultyBasic, R.string.bsp),
    @SerializedName("difficult") DIFFICULT(R.color.difficultyDifficult, R.string.dsp),
    @SerializedName("expert") EXPERT(R.color.difficultyExpert, R.string.esp),
    @SerializedName("challenge") CHALLENGE(R.color.difficultyChallenge, R.string.csp)
}