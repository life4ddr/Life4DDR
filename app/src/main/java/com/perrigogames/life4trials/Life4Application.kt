package com.perrigogames.life4trials

import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
import androidx.preference.PreferenceManager
import com.perrigogames.life4.Notifications
import com.perrigogames.life4.PlatformStrings
import com.perrigogames.life4.api.LocalDataReader
import com.perrigogames.life4.api.LocalUncachedDataReader
import com.perrigogames.life4.initKoin
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.IGNORES_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.PLACEMENTS_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.RANKS_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.SONGS_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.TRIALS_FILE_NAME
import com.perrigogames.life4.model.FirstRunManager
import com.perrigogames.life4.model.EventBusNotifier
import com.perrigogames.life4.model.PlacementManager
import com.perrigogames.life4.model.SettingsManager
import com.perrigogames.life4trials.api.AndroidDataReader
import com.perrigogames.life4trials.api.AndroidUncachedDataReader
import com.perrigogames.life4trials.db.MyObjectBox
import com.perrigogames.life4trials.manager.*
import com.perrigogames.life4trials.repo.LadderResultRepo
import com.perrigogames.life4trials.repo.SongRepo
import com.perrigogames.life4trials.repo.TrialRepo
import com.perrigogames.life4trials.util.AndroidNotifications
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser
import org.greenrobot.eventbus.EventBus
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import org.koin.dsl.module


class Life4Application: MultiDexApplication() {

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
                single<LocalDataReader>(named(IGNORES_FILE_NAME)) { AndroidDataReader(R.raw.ignore_lists_v2, IGNORES_FILE_NAME) }
                single<LocalUncachedDataReader>(named(PLACEMENTS_FILE_NAME)) { AndroidUncachedDataReader(R.raw.placements) }
                single<LocalDataReader>(named(RANKS_FILE_NAME)) { AndroidDataReader(R.raw.ranks_v2, RANKS_FILE_NAME) }
                single<LocalDataReader>(named(SONGS_FILE_NAME)) { AndroidDataReader(R.raw.songs, SONGS_FILE_NAME) }
                single<LocalDataReader>(named(TRIALS_FILE_NAME)) { AndroidDataReader(R.raw.trials, TRIALS_FILE_NAME) }
                single { SettingsManager() }
                single {
                    MyObjectBox.builder()
                        .androidContext(this@Life4Application)
                        .build()
                        .also { startObjectboxBrowser(it) }
                }
                single { EventBus() }
                single<EventBusNotifier> { AndroidEventBusNotifier() }
                single { SongRepo() }
                single { TrialRepo() }
                single { LadderResultRepo() }
                single { FirstRunManager() }
                single { IgnoreListManager() }
                single { SongDataManager() }
                single { TrialManager() }
                single { LadderManager() }
                single { PlayerManager() }
                single<Notifications> { AndroidNotifications() }
            })
        }

        setupNotifications(this)
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)
        //FIXME major update
//        ManagerContainer().settingsManager.handleMajorUpdate(this)
//        if (BuildConfig.DEBUG) {
//            FirebaseUtil.getId(this)
//        }
    }

    private fun startObjectboxBrowser(objectBox: BoxStore) {
        if (BuildConfig.DEBUG) {
            val started = AndroidObjectBrowser(objectBox).start(this)
            Log.i("ObjectBrowser", "Started: $started")
        }
    }

    private inner class ManagerContainer: KoinComponent {
        val firstRunManager: FirstRunManager by inject()
        val ladderManager: LadderManager by inject()
        val placementManager: PlacementManager by inject()
        val songDataManager: SongDataManager by inject()
        val trialManager: TrialManager by inject()
    }
}
