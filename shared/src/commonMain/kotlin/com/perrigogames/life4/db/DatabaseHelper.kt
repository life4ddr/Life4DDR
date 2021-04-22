package com.perrigogames.life4.db

import com.perrigogames.life4.Life4Db
import com.perrigogames.life4.data.StableIdColumnAdapter
import com.perrigogames.life4.data.TrialRank
import com.perrigogames.life4.enums.*
import com.perrigogames.life4.db.*
import com.squareup.sqldelight.db.SqlDriver

abstract class DatabaseHelper(private val sqlDriver: SqlDriver) {

    protected val dbRef = Life4Db(sqlDriver,
        ChartInfo.Adapter(
            StableIdColumnAdapter(DifficultyClass.values()),
            StableIdColumnAdapter(PlayStyle.values())
        ),
        ChartResult.Adapter(
            StableIdColumnAdapter(DifficultyClass.values()),
            StableIdColumnAdapter(PlayStyle.values()),
            StableIdColumnAdapter(ClearType.values())
        ),
        GoalState.Adapter(
            StableIdColumnAdapter(GoalStatus.values())
        ),
        SongInfo.Adapter(
            StableIdColumnAdapter(GameVersion.values())
        ),
        TrialSession.Adapter(
            StableIdColumnAdapter(TrialRank.values())
        )
    )

    internal fun dbClear() {
        sqlDriver.close()
    }
}