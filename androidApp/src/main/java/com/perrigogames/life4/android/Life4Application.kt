package com.perrigogames.life4.android

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.perrigogames.life4.*
import com.perrigogames.life4.android.util.AndroidNotifications
import com.perrigogames.life4.android.util.setupNotifications
import com.perrigogames.life4.api.base.LocalDataReader
import com.perrigogames.life4.api.base.LocalUncachedDataReader
import com.perrigogames.life4.feature.placements.PlacementManager
import com.perrigogames.life4.feature.songlist.SongDataManager
import com.perrigogames.life4.feature.trials.TrialManager
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
            makeNativeModule(
                appInfo = AndroidAppInfo,
                platformStrings = AndroidPlatformStrings(),
                ignoresReader = AndroidDataReader(R.raw.ignore_lists, IGNORES_FILE_NAME),
                motdReader = AndroidDataReader(R.raw.motd, MOTD_FILE_NAME),
                partialDifficultyReader = AndroidDataReader(R.raw.partial_difficulties, PARTIAL_DIFFICULTY_FILE_NAME),
                placementsReader = AndroidUncachedDataReader(R.raw.placements),
                ranksReader = AndroidDataReader(R.raw.ranks, RANKS_FILE_NAME),
                songsReader = AndroidDataReader(R.raw.songs, SONGS_FILE_NAME),
                trialsReader = AndroidDataReader(R.raw.trials, TRIALS_FILE_NAME),
                notifications = AndroidNotifications(),
            ) {
                single<Context> { this@Life4Application }
//                single {
//                    { Log.i("Startup", "Hello from Android/Kotlin!") }
//                }
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
    override val appId: String = "LIFE4DDR" // FIXME
}
