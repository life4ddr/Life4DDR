package com.perrigogames.life4

import com.perrigogames.life4.db.GoalDatabaseHelper
import com.perrigogames.life4.enums.GoalStatus.*
import kotlin.test.*

abstract class GoalSqlTest {

    private lateinit var dbHelper: GoalDatabaseHelper

    @BeforeTest
    fun setup() = runTest{
        dbHelper = GoalDatabaseHelper(testDbConnection())
        dbHelper.deleteAll()
        dbHelper.insertGoalState(1, INCOMPLETE)
        dbHelper.insertGoalState(2, INCOMPLETE)
        dbHelper.insertGoalState(3, COMPLETE)
        dbHelper.insertGoalState(4, IGNORED)
    }

    @Test
    fun `Correct Default Value`() = runTest {
        val status = dbHelper.statusForId(99)
        assertEquals(INCOMPLETE, status, "Should return INCOMPLETE when not explicitly defined")
    }

    @Test
    fun `Select All`() = runTest {
        val goals = dbHelper.allStates().executeAsList()
        assertTrue(goals.isNotEmpty())
    }

    @Test
    fun `Select State By Id`() = runTest {
        val goals = dbHelper.allStates().executeAsList()
        val firstGoal = goals.first()
        assertNotNull(dbHelper.statusForId(firstGoal.goalId), "Could not retrieve Goal by ID")
    }

    @Test
    fun `Overwrite Goal`() = runTest {
        val goals = dbHelper.allStates().executeAsList()
        val firstGoal = goals.first()
        assertNotEquals(IGNORED, dbHelper.statusForId(firstGoal.goalId),
            "Won't be able to tell if an update takes place")
        dbHelper.insertGoalState(firstGoal.goalId, IGNORED)
        assertEquals(IGNORED, dbHelper.statusForId(firstGoal.goalId),
            "Status was not properly updated")
    }

    @Test
    fun `Update Goal`() = runTest {
        val goals = dbHelper.allStates().executeAsList()
        val firstGoal = goals.first()
        assertNotEquals(IGNORED, dbHelper.statusForId(firstGoal.goalId),
            "Won't be able to tell if an update takes place")
        dbHelper.updateGoalState(firstGoal.goalId, IGNORED)
        assertEquals(IGNORED, dbHelper.statusForId(firstGoal.goalId),
            "Status was not properly updated")
    }

    @Test
    fun `Delete All`() = runTest {
        dbHelper.insertGoalState(5, INCOMPLETE)
        dbHelper.insertGoalState(6, COMPLETE)
        assertTrue(dbHelper.allStates().executeAsList().isNotEmpty())
        dbHelper.deleteAll()
        assertTrue(dbHelper.allStates().executeAsList().count() == 0)
    }
}