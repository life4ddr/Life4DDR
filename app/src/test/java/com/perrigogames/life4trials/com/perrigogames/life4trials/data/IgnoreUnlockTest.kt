package com.perrigogames.life4trials.com.perrigogames.life4trials.data

import com.perrigogames.life4trials.data.IgnoreUnlockType
import org.junit.Test

class IgnoreUnlockTest {

    @Test
    fun testAllUnlock() {
        IgnoreUnlockType.ALL
    }

    @Test
    fun testSequenceUnlock() {
        IgnoreUnlockType.SEQUENTIAL
    }

    @Test
    fun testSingleUnlock() {
        IgnoreUnlockType.SINGLE
    }
}
