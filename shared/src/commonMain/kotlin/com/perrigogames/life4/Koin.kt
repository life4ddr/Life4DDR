package com.perrigogames.life4

import com.perrigogames.life4.db.*
import com.perrigogames.life4.ktor.GithubDataAPI
import com.perrigogames.life4.ktor.GithubDataImpl
import com.perrigogames.life4.ktor.Life4API
import com.perrigogames.life4.ktor.Life4APIImpl
import com.perrigogames.life4.model.FirstRunManager
import com.perrigogames.life4.model.MajorUpdateManager
import com.perrigogames.life4.model.PlacementManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(platformModule, coreModule)
}

val coreModule = module {
    single { GoalDatabaseHelper(get()) }
    single { ResultDatabaseHelper(get()) }
    single { SongDatabaseHelper(get()) }
    single { TrialDatabaseHelper(get()) }
    single<GithubDataAPI> { GithubDataImpl() }
    single<Life4API> { Life4APIImpl() }
    single { Json(JsonConfiguration.Stable.copy(classDiscriminator = "t")) }
    single { PlacementManager() }
    single { FirstRunManager() }
    single { MajorUpdateManager() }
}

expect val platformModule: Module
