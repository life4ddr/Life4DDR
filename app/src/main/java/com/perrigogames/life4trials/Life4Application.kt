package com.perrigogames.life4trials

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.perrigogames.life4trials.data.LadderRankData
import com.perrigogames.life4trials.db.MyObjectBox
import com.perrigogames.life4trials.manager.PlacementManager
import com.perrigogames.life4trials.manager.TrialManager
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.NotificationUtil
import com.perrigogames.life4trials.util.SharedPrefsUtils
import com.perrigogames.life4trials.util.loadRawString
import io.objectbox.BoxStore
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Life4Application: MultiDexApplication() {

    lateinit var ladderRankData: LadderRankData
    lateinit var placementManager: PlacementManager
    lateinit var trialManager: TrialManager

    override fun onCreate() {
        super.onCreate()
        SharedPrefsUtils.initializeDefaults(this)

        placementManager = PlacementManager(this)
        trialManager = TrialManager(this)
        ladderRankData = DataUtil.gson.fromJson(loadRawString(R.raw.ranks), LadderRankData::class.java)!!

        objectBox = MyObjectBox.builder()
            .androidContext(this)
            .build()

        NotificationUtil.setupNotifications(this)
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