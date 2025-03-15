package com.perrigogames.life4.db

import com.perrigogames.life4.Life4Db
import com.perrigogames.life4.data.StableIdColumnAdapter
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.GoalStatus
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.feature.trials.enums.TrialRank
import com.squareup.sqldelight.db.SqlDriver

abstract class DatabaseHelper(private val sqlDriver: SqlDriver) {

    protected val dbRef = Life4Db(sqlDriver,
        ChartResult.Adapter(
            StableIdColumnAdapter(DifficultyClass.entries.toTypedArray()),
            StableIdColumnAdapter(PlayStyle.entries.toTypedArray()),
            StableIdColumnAdapter(ClearType.entries.toTypedArray())
        ),
        GoalState.Adapter(
            StableIdColumnAdapter(GoalStatus.entries.toTypedArray())
        ),
        TrialSession.Adapter(
            StableIdColumnAdapter(TrialRank.entries.toTypedArray())
        )
    )

    internal fun dbClear() {
        sqlDriver.close()
    }
}