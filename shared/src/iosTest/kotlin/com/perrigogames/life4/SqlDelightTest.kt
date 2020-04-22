package com.perrigogames.life4

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.ios.NativeSqliteDriver

actual fun testDbConnection(): SqlDriver = NativeSqliteDriver(Life4Db.Schema, "life4test")