package com.perrigogames.life4.db

import com.perrigogames.life4.db.GoalState
import com.perrigogames.life4.enums.GoalStatus
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class GoalDatabaseHelper(sqlDriver: SqlDriver): DatabaseHelper(sqlDriver) {

    fun allStates(): Query<GoalState> = dbRef.goalStatusQueries.getAll()

    fun stateForId(id: Long): GoalState? =
        dbRef.goalStatusQueries.getStatus(id).executeAsList().firstOrNull()

    fun statesForIdList(ids: List<Long>): Query<GoalState> = dbRef.goalStatusQueries.getStatusList(ids)

    fun insertGoalState(goalId: Long, status: GoalStatus) {
        dbRef.goalStatusQueries.setStatus(goalId, status, Clock.System.now().toString())
    }

    suspend fun deleteAll() = withContext(Dispatchers.Default) {
        dbRef.goalStatusQueries.deleteAll()
    }
}
