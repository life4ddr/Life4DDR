package com.perrigogames.life4

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

@OptIn(ExperimentalSettingsApi::class)
actual val platformModule = module {
    single<SqlDriver> { NativeSqliteDriver(Life4Db.Schema, "Life4Db") }
}

fun makeIosExtraModule(
    defaults: NSUserDefaults
) = module {
    single<Settings> { NSUserDefaultsSettings(defaults) }
}