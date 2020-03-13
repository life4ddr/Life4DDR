package com.perrigogames.life4trials

import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.perrigogames.life4.initKoin
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.PLACEMENTS_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.TRIALS_FILE_NAME
import com.perrigogames.life4.model.PlacementManager
import com.perrigogames.life4trials.api.AndroidUncachedDataReader
import com.perrigogames.life4trials.api.RetrofitGithubDataAPI
import com.perrigogames.life4trials.api.RetrofitLife4API
import com.perrigogames.life4trials.db.MyObjectBox
import com.perrigogames.life4trials.manager.*
import com.perrigogames.life4trials.repo.LadderResultRepo
import com.perrigogames.life4trials.repo.SongRepo
import com.perrigogames.life4trials.repo.TrialRepo
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.NotificationUtil
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import org.greenrobot.eventbus.EventBus
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


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
                settingsManager.onApplicationException()
            }

            defaultHandler!!.uncaughtException(thread, exception)
        }

        initKoin {
            modules(module {
                single<Context> { this@Life4Application }
                single { SettingsManager() }
                single<RetrofitLife4API> { life4Retrofit.create(RetrofitLife4API::class.java) }
                single<RetrofitGithubDataAPI> { githubRetrofit.create(RetrofitGithubDataAPI::class.java) }
                single {
                    MyObjectBox.builder()
                        .androidContext(this@Life4Application)
                        .build()
                        .also { startObjectboxBrowser(it) }
                }
                single { EventBus() }
                single(named(PLACEMENTS_FILE_NAME)) { AndroidUncachedDataReader(R.raw.placements) }
                single(named(TRIALS_FILE_NAME)) { AndroidUncachedDataReader(R.raw.placements) }
                single { SongRepo() }
                single { TrialRepo() }
                single { LadderResultRepo() }
                single { FirstRunManager() }
                single { IgnoreListManager() }
                single { SongDataManager() }
                single { TrialManager() }
                single { LadderManager() }
                single { PlayerManager() }
            })
        }

        NotificationUtil.setupNotifications(this)

        ManagerContainer().settingsManager.handleMajorUpdate(this)
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
        val settingsManager: SettingsManager by inject()
    }

    companion object {
        private val life4Retrofit = retrofit("http://life4bot.herokuapp.com/")
        private val githubTarget = if (BuildConfig.DEBUG) "remote-data-test" else "remote-data"
        private val githubRetrofit = retrofit("https://raw.githubusercontent.com/PerrigoGames/Life4DDR-Trials/$githubTarget/app/src/main/res/raw/")

        private fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
            .client(OkHttpClient().newBuilder().build())
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(DataUtil.gson))
            .client(OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply { level = BODY })
                .build())
            .build()
    }
}
