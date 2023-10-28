package com.perrigogames.life4

import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import co.touchlab.kermit.platformLogWriter
import com.perrigogames.life4.db.GoalDatabaseHelper
import com.perrigogames.life4.db.ResultDatabaseHelper
import com.perrigogames.life4.db.SongDatabaseHelper
import com.perrigogames.life4.db.TrialDatabaseHelper
import com.perrigogames.life4.ktor.GithubDataAPI
import com.perrigogames.life4.ktor.GithubDataImpl
import com.perrigogames.life4.ktor.Life4API
import com.perrigogames.life4.ktor.Life4APIImpl
import com.perrigogames.life4.model.*
import com.perrigogames.life4.model.settings.FirstRunSettingsManager
import com.perrigogames.life4.model.settings.InfoSettingsManager
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

fun initKoin(appModule: Module) = startKoin {
    modules(appModule, platformModule, coreModule)
}.apply {
    // doOnStartup is a lambda which is implemented in Swift on iOS side
    koin.get<() -> Unit>().invoke()
    // AppInfo is a Kotlin interface with separate Android and iOS implementations
    koin.get<Logger> { parametersOf(null) }.also { kermit ->
        kermit.v { "App Id ${koin.get<AppInfo>().appId}" }
    }
    // Init song data and ladder progress
    koin.get<SongDataCoordinator>()
}

val coreModule = module {
    single { GoalDatabaseHelper(get()) }
    single { ResultDatabaseHelper(get()) }
    single { SongDatabaseHelper(get()) }
    single { TrialDatabaseHelper(get()) }
    single<GithubDataAPI> { GithubDataImpl() }
    single<Life4API> { Life4APIImpl(get()) }
    single { Json { classDiscriminator = "t" } }
    single { PlacementManager() }
    single { MajorUpdateManager() }
    single { MotdManager() }
    single { LadderDataManager() }
    single { LadderProgressManager() }
    single { TrialManager() }
    single { TrialSessionManager() }
    single { IgnoreListManager() }
    single { SongDataManager() }
    single { SongDataCoordinator() }
    single { PlayerManager() }
    single { InfoSettingsManager() }
    single { FirstRunSettingsManager() }
    single { UserRankManager() }

    // platformLogWriter() is a relatively simple config option, useful for local debugging. For production
    // uses you *may* want to have a more robust configuration from the native platform. In KaMP Kit,
    // that would likely go into platformModule expect/actual.
    // See https://github.com/touchlab/Kermit
    val baseLogger = Logger(config = StaticConfig(logWriterList = listOf(platformLogWriter())), "KampKit")
    factory { (tag: String?) -> if (tag != null) baseLogger.withTag(tag) else baseLogger }
}

// Simple function to clean up the syntax a bit
fun KoinComponent.injectLogger(tag: String): Lazy<Logger> = inject { parametersOf(tag) }

expect val platformModule: Module
