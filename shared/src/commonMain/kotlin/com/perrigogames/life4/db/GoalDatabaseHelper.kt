package com.perrigogames.life4.db

import com.perrigogames.life4.Life4Db
import com.perrigogames.life4.data.StableIdColumnAdapter
import com.perrigogames.life4.enums.GoalStatus
import com.soywiz.klock.DateTime
import com.soywiz.klock.ISO8601
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoalDatabaseHelper(private val sqlDriver: SqlDriver) {
    //FIXME don't copy this block everywhere
    private val dbRef = Life4Db(sqlDriver, GoalState.Adapter(StableIdColumnAdapter(GoalStatus.values())))

    internal fun dbClear() {
        sqlDriver.close()
    }

    fun allStates(): Query<GoalState> = dbRef.goalStatusQueries.getAll()

    fun statusForId(id: Long): GoalStatus? =
        dbRef.goalStatusQueries.getStatus(id).executeAsList().firstOrNull()?.status

    fun statesForIdList(ids: List<Long>): Query<GoalState> = dbRef.goalStatusQueries.getStatusList(ids)

    suspend fun insertGoalState(goalId: Long, status: GoalStatus) = withContext(Dispatchers.Default) {
        dbRef.goalStatusQueries.setStatus(goalId, status, DateTime.now().format(ISO8601.DATETIME_COMPLETE))
    }

    suspend fun updateGoalState(goalId: Long, status: GoalStatus) = withContext(Dispatchers.Default) {
        dbRef.goalStatusQueries.updateStatus(status, goalId)
    }

    suspend fun deleteAll() = withContext(Dispatchers.Default) {
        dbRef.goalStatusQueries.deleteAll()
    }
}
