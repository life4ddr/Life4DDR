package com.perrigogames.life4trials

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.perrigogames.life4trials.db.MyObjectBox
import com.perrigogames.life4trials.manager.LadderManager
import com.perrigogames.life4trials.manager.PlacementManager
import com.perrigogames.life4trials.manager.TrialManager
import com.perrigogames.life4trials.util.NotificationUtil
import com.perrigogames.life4trials.util.SharedPrefsUtil
import com.perrigogames.life4trials.util.SharedPrefsUtil.KEY_APP_CRASHED
import io.objectbox.BoxStore
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Life4Application: MultiDexApplication() {

    lateinit var ladderManager: LadderManager
    lateinit var placementManager: PlacementManager
    lateinit var trialManager: TrialManager

    override fun onCreate() {
        super.onCreate()
        SharedPrefsUtil.initializeDefaults(this)

        placementManager = PlacementManager(this)
        trialManager = TrialManager(this)
        ladderManager = LadderManager(this)

        objectBox = MyObjectBox.builder()
            .androidContext(this)
            .build()

        NotificationUtil.setupNotifications(this)

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            SharedPrefsUtil.setUserFlag(this@Life4Application, KEY_APP_CRASHED, true)
            defaultHandler!!.uncaughtException(thread, exception)
        }
    }

    companion object {
        val eventBus = EventBus()

        lateinit var objectBox: BoxStore

        fun retrofit() : Retrofit = Retrofit.Builder()
            .client(OkHttpClient().newBuilder().build())
            .baseUrl("https://www.googleapis.com/drive/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }
}

val Context.life4app get() = applicationContext as Life4Application