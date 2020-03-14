package com.perrigogames.life4

import com.russhwolf.settings.AppleSettings
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.ios.NativeSqliteDriver
import org.koin.dsl.module

fun initKoin() = initKoin{}

actual val platformModule = module {
    single<SqlDriver> { NativeSqliteDriver(Life4Db.Schema, "life4db") }
    single { AppleSettings.Factory().create() }
}