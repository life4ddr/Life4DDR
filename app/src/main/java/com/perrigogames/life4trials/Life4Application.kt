package com.perrigogames.life4trials

import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.perrigogames.life4trials.api.Life4API
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_INFO_NAME
import com.perrigogames.life4trials.api.FirebaseUtil
import com.perrigogames.life4trials.db.MyObjectBox
import com.perrigogames.life4trials.manager.*
import com.perrigogames.life4trials.util.NotificationUtil
import com.perrigogames.life4trials.util.SharedPrefsUtil
import com.perrigogames.life4trials.util.SharedPrefsUtil.KEY_APP_CRASHED
import com.perrigogames.life4trials.util.SharedPrefsUtil.KEY_INIT_STATE
import com.perrigogames.life4trials.util.SharedPrefsUtil.VAL_INIT_STATE_PLACEMENTS
import com.perrigogames.life4trials.util.SharedPrefsUtil.VAL_INIT_STATE_RANKS
import io.objectbox.BoxStore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import io.objectbox.android.AndroidObjectBrowser
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Life4Application: MultiDexApplication() {

    lateinit var firstRunManager: FirstRunManager
    lateinit var ladderManager: LadderManager
    lateinit var placementManager: PlacementManager
    lateinit var songDataManager: SongDataManager
    lateinit var tournamentManager: TournamentManager
    lateinit var trialManager: TrialManager
    lateinit var playerManager: PlayerManager
    lateinit var life4Api: Life4API

    override fun onCreate() {
        super.onCreate()

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            SharedPrefsUtil.setUserFlag(this@Life4Application, KEY_APP_CRASHED, true)
            defaultHandler!!.uncaughtException(thread, exception)
        }

        SharedPrefsUtil.initializeDefaults(this)

        life4Api = retrofit.create(Life4API::class.java)

        objectBox = MyObjectBox.builder()
            .androidContext(this)
            .build()
        if (BuildConfig.DEBUG) {
            val started = AndroidObjectBrowser(objectBox).start(this)
            Log.i("ObjectBrowser", "Started: $started")
        }

        firstRunManager = FirstRunManager(this)
        songDataManager = SongDataManager(this)
        placementManager = PlacementManager(this)
        trialManager = TrialManager(this)
        ladderManager = LadderManager(this, songDataManager)
        tournamentManager = TournamentManager()
        playerManager = PlayerManager(this)

        NotificationUtil.setupNotifications(this)

        if (BuildConfig.DEBUG) {
            FirebaseUtil.getId(this)
        }
    }

    val requireSignin: Boolean get() = SharedPrefsUtil.getUserString(this, KEY_INFO_NAME, null) == null
    val showPlacements: Boolean get() = SharedPrefsUtil.getUserString(this, KEY_INIT_STATE, null) == VAL_INIT_STATE_PLACEMENTS
    val showRankList: Boolean get() = SharedPrefsUtil.getUserString(this, KEY_INIT_STATE, null) == VAL_INIT_STATE_RANKS

    companion object {
        val eventBus = EventBus()

        lateinit var objectBox: BoxStore

        val retrofit: Retrofit = Retrofit.Builder()
            .client(OkHttpClient().newBuilder().build())
            .baseUrl("http://life4bot.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply { level = BODY })
                .build())
            .build()
    }
}

val Context.life4app get() = applicationContext as Life4Application