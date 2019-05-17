package com.perrigogames.life4trials

import androidx.multidex.MultiDexApplication
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.perrigogames.life4trials.data.TrialData
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.NotificationUtil
import com.perrigogames.life4trials.util.loadRawString
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Life4Application: MultiDexApplication() {

    lateinit var trialData: TrialData

    override fun onCreate() {
        super.onCreate()

        trialData = DataUtil.gson.fromJson(loadRawString(R.raw.trials), TrialData::class.java)!!
        if (BuildConfig.DEBUG) {
            val debugData: TrialData = DataUtil.gson.fromJson(loadRawString(R.raw.trials_debug), TrialData::class.java)!!
            trialData = TrialData(trialData.trials + debugData.trials)
        }

        NotificationUtil.setupNotifications(this)
    }

    companion object {
        val eventBus = EventBus()

        fun retrofit() : Retrofit = Retrofit.Builder()
            .client(OkHttpClient().newBuilder().build())
            .baseUrl("https://www.googleapis.com/drive/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }
}