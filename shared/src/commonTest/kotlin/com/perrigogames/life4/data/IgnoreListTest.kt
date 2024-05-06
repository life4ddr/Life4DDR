package com.perrigogames.life4.data

import com.perrigogames.life4.data.IgnoreUnlockType.*
import kotlin.test.Test
import kotlin.test.assertEquals

class IgnoreListTest {
    @Test fun `Test ALL locked`() = ALL.testStore(expected = 0L, flagsString = "ffffff")

    @Test fun `Test ALL unlocked`() = ALL.testStore(expected = 1L, flagsString = "tttttt")

    @Test fun `Test SEQUENTIAL locked`() = SEQUENTIAL.testStore(expected = 0L, flagsString = "ffffff")

    @Test fun `Test SEQUENTIAL partially locked`() = SEQUENTIAL.testStore(expected = 3L, flagsString = "tttfff")

    @Test fun `Test SEQUENTIAL unlocked`() = SEQUENTIAL.testStore(expected = 6L, flagsString = "tttttt")

    @Test fun `Test SINGLE locked`() = SINGLE.testStore(expected = 0L, flagsString = "ffffff")

    @Test fun `Test SINGLE mostly locked`() = SINGLE.testStore(expected = 2L, flagsString = "ftffff")

    @Test fun `Test SINGLE sparsely locked`() = SINGLE.testStore(expected = 22L, flagsString = "fttftf")

    @Test fun `Test SINGLE unlocked`() = SINGLE.testStore(expected = 63L, flagsString = "tttttt")

    private fun String.toFlags() = toCharArray().map { it.equals('t', ignoreCase = true) }

    private fun IgnoreUnlockType.testStore(
        expected: Long,
        flagsString: String,
        listLength: Int = flagsString.length,
    ) {
        val flags = flagsString.toFlags()
        val stored = this.toStoredState(flags)
        assertEquals(expected, stored)
        assertEquals(flags, this.fromStoredState(stored, listLength))
    }
}
