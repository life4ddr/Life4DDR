package com.perrigogames.life4

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import org.koin.core.KoinApplication
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

import com.perrigogames.life4.coreModule
import org.koin.core.context.startKoin

actual val platformModule = module {
    single<SqlDriver> { NativeSqliteDriver(Life4Db.Schema, "Life4Db") }
    single<Settings> { NSUserDefaultsSettings(get()) }
}
