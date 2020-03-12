package com.perrigogames.life4.db

import com.perrigogames.life4.Life4Db
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.db.SqlDriver

class TrialDatabaseHelper(private val sqlDriver: SqlDriver) {
    private val dbRef = Life4Db(sqlDriver)

    internal fun dbClear() {
        sqlDriver.close()
    }

    fun selectAllSessions(): Query<TrialSession> = dbRef.trialQueries.selectAll()
}
