package com.perrigogames.life4.data

import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.DifficultyClass.BASIC
import com.perrigogames.life4.enums.DifficultyClass.BEGINNER
import com.perrigogames.life4.enums.DifficultyClass.CHALLENGE
import com.perrigogames.life4.enums.DifficultyClass.DIFFICULT
import com.perrigogames.life4.enums.DifficultyClass.EXPERT
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class DifficultyClassSetTest {
    @Test fun `Test BEGINNER set`() =
        DifficultyClassSet(BEGINNER, requireAll = false)
            .testToString("b")

    @Test fun `Test BASIC set`() =
        DifficultyClassSet(BASIC, requireAll = false)
            .testToString("B")

    @Test fun `Test DIFFICULT set`() =
        DifficultyClassSet(DIFFICULT, requireAll = false)
            .testToString("D")

    @Test fun `Test EXPERT set`() =
        DifficultyClassSet(EXPERT, requireAll = false)
            .testToString("E")

    @Test fun `Test CHALLENGE set`() =
        DifficultyClassSet(CHALLENGE, requireAll = false)
            .testToString("C")

    @Test fun `Test BEGINNER parse`() = "b".testParse(BEGINNER, requireAll = false)

    @Test fun `Test BASIC parse`() = "B".testParse(BASIC, requireAll = false)

    @Test fun `Test DIFFICULT parse`() = "D".testParse(DIFFICULT, requireAll = false)

    @Test fun `Test EXPERT parse`() = "E".testParse(EXPERT, requireAll = false)

    @Test fun `Test CHALLENGE parse`() = "C".testParse(CHALLENGE, requireAll = false)

    @Test fun `Test 2 diff set`() =
        DifficultyClassSet(
            listOf(BEGINNER, BASIC),
            requireAll = false,
        ).testToString("bB")

    @Test fun `Test 3 diff set`() =
        DifficultyClassSet(
            listOf(BEGINNER, BASIC, DIFFICULT),
            requireAll = false,
        ).testToString("bBD")

    @Test fun `Test 4 diff set`() =
        DifficultyClassSet(
            listOf(BEGINNER, BASIC, DIFFICULT, EXPERT),
            requireAll = false,
        ).testToString("bBDE")

    @Test fun `Test 5 diff set`() =
        DifficultyClassSet(
            listOf(BEGINNER, BASIC, DIFFICULT, EXPERT, CHALLENGE),
            requireAll = false,
        ).testToString("bBDEC")

    @Test fun `Test required set`() =
        DifficultyClassSet(BEGINNER, requireAll = true)
            .testToString("b*")

    @Test fun `Test 2 diff required set`() =
        DifficultyClassSet(
            listOf(BEGINNER, BASIC),
            requireAll = true,
        ).testToString("bB*")

    @Test fun `Test 3 diff required set`() =
        DifficultyClassSet(
            listOf(BEGINNER, BASIC, DIFFICULT),
            requireAll = true,
        ).testToString("bBD*")

    @Test fun `Test 4 diff required set`() =
        DifficultyClassSet(
            listOf(BEGINNER, BASIC, DIFFICULT, EXPERT),
            requireAll = true,
        ).testToString("bBDE*")

    @Test fun `Test 5 diff required set`() =
        DifficultyClassSet(
            listOf(BEGINNER, BASIC, DIFFICULT, EXPERT, CHALLENGE),
            requireAll = true,
        ).testToString("bBDEC*")

    @Test fun `Test 2 diff parse`() = "bB".testParse(listOf(BEGINNER, BASIC), requireAll = false)

    @Test fun `Test 3 diff parse`() =
        "bBD".testParse(
            listOf(BEGINNER, BASIC, DIFFICULT),
            requireAll = false,
        )

    @Test fun `Test 4 diff parse`() =
        "bBDE".testParse(
            listOf(BEGINNER, BASIC, DIFFICULT, EXPERT),
            requireAll = false,
        )

    @Test fun `Test 5 diff parse`() =
        "bBDEC".testParse(
            listOf(BEGINNER, BASIC, DIFFICULT, EXPERT, CHALLENGE),
            requireAll = false,
        )

    @Test fun `Test required parse`() = "b*".testParse(BEGINNER, requireAll = true)

    @Test fun `Test 2 diff required parse`() =
        "bB*".testParse(
            listOf(BEGINNER, BASIC),
            requireAll = true,
        )

    @Test fun `Test 3 diff required parse`() =
        "bBD*".testParse(
            listOf(BEGINNER, BASIC, DIFFICULT),
            requireAll = true,
        )

    @Test fun `Test 4 diff required parse`() =
        "bBDE*".testParse(
            listOf(BEGINNER, BASIC, DIFFICULT, EXPERT),
            requireAll = true,
        )

    @Test fun `Test 5 diff required parse`() =
        "bBDEC*".testParse(
            listOf(BEGINNER, BASIC, DIFFICULT, EXPERT, CHALLENGE),
            requireAll = true,
        )

    private fun DifficultyClassSet.testToString(expected: String) {
        assertEquals(expected, this.toString())
    }

    private fun String.testParse(
        diff: DifficultyClass,
        requireAll: Boolean,
    ) {
        val test = DifficultyClassSet.parse(this)
        assertContains(test.set, diff)
        assertEquals(requireAll, test.requireAll)
    }

    private fun String.testParse(
        diffs: List<DifficultyClass>,
        requireAll: Boolean,
    ) {
        val test = DifficultyClassSet.parse(this)
        assertEquals(test.set, diffs)
        assertEquals(requireAll, test.requireAll)
    }
}
