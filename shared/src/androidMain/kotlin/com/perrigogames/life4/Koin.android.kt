package com.perrigogames.life4

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.koin.core.module.Module
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
actual val platformModule: Module = module {
    single<SqlDriver> { AndroidSqliteDriver(Life4Db.Schema, get(), "Life4Db") }
    single<Settings> { SharedPreferencesSettings.Factory(get()).create() }
    single<DataStore<Preferences>> { get<Context>().dataStore }
    single<FlowSettings> { DataStoreSettings(get()) }
}

val Context.dataStore by preferencesDataStore("preferences")
