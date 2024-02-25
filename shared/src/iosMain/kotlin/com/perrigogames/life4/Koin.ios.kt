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

fun initKoinIos(
    userDefaults: NSUserDefaults,
    appInfo: AppInfo,
    doOnStartup: () -> Unit
): KoinApplication = initKoin(
    module {
        single<Settings> { NSUserDefaultsSettings(userDefaults) }
        single { appInfo }
        single { doOnStartup }
    }
)

fun initKoin () {
    startKoin {
        modules(coreModule)
    }
}

actual val platformModule = module {
    single<SqlDriver> { NativeSqliteDriver(Life4Db.Schema, "Life4Db") }
}
