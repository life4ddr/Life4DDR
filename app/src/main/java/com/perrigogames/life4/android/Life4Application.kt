package com.perrigogames.life4.android

import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager
import com.perrigogames.life4.*
import com.perrigogames.life4.api.LocalDataReader
import com.perrigogames.life4.api.LocalUncachedDataReader
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.IGNORES_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.PLACEMENTS_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.RANKS_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.SONGS_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.TRIALS_FILE_NAME
import com.perrigogames.life4.model.*
import com.perrigogames.life4.android.manager.AndroidLadderDialogs
import com.perrigogames.life4.android.manager.AndroidTrialNavigation
import com.perrigogames.life4.android.util.AndroidNotifications
import com.perrigogames.life4.android.util.setupNotifications
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.MOTD_FILE_NAME
import org.greenrobot.eventbus.EventBus
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.qualifier.named
import org.koin.dsl.module


class Life4Application: Application() {

    override fun onCreate() {
        super.onCreate()
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            ManagerContainer().apply {
                firstRunManager.onApplicationException()
                ladderManager.onApplicationException()
                placementManager.onApplicationException()
                songDataManager.onApplicationException()
                trialManager.onApplicationException()
            }

            defaultHandler!!.uncaughtException(thread, exception)
        }

        initKoin {
            modules(module {
                single<Context> { this@Life4Application }
                single<PlatformStrings> { AndroidPlatformStrings() }
                single<LocalDataReader>(named(IGNORES_FILE_NAME)) { AndroidDataReader(R.raw.ignore_lists, IGNORES_FILE_NAME) }
                single<LocalDataReader>(named(MOTD_FILE_NAME)) { AndroidDataReader(R.raw.motd, MOTD_FILE_NAME) }
                single<LocalUncachedDataReader>(named(PLACEMENTS_FILE_NAME)) { AndroidUncachedDataReader(R.raw.placements) }
                single<LocalDataReader>(named(RANKS_FILE_NAME)) { AndroidDataReader(R.raw.ranks, RANKS_FILE_NAME) }
                single<LocalDataReader>(named(SONGS_FILE_NAME)) { AndroidDataReader(R.raw.songs, SONGS_FILE_NAME) }
                single<LocalDataReader>(named(TRIALS_FILE_NAME)) { AndroidDataReader(R.raw.trials, TRIALS_FILE_NAME) }
                single { EventBus() }
                single<EventBusNotifier> { AndroidEventBusNotifier() }
                single<Notifications> { AndroidNotifications() }

                val ladderDialogs = AndroidLadderDialogs()
                single { ladderDialogs }
                single<LadderDialogs> { ladderDialogs }
                val trialDialogs = AndroidTrialNavigation()
                single { trialDialogs }
                single<TrialNavigation> { trialDialogs }
            })
        }

        setupNotifications(this)
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)

        ManagerContainer() // instantiate the managers so that remote data gets pulled properly
    }

    private inner class ManagerContainer: KoinComponent {
        val firstRunManager: FirstRunManager = get()
        val ladderManager: LadderManager = get()
        val motdManager: MotdManager = get()
        val placementManager: PlacementManager = get()
        val songDataManager: SongDataManager = get()
        val trialManager: TrialManager = get()
    }
}
