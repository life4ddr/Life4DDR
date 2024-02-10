package com.perrigogames.life4.data

import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.DifficultyClass.BASIC
import com.perrigogames.life4.enums.DifficultyClass.BEGINNER
import com.perrigogames.life4.enums.DifficultyClass.CHALLENGE
import com.perrigogames.life4.enums.DifficultyClass.DIFFICULT
import com.perrigogames.life4.enums.DifficultyClass.EXPERT
import com.perrigogames.life4.enums.PlayStyle
import kotlin.test.Test
import kotlin.test.assertEquals

class DifficultyClassPlayStyleTest {

    @Test fun `Test bSP aggregate`() = BEGINNER.testAggregate("bSP", PlayStyle.SINGLE)

    @Test fun `Test BSP aggregate`() = BASIC.testAggregate("BSP", PlayStyle.SINGLE)

    @Test fun `Test DSP aggregate`() = DIFFICULT.testAggregate("DSP", PlayStyle.SINGLE)

    @Test fun `Test ESP aggregate`() = EXPERT.testAggregate("ESP", PlayStyle.SINGLE)

    @Test fun `Test CSP aggregate`() = CHALLENGE.testAggregate("CSP", PlayStyle.SINGLE)

    @Test fun `Test BDP aggregate`() = BASIC.testAggregate("BDP", PlayStyle.DOUBLE)

    @Test fun `Test DDP aggregate`() = DIFFICULT.testAggregate("DDP", PlayStyle.DOUBLE)

    @Test fun `Test EDP aggregate`() = EXPERT.testAggregate("EDP", PlayStyle.DOUBLE)

    @Test fun `Test CDP aggregate`() = CHALLENGE.testAggregate("CDP", PlayStyle.DOUBLE)

    private fun DifficultyClass.testAggregate(expected: String, style: PlayStyle) =
        assertEquals(expected, this.aggregateString(style))
}
