package com.perrigogames.life4trials.com.perrigogames.life4trials.data

//import com.perrigogames.life4.data.*
//import com.perrigogames.life4.enums.ClearType.*
//import com.perrigogames.life4.data.TrialData.Companion.TRIAL_LENGTH
//import com.perrigogames.life4.data.TrialRank.*
//import com.perrigogames.life4.enums.DifficultyClass
//import junit.framework.Assert.assertEquals
//import org.junit.Test

class TrialSessionTest {
//
//    @Test
//    fun testHighestRank_exScore() {
//        val trial = testTrial(listOf(
//            TrialGoalSet(COBALT, exMissing = 12),
//            TrialGoalSet(AMETHYST, exMissing = 5),
//            TrialGoalSet(EMERALD, exMissing = 1)))
//        val session = TrialSession(trial, EMERALD)
//        assertEquals(EMERALD, session.highestPossibleRank)
//
//        session.modify(0, EMERALD) { }  // -0 EX
//        session.modify(1, AMETHYST) { it.exScore = SONG_EX - 5  } // -5 EX
//        session.modify(2, COBALT) { it.exScore = SONG_EX - 5  } // -10 EX
//        session.modify(3, null) { it.exScore = SONG_EX - 3  } // -13 EX
//    }
//
//    @Test
//    fun testHighestRank_badJudgments() {
//        val trial = testTrial(listOf(
//            TrialGoalSet(AMETHYST, judge = 3),
//            TrialGoalSet(EMERALD, judge = 1)))
//        val session = TrialSession(trial, EMERALD)
//        assertEquals(EMERALD, session.highestPossibleRank)
//
//        session.modify(0, EMERALD) { it.badJudges = 1  } // 1 bad judgments
//        session.modify(1, AMETHYST) { it.badJudges = 1 }  // 2 bad judgments
//        session.modify(2, AMETHYST) { it.badJudges = 1  } // 3 bad judgments
//        session.modify(3, null) { it.badJudges = 1 }  // 4 bad judgments
//    }
//
//    @Test
//    fun testHighestRank_misses() {
//        val trial = testTrial(listOf(
//            TrialGoalSet(AMETHYST, miss = 3),
//            TrialGoalSet(EMERALD, miss = 1)))
//        val session = TrialSession(trial, EMERALD)
//
//        session.modify(0, EMERALD) { it.misses = 1 }  // 1 miss
//        session.modify(1, AMETHYST) { it.misses = 1  } // 2 miss
//        session.modify(2, AMETHYST) { it.misses = 1 }  // 3 miss
//        session.modify(3, null) { it.misses = 1 }  // 4 miss
//    }
//
//    @Test
//    fun testHighestRank_missesEach() {
//        val trial = testTrial(listOf(
//            TrialGoalSet(AMETHYST, missEach = 3),
//            TrialGoalSet(EMERALD, missEach = 1)))
//        val session = TrialSession(trial, EMERALD)
//
//        session.modify(0, EMERALD) { it.misses = 0 }
//        session.modify(1, EMERALD) { it.misses = 1 }
//        session.modify(2, AMETHYST) { it.misses = 2 }
//        session.modify(3, null) { it.misses = 4 }
//    }
//
//    @Test
//    fun testHighestRank_score() {
//        val trial = testTrial(listOf(
//            TrialGoalSet(AMETHYST, score = listOf(998000, 997000, 996000, 995000)),
//            TrialGoalSet(EMERALD, score = listOf(999000, 999000, 999000, 999000))))
//        val session = TrialSession(trial, EMERALD)
//
//        session.modify(0, EMERALD) { it.score = 999000 }
//        session.modify(0, AMETHYST) { it.score = 998000 } //replace
//        session.modify(1, AMETHYST) { it.score = 999000 }
//        session.modify(2, AMETHYST) { it.score = 995000 }
//        session.modify(3, null) { it.score = 995000 }
//    }
//
//    @Test
//    fun testHighestRank_indexedScore() {
//        val trial = testTrial(listOf(
//            TrialGoalSet(AMETHYST, scoreIndexed = listOf(998000, 997000, 996000, 995000)),
//            TrialGoalSet(EMERALD, scoreIndexed = listOf(999000, 999000, 999000, 999000))))
//        val session = TrialSession(trial, EMERALD)
//
//        session.modify(0, EMERALD) { it.score = 999000 }
//
//        session.modify(1, AMETHYST) { it.score = 998000 }
//        session.modify(1, EMERALD) { it.score = 999000 } // replace
//
//        session.modify(2, null) { it.score = 995000 }
//        session.modify(2, AMETHYST) { it.score = 996000 } // replace
//
//        session.modify(3, AMETHYST) { it.score = 995000 }
//        session.modify(3, null) { it.score = 994999 } // replace
//    }
//
//    @Test
//    fun testHighestRank_clear() {
//        val trial = testTrial(listOf(
//            TrialGoalSet(SILVER, clear = listOf(CLEAR, CLEAR, CLEAR, FAIL)),
//            TrialGoalSet(AMETHYST, clear = listOf(PERFECT_FULL_COMBO, GREAT_FULL_COMBO, CLEAR, LIFE4_CLEAR)),
//            TrialGoalSet(EMERALD, clear = listOf(MARVELOUS_FULL_COMBO, MARVELOUS_FULL_COMBO, MARVELOUS_FULL_COMBO, MARVELOUS_FULL_COMBO))))
//        val session = TrialSession(trial, EMERALD)
//        assertEquals(EMERALD, session.highestPossibleRank)
//
//        session.modify(0, EMERALD) { it.perfects = 0; it.badJudges = 0 } // MFC
//        session.modify(1, EMERALD) { it.perfects = 0; it.badJudges = 0 } // MFC
//        session.modify(1, AMETHYST) { it.perfects = 1; it.badJudges = 0 } // PFC
//        session.modify(0, AMETHYST) { it.perfects = 1; it.badJudges = 1; it.misses = 0 } // GFC
//        session.modify(3, SILVER) { it.passed = false } // fail
//        session.modify(3, AMETHYST) { } // clear
//        session.modify(2, SILVER) { } // clear
//        session.modify(2, AMETHYST) { it.misses = 3 } // life4
//        session.modify(2, SILVER) { it.passed = false } // fail
//        session.modify(0, null) { it.passed = false } // fail
//    }
//
//    @Test
//    fun testHighestRank_indexedClear() {
//        val trial = testTrial(listOf(
//            TrialGoalSet(SILVER, clearIndexed = listOf(CLEAR, CLEAR, CLEAR, FAIL)),
//            TrialGoalSet(AMETHYST, clearIndexed = listOf(PERFECT_FULL_COMBO, GREAT_FULL_COMBO, CLEAR, LIFE4_CLEAR)),
//            TrialGoalSet(EMERALD, clearIndexed = listOf(MARVELOUS_FULL_COMBO, MARVELOUS_FULL_COMBO, MARVELOUS_FULL_COMBO, MARVELOUS_FULL_COMBO))))
//        val session = TrialSession(trial, EMERALD)
//        assertEquals(EMERALD, session.highestPossibleRank)
//
//        session.modify(0, EMERALD) { it.perfects = 0; it.badJudges = 0 } // MFC
//        session.modify(1, EMERALD) { it.perfects = 0; it.badJudges = 0 } // MFC
//        session.modify(0, AMETHYST) { it.perfects = 1; it.badJudges = 0 } // PFC
//        session.modify(1, AMETHYST) { it.perfects = 1; it.badJudges = 1; it.misses = 0 } // GFC
//        session.modify(2, null) { it.passed = false } // fail
//        session.modify(2, AMETHYST) { } // clear
//        session.modify(3, SILVER) { } // clear
//        session.modify(3, AMETHYST) { it.misses = 3 } // life4
//        session.modify(3, SILVER) { it.passed = false } // fail
//    }
//
//    companion object {
//
//        const val SONG_EX = 1000
//
//        private fun testTrial(goals: List<TrialGoalSet>) = Trial(
//            "test",
//            "Test Trial",
//            "Test Author",
//            TrialType.TRIAL,
//            null,
//            false,
//            null,
//            null,
//            null,
//            1,
//            goals,
//            SONG_EX * TRIAL_LENGTH,
//            null,
//            false,
//            (1..TRIAL_LENGTH).map { idx ->
//                Song(
//                    "Song $idx",
//                    idx,
//                    DifficultyClass.CHALLENGE,
//                    SONG_EX,
//                    null
//                )
//            })
//
//        /**
//         * Creates (and automatically adds) a [SongResult] to this Trial
//         */
//        private inline fun TrialSession.logSong(idx: Int, block: (SongResult) -> Unit): SongResult =
//            SongResult(
//                trial.songs[idx],
//                score = 1000000,
//                exScore = SONG_EX
//            ).also {
//                block(it)
//                results[idx] = it
//            }
//
//        /**
//         * Performs an action, then asserts the sessions highest rank
//         */
//        private inline fun TrialSession.modify(resultIndex: Int, expectedRank: TrialRank?, action: (SongResult) -> Unit) {
//            logSong(resultIndex, action)
//            assertEquals(expectedRank, highestPossibleRank)
//        }
//    }
}
