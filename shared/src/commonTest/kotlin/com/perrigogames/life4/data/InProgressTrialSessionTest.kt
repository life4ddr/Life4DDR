package com.perrigogames.life4.data

import com.perrigogames.life4.enums.TrialRank
import com.perrigogames.life4.util.testSongResult
import com.perrigogames.life4.util.testTrial
import kotlin.test.Test
import kotlin.test.assertEquals

class InProgressTrialSessionTest {
    private val trial = testTrial()

    private fun testSession(
        goalRank: TrialRank = TrialRank.GOLD,
        results: Array<SongResult?> = arrayOfNulls(4),
    ) = InProgressTrialSession(
        trial = trial,
        goalRank = goalRank,
        results = Array(4) { if (it < results.size) results[it] else null },
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
        val subject =
            testSession(
                results =
                    arrayOf(
                        testSongResult(trial.songs[0], score = 900_000, exScore = 230, misses = 2),
                    ),
            )

        subject.assertProgress(current = 230, currentMax = 990, max = 1000)
        subject.assertStepCounts(misses = 2, judge = 2, vJudge = false)
        assertEquals(true, subject.hasStarted)
    }

    @Test
    fun `Test imperfect filled session`() {
        val subject =
            testSession(
                results =
                    arrayOf(
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
        val subject =
            testSession(
                results =
                    arrayOf(
                        testSongResult(trial.songs[0], score = 1_000_000, exScore = 240, misses = 0),
                    ),
            )

        subject.assertProgress(current = 240, currentMax = 1000, max = 1000)
        subject.assertStepCounts(misses = 0, judge = 0, vJudge = false)
        assertEquals(true, subject.hasStarted)
    }

    @Test
    fun `Test perfect filled session`() {
        val subject =
            testSession(
                results =
                    arrayOf(
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

    private fun InProgressTrialSession.assertProgress(
        current: Int,
        currentMax: Int,
        max: Int,
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
}
