package com.perrigogames.life4trials.data

import androidx.annotation.ColorRes
import com.perrigogames.life4trials.R
import com.squareup.moshi.Json

enum class DifficultyClass(@ColorRes val colorRes: Int) {
    @Json(name="beginner") BEGINNER(R.color.difficultyBeginner),
    @Json(name="basic") BASIC(R.color.difficultyBasic),
    @Json(name="difficult") DIFFICULT(R.color.difficultyDifficult),
    @Json(name="expert") EXPERT(R.color.difficultyExpert),
    @Json(name="challenge") CHALLENGE(R.color.difficultyChallenge)
}