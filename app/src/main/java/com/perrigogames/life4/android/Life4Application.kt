package com.perrigogames.life4.android

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.perrigogames.life4.AppInfo
import com.perrigogames.life4.LadderDialogs
import com.perrigogames.life4.Notifications
import com.perrigogames.life4.PlatformStrings
import com.perrigogames.life4.TrialNavigation
import com.perrigogames.life4.android.manager.AndroidLadderDialogs
import com.perrigogames.life4.android.manager.AndroidTrialNavigation
import com.perrigogames.life4.android.util.AndroidNotifications
import com.perrigogames.life4.android.util.setupNotifications
import com.perrigogames.life4.api.base.LocalDataReader
import com.perrigogames.life4.api.base.LocalUncachedDataReader
import com.perrigogames.life4.feature.placements.PlacementManager
import com.perrigogames.life4.feature.songlist.SongDataManager
import com.perrigogames.life4.feature.trials.TrialManager
import com.perrigogames.life4.initKoin
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.IGNORES_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.MOTD_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.PARTIAL_DIFFICULTY_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.PLACEMENTS_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.RANKS_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.SONGS_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.TRIALS_FILE_NAME
import com.perrigogames.life4.model.LadderDataManager
import com.perrigogames.life4.model.MotdManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import org.koin.dsl.module


class Life4Application: Application() {

    override fun onCreate() {
        super.onCreate()
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            ManagerContainer().apply {
                ladderDataManager.onApplicationException()
                placementManager.onApplicationException()
                songDataManager.onApplicationException()
                trialManager.onApplicationException()
            }

            defaultHandler!!.uncaughtException(thread, exception)
        }

        initKoin(
            module {
                single<AppInfo> { AndroidAppInfo }
                single<Context> { this@Life4Application }
                single<PlatformStrings> { AndroidPlatformStrings() }
                single<LocalDataReader>(named(IGNORES_FILE_NAME)) { AndroidDataReader(R.raw.ignore_lists, IGNORES_FILE_NAME) }
                single<LocalDataReader>(named(MOTD_FILE_NAME)) { AndroidDataReader(R.raw.motd, MOTD_FILE_NAME) }
                single<LocalDataReader>(named(PARTIAL_DIFFICULTY_FILE_NAME)) { AndroidDataReader(R.raw.partial_difficulties, PARTIAL_DIFFICULTY_FILE_NAME) }
                single<LocalUncachedDataReader>(named(PLACEMENTS_FILE_NAME)) { AndroidUncachedDataReader(R.raw.placements) }
                single<LocalDataReader>(named(RANKS_FILE_NAME)) { AndroidDataReader(R.raw.ranks, RANKS_FILE_NAME) }
                single<LocalDataReader>(named(SONGS_FILE_NAME)) { AndroidDataReader(R.raw.songs, SONGS_FILE_NAME) }
                single<LocalDataReader>(named(TRIALS_FILE_NAME)) { AndroidDataReader(R.raw.trials, TRIALS_FILE_NAME) }
                single<Notifications> { AndroidNotifications() }

                val ladderDialogs = AndroidLadderDialogs()
                single { ladderDialogs }
                single<LadderDialogs> { ladderDialogs }
                val trialDialogs = AndroidTrialNavigation()
                single { trialDialogs }
                single<TrialNavigation> { trialDialogs }

                single {
                    { Log.i("Startup", "Hello from Android/Kotlin!") }
                }
            }
        )

        setupNotifications(this)
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)

        ManagerContainer() // instantiate the managers so that remote data gets pulled properly
    }

    private inner class ManagerContainer: KoinComponent {
        val ladderDataManager: LadderDataManager = get()
        val motdManager: MotdManager = get()
        val placementManager: PlacementManager = get()
        val songDataManager: SongDataManager = get()
        val trialManager: TrialManager = get()
    }
}

object AndroidAppInfo : AppInfo {
    override val appId: String = BuildConfig.APPLICATION_ID
}
