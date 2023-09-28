package com.perrigogames.life4trials.data

import androidx.annotation.ColorRes
import com.google.gson.annotations.SerializedName
import com.perrigogames.life4trials.R

enum class DifficultyClass(@ColorRes val colorRes: Int) {
    @SerializedName("beginner") BEGINNER(R.color.difficultyBeginner),
    @SerializedName("basic") BASIC(R.color.difficultyBasic),
    @SerializedName("difficult") DIFFICULT(R.color.difficultyDifficult),
    @SerializedName("expert") EXPERT(R.color.difficultyExpert),
    @SerializedName("challenge") CHALLENGE(R.color.difficultyChallenge)
}