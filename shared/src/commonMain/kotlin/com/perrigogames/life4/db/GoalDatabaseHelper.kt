package com.perrigogames.life4.db

import com.perrigogames.life4.enums.GoalStatus
import com.soywiz.klock.DateTime
import com.soywiz.klock.ISO8601
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoalDatabaseHelper(sqlDriver: SqlDriver): DatabaseHelper(sqlDriver) {

    fun allStates(): Query<GoalState> = dbRef.goalStatusQueries.getAll()

    fun stateForId(id: Long): GoalState? =
        dbRef.goalStatusQueries.getStatus(id).executeAsList().firstOrNull()

    fun statesForIdList(ids: List<Long>): Query<GoalState> = dbRef.goalStatusQueries.getStatusList(ids)

    suspend fun insertGoalState(goalId: Long, status: GoalStatus) = withContext(Dispatchers.Default) {
        dbRef.goalStatusQueries.setStatus(goalId, status, DateTime.now().format(ISO8601.DATETIME_COMPLETE))
    }

    suspend fun updateGoalState(goalId: Long, status: GoalStatus) = withContext(Dispatchers.Default) {
        dbRef.goalStatusQueries.updateStatus(status, DateTime.now().format(ISO8601.DATETIME_COMPLETE), goalId)
    }

    suspend fun deleteAll() = withContext(Dispatchers.Default) {
        dbRef.goalStatusQueries.deleteAll()
    }
}
