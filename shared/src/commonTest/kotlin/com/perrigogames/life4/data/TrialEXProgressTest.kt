package com.perrigogames.life4.data

import kotlin.test.Test
import kotlin.test.assertEquals

class TrialEXProgressTest {

    @Test
    fun `Test empty TrialEXProgress`() {
        val subject = TrialEXProgress(
            currentExScore = 0,
            currentMaxExScore = 1000,
            maxExScore = 1000,
        )
        assertEquals(0F, subject.currentExPercent)
        assertEquals(1F, subject.currentMaxExPercent)
        assertEquals(1000, subject.remainingPotentialExScore)
        assertEquals(1000, subject.missingExScore)
        assertEquals(0, subject.lostExScore)
    }

    @Test
    fun `Test partially filled imperfect TrialEXProgress`() {
        val subject = TrialEXProgress(
            currentExScore = 230,
            currentMaxExScore = 980,
            maxExScore = 1000,
        )
        assertEquals(0.23F, subject.currentExPercent)
        assertEquals(0.98F, subject.currentMaxExPercent)
        assertEquals(750, subject.remainingPotentialExScore)
        assertEquals(770, subject.missingExScore)
        assertEquals(20, subject.lostExScore)
    }

    @Test
    fun `Test fully filled imperfect TrialEXProgress`() {
        val subject = TrialEXProgress(
            currentExScore = 760,
            currentMaxExScore = 760,
            maxExScore = 1000,
        )
        assertEquals(0.76F, subject.currentExPercent)
        assertEquals(0.76F, subject.currentMaxExPercent)
        assertEquals(0, subject.remainingPotentialExScore)
        assertEquals(240, subject.missingExScore)
        assertEquals(240, subject.lostExScore)
    }

    @Test
    fun `Test partially filled perfect TrialEXProgress`() {
        val subject = TrialEXProgress(
            currentExScore = 250,
            currentMaxExScore = 1000,
            maxExScore = 1000,
        )
        assertEquals(0.25F, subject.currentExPercent)
        assertEquals(1F, subject.currentMaxExPercent)
        assertEquals(750, subject.remainingPotentialExScore)
        assertEquals(750, subject.missingExScore)
        assertEquals(0, subject.lostExScore)
    }

    @Test
    fun `Test fully filled perfect TrialEXProgress`() {
        val subject = TrialEXProgress(
            currentExScore = 1000,
            currentMaxExScore = 1000,
            maxExScore = 1000,
        )
        assertEquals(1F, subject.currentExPercent)
        assertEquals(1F, subject.currentMaxExPercent)
        assertEquals(0, subject.remainingPotentialExScore)
        assertEquals(0, subject.missingExScore)
        assertEquals(0, subject.lostExScore)
    }
}