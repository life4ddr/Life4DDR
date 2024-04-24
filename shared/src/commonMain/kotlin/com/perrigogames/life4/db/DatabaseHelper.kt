package com.perrigogames.life4.db

import com.perrigogames.life4.Life4Db
import com.perrigogames.life4.data.StableIdColumnAdapter
import com.perrigogames.life4.enums.*
import com.squareup.sqldelight.db.SqlDriver

abstract class DatabaseHelper(private val sqlDriver: SqlDriver) {

    protected val dbRef = Life4Db(sqlDriver,
        ChartInfo.Adapter(
            StableIdColumnAdapter(DifficultyClass.entries.toTypedArray()),
            StableIdColumnAdapter(PlayStyle.entries.toTypedArray())
        ),
        ChartResult.Adapter(
            StableIdColumnAdapter(ClearType.entries.toTypedArray())
        ),
        GoalState.Adapter(
            StableIdColumnAdapter(GoalStatus.entries.toTypedArray())
        ),
        SongInfo.Adapter(
            StableIdColumnAdapter(GameVersion.entries.toTypedArray())
        ),
        TrialSession.Adapter(
            StableIdColumnAdapter(TrialRank.entries.toTypedArray())
        )
    )

    internal fun dbClear() {
        sqlDriver.close()
    }
}