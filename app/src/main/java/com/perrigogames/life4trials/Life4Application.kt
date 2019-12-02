package com.perrigogames.life4trials

import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.perrigogames.life4trials.api.GithubDataAPI
import com.perrigogames.life4trials.api.Life4API
import com.perrigogames.life4trials.db.MyObjectBox
import com.perrigogames.life4trials.manager.*
import com.perrigogames.life4trials.repo.TrialRepo
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.NotificationUtil
import com.perrigogames.life4trials.util.SharedPrefsUtil
import com.perrigogames.life4trials.util.SharedPrefsUtil.KEY_APP_CRASHED
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class Life4Application: MultiDexApplication() {

    lateinit var trialRepo: TrialRepo
    lateinit var firstRunManager: FirstRunManager
    lateinit var ladderManager: LadderManager
    lateinit var placementManager: PlacementManager
    lateinit var songDataManager: SongDataManager
    lateinit var tournamentManager: TournamentManager
    lateinit var trialManager: TrialManager
    lateinit var playerManager: PlayerManager
    lateinit var life4Api: Life4API
    lateinit var githubDataApi: GithubDataAPI

    override fun onCreate() {
        super.onCreate()

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            trialRepo.onApplicationException()
            firstRunManager.onApplicationException()
            ladderManager.onApplicationException()
            placementManager.onApplicationException()
            songDataManager.onApplicationException()
            tournamentManager.onApplicationException()
            trialManager.onApplicationException()

            SharedPrefsUtil.setUserFlag(this@Life4Application, KEY_APP_CRASHED, true)
            defaultHandler!!.uncaughtException(thread, exception)
        }

        SharedPrefsUtil.initializeDefaults(this)

        life4Api = life4Retrofit.create(Life4API::class.java)
        githubDataApi = githubRetrofit.create(GithubDataAPI::class.java)

        objectBox = MyObjectBox.builder()
            .androidContext(this)
            .build()
        if (BuildConfig.DEBUG) {
            val started = AndroidObjectBrowser(objectBox).start(this)
            Log.i("ObjectBrowser", "Started: $started")
        }

        trialRepo = TrialRepo()
        firstRunManager = FirstRunManager(this)
        songDataManager = SongDataManager(this, githubDataApi)
        placementManager = PlacementManager(this)
        trialManager = TrialManager(this, trialRepo, githubDataApi)
        ladderManager = LadderManager(this, songDataManager, trialManager, githubDataApi)
        tournamentManager = TournamentManager()
        playerManager = PlayerManager(this)

        NotificationUtil.setupNotifications(this)
        SharedPrefsUtil.handleMajorUpdate(this)

//        if (BuildConfig.DEBUG) {
//            FirebaseUtil.getId(this)
//        }
    }

    companion object {
        val eventBus = EventBus()

        lateinit var objectBox: BoxStore

        val life4Retrofit = retrofit("http://life4bot.herokuapp.com/")
        private val githubTarget = if (BuildConfig.DEBUG) "remote-data-test" else "remote-data"
        val githubRetrofit = retrofit("https://raw.githubusercontent.com/PerrigoGames/Life4DDR-Trials/$githubTarget/app/src/main/res/raw/")

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

val Context.life4app get() = applicationContext as Life4Application