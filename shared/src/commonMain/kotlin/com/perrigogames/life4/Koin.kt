package com.perrigogames.life4

import com.perrigogames.life4.db.TrialDatabaseHelper
import com.perrigogames.life4.ktor.GithubDataAPI
import com.perrigogames.life4.ktor.GithubDataImpl
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(platformModule, coreModule)
}

val coreModule = module {
    single { TrialDatabaseHelper(get()) }
    single<GithubDataAPI> { GithubDataImpl() }
}

expect val platformModule: Module
