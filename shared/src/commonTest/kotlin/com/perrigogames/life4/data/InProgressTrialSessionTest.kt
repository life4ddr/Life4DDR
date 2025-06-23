package com.perrigogames.life4.data

import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.feature.trials.data.Trial
import com.perrigogames.life4.feature.trials.data.TrialGoalSet
import com.perrigogames.life4.feature.trials.data.TrialSong
import com.perrigogames.life4.feature.trials.enums.TrialRank
import com.perrigogames.life4.feature.trials.enums.TrialType
import com.perrigogames.life4.util.testSongResult
import com.perrigogames.life4.util.setTestSongResult
import com.perrigogames.life4.util.testTrial
import kotlin.test.Test
import kotlin.test.assertEquals

class InProgressTrialSessionTest {

    private val trial = testTrial()

    private fun testSession(
        goalRank: TrialRank = TrialRank.GOLD,
        results: Array<SongResult?> = arrayOfNulls(4)
    ) = InProgressTrialSession(
        trial = trial,
        results = Array(4) { if (it < results.size) results[it] else null }
    )

    @Test
    fun `Test empty session`() {
        val subject = testSession()

        subject.assertProgress(current = 0, currentMax = 1000, max = 1000)
        subject.assertStepCounts(misses = 0, judge = 0)
        assertEquals(false, subject.hasStarted)
    }

    @Test
    fun `Test imperfect partially filled session`() {
        val subject = testSession(
            results = arrayOf(
                testSongResult(trial.songs[0], score = 900_000, exScore = 230, misses = 2),
            ),
        )

        subject.assertProgress(current = 230, currentMax = 990, max = 1000)
        subject.assertStepCounts(misses = 2, judge = 2, vJudge = false)
        assertEquals(true, subject.hasStarted)
    }

    @Test
    fun `Test imperfect filled session`() {
        val subject = testSession(
            results = arrayOf(
                testSongResult(trial.songs[0], score = 900_000, exScore = 230, misses = 2),
                testSongResult(trial.songs[1], score = 900_000, exScore = 240, misses = 3),
                testSongResult(trial.songs[2], score = 900_000, exScore = 230, misses = 5),
                testSongResult(trial.songs[3], score = 900_000, exScore = 240, misses = 0),
            ),
        )

        subject.assertProgress(current = 940, currentMax = 940, max = 1000)
        subject.assertStepCounts(misses = 10, judge = 10, vJudge = false)
        assertEquals(true, subject.hasStarted)
    }

    @Test
    fun `Test perfect partially filled session`() {
        val subject = testSession(
            results = arrayOf(
                testSongResult(trial.songs[0], score = 1_000_000, exScore = 240, misses = 0),
            ),
        )

        subject.assertProgress(current = 240, currentMax = 1000, max = 1000)
        subject.assertStepCounts(misses = 0, judge = 0, vJudge = false)
        assertEquals(true, subject.hasStarted)
    }

    @Test
    fun `Test perfect filled session`() {
        val subject = testSession(
            results = arrayOf(
                testSongResult(trial.songs[0], score = 1_000_000, exScore = 240, misses = 0),
                testSongResult(trial.songs[1], score = 1_000_000, exScore = 250, misses = 0),
                testSongResult(trial.songs[2], score = 1_000_000, exScore = 250, misses = 0),
                testSongResult(trial.songs[3], score = 1_000_000, exScore = 260, misses = 0),
            ),
        )

        subject.assertProgress(current = 1000, currentMax = 1000, max = 1000)
        subject.assertStepCounts(misses = 0, judge = 0, vJudge = false)
        assertEquals(true, subject.hasStarted)
    }

    @Test
    fun `Satisfied Conditions - EX Missing`() {
        var subject = createTestTrialSession(
            goal = TrialGoalSet(CONDITION_TEST_RANK, exMissing = 250),
        )
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))

        // Perfect 1st song
        subject.setTestSongResult(0, exScore = 1000)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))

        // Empty 2nd song
        subject.setTestSongResult(1)
        assertEquals(null, subject.isRankSatisfied(CONDITION_TEST_RANK))

        // Remedied 2nd song, directly on the line
        subject.setTestSongResult(1, exScore = 750)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))

        // 3rd song, over the line
        subject.setTestSongResult(2, exScore = 750)
        assertEquals(false, subject.isRankSatisfied(CONDITION_TEST_RANK))
    }

    @Test
    fun `Satisfied Conditions - Bad Judgments and Misses`() {
        var subject = createTestTrialSession(
            goal = TrialGoalSet(CONDITION_TEST_RANK, judge = 10, miss = 5),
        )
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))

        // Perfect 1st song
        subject.setTestSongResult(0, misses = 0, goods = 0, greats = 0)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))

        // Empty 2nd song
        subject.setTestSongResult(1)
        assertEquals(null, subject.isRankSatisfied(CONDITION_TEST_RANK))

        // Remedied 2nd song, directly on the line
        subject.setTestSongResult(1, misses = 5, goods = 2, greats = 2)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))

        // 3rd song, over the line with the various components
        subject.setTestSongResult(2, misses = 0, goods = 2, greats = 0)
        assertEquals(false, subject.isRankSatisfied(CONDITION_TEST_RANK))

        subject.setTestSongResult(2, misses = 0, goods = 0, greats = 2)
        assertEquals(false, subject.isRankSatisfied(CONDITION_TEST_RANK))

        subject.setTestSongResult(2, misses = 1, goods = 0, greats = 0)
        assertEquals(false, subject.isRankSatisfied(CONDITION_TEST_RANK))
    }

    @Test
    fun `Satisfied Conditions - Miss Each`() {
        var subject = createTestTrialSession(
            goal = TrialGoalSet(CONDITION_TEST_RANK, missEach = 5),
        )
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))

        // Perfect 1st song
        subject.setTestSongResult(0, misses = 0)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))

        // Empty 2nd song
        subject.setTestSongResult(1)
        assertEquals(null, subject.isRankSatisfied(CONDITION_TEST_RANK))

        // Remedied 2nd song, directly on the line
        subject.setTestSongResult(1, misses = 5)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))

        // 3rd song, also directly on the line
        subject.setTestSongResult(2, misses = 5)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))

        // 4th song, over the line
        subject.setTestSongResult(2, misses = 6)
        assertEquals(false, subject.isRankSatisfied(CONDITION_TEST_RANK))
    }

    @Test
    fun `Satisfied Conditions - Scores, top down`() {
        var subject = createTestTrialSession(
            goal = TrialGoalSet(CONDITION_TEST_RANK, score = listOf(900, 900, 850, 800)),
        )
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))

        subject.setTestSongResult(0, score = 900)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(1, score = 900)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(2, score = 850)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(3, score = 800)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
    }

    @Test
    fun `Satisfied Conditions - Scores, bottom up`() {
        var subject = createTestTrialSession(
            goal = TrialGoalSet(CONDITION_TEST_RANK, score = listOf(900, 900, 850, 800)),
        )
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))

        subject.setTestSongResult(0, score = 800)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(1, score = 850)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(2, score = 900)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(3, score = 900)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
    }

    @Test
    fun `Satisfied Conditions - Scores, scattered`() {
        var subject = createTestTrialSession(
            goal = TrialGoalSet(CONDITION_TEST_RANK, score = listOf(900, 900, 850, 800)),
        )
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))

        subject.setTestSongResult(0, score = 850)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(1, score = 900)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(2, score = 800)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(3, score = 900)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
    }

    @Test
    fun `Satisfied Conditions - Scores, missing`() {
        var subject = createTestTrialSession(
            goal = TrialGoalSet(CONDITION_TEST_RANK, score = listOf(900, 900, 850, 800)),
        )
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))

        subject.setTestSongResult(0)
        assertEquals(null, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(0, score = 900)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(1)
        assertEquals(null, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(1, score = 900)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(2)
        assertEquals(null, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(2, score = 900)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(3)
        assertEquals(null, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(3, score = 900)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
    }

    @Test
    fun `Satisfied Conditions - Scores, fail to fill`() {
        var subject = createTestTrialSession(
            goal = TrialGoalSet(CONDITION_TEST_RANK, score = listOf(900, 900, 850, 800)),
        )
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))

        subject.setTestSongResult(0, score = 850)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(1, score = 900)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(2, score = 800)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(3, score = 899)
        assertEquals(false, subject.isRankSatisfied(CONDITION_TEST_RANK))
    }

    @Test
    fun `Satisfied Conditions - Scores, fail to crack`() {
        var subject = createTestTrialSession(
            goal = TrialGoalSet(CONDITION_TEST_RANK, score = listOf(900, 900, 850, 800)),
        )
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))

        subject.setTestSongResult(0, score = 799)
        assertEquals(false, subject.isRankSatisfied(CONDITION_TEST_RANK))
    }

    @Test
    fun `Satisfied Conditions - Scores Indexed`() {
        var subject = createTestTrialSession(
            goal = TrialGoalSet(CONDITION_TEST_RANK, scoreIndexed = listOf(900, 900, 850, 800)),
        )
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))

        subject.setTestSongResult(0, score = 899)
        assertEquals(false, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(0, score = 900)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(1, score = 899)
        assertEquals(false, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(1, score = 900)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(2, score = 849)
        assertEquals(false, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(2, score = 850)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(3, score = 799)
        assertEquals(false, subject.isRankSatisfied(CONDITION_TEST_RANK))
        subject.setTestSongResult(3, score = 800)
        assertEquals(true, subject.isRankSatisfied(CONDITION_TEST_RANK))
    }

    //            "Score Idx" to scoresIndexedSatisfied(),
    //            "Clears" to clearsSatisfied(),
    //            "Clear Idx" to clearsIndexedSatisfied(),

    private fun createTestTrialSession(
        goal: TrialGoalSet,
        results: Array<SongResult?> = arrayOfNulls(4)
    ) : InProgressTrialSession {
        return InProgressTrialSession(
            trial = Trial(
                id = "dummy",
                name = "dummy",
                type = TrialType.TRIAL,
                songs = listOf(
                    TrialSong(difficultyClass = DifficultyClass.CHALLENGE, ex = 1000),
                    TrialSong(difficultyClass = DifficultyClass.CHALLENGE, ex = 1000),
                    TrialSong(difficultyClass = DifficultyClass.CHALLENGE, ex = 1000),
                    TrialSong(difficultyClass = DifficultyClass.CHALLENGE, ex = 1000),
                ),
                goals = listOf(goal),
                totalEx = 4000,
            ),
            results = results,
        )
    }

    private fun InProgressTrialSession.assertProgress(
        current: Int,
        currentMax: Int,
        max: Int
    ) {
        assertEquals(current, progress.currentExScore)
        assertEquals(currentMax, progress.currentMaxExScore)
        assertEquals(max, progress.maxExScore)
    }

    private fun InProgressTrialSession.assertStepCounts(
        misses: Int,
        judge: Int,
        vMisses: Boolean = true,
        vJudge: Boolean = true,
    ) {
        assertEquals(misses, currentMisses)
        assertEquals(judge, currentBadJudgments)
        assertEquals(if (vMisses) misses else null, currentValidatedMisses)
        assertEquals(if (vJudge) judge else null, currentValidatedBadJudgments)
    }

    companion object {
        val CONDITION_TEST_RANK = TrialRank.ONYX
    }
}