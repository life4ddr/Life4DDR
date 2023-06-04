package com.perrigogames.life4

import com.russhwolf.settings.SharedPreferencesSettings
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<SqlDriver> { AndroidSqliteDriver(Life4Db.Schema, get(), "Life4Db") }
    single { SharedPreferencesSettings.Factory(get()).create() }
}
