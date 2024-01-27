@file:OptIn(ExperimentalSerializationApi::class, ExperimentalSerializationApi::class)

package com.perrigogames.life4.util

import com.perrigogames.life4.data.Song
import com.perrigogames.life4.data.SongResult
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.TrialRank.DIAMOND
import com.perrigogames.life4.enums.TrialRank.GOLD
import com.perrigogames.life4.enums.TrialRank.PLATINUM
import com.perrigogames.life4.enums.TrialRank.SILVER
import com.perrigogames.life4.enums.TrialType
import com.perrigogames.life4.feature.trialsession.TrialGoalSet
import kotlinx.serialization.ExperimentalSerializationApi

fun testTrial(
    songEx: List<Int> = TRIAL_SONG_EX
) = Trial(
    id = "id",
    name = "Test Trial",
    author = "Author",
    type = TrialType.TRIAL,
    placementRank = null,
    songs = listOf(
        testSong(index = 1, ex = songEx[0]),
        testSong(index = 2, ex = songEx[1]),
        testSong(index = 3, ex = songEx[2]),
        testSong(index = 4, ex = songEx[3]),
    ),
    difficulty = 16,
    totalEx = 1000,
    goals = listOf(
        TrialGoalSet(SILVER, score = listOf(_80k, _80k, _80k, _80k)),
        TrialGoalSet(GOLD, score = listOf(_95k, _95k, _90k, _90k)),
        TrialGoalSet(PLATINUM, miss = 10, exMissing = 300),
        TrialGoalSet(DIAMOND, miss = 5, exMissing = 150),
    ),
)

fun testSong(
    index: Int,
    ex: Int,
    difficultyClass: DifficultyClass = DifficultyClass.EXPERT,
) = Song(
    "Song $index",
    14 + index,
    difficultyClass,
    ex,
)

fun testSongResult(
    song: Song,
    score: Int? = null,
    exScore: Int? = null,
    misses: Int? = null,
    goods: Int? = null,
    greats: Int? = null,
    perfects: Int? = null,
    passed: Boolean = true,
) = SongResult(
    song = song,
    photoUriString = null,
    score = score,
    exScore = exScore,
    misses = misses,
    goods = goods,
    greats = greats,
    perfects = perfects,
    passed = passed,
)

val TRIAL_SONG_EX = listOf(240, 250, 250, 260)

private val _80k = 800_000
private val _90k = 900_000
private val _95k = 950_000
private val _99k = 990_000
