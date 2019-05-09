package com.perrigogames.life4trials

import android.app.Application
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.perrigogames.life4trials.api.DriveAPI
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class Life4Application: Application() {

    companion object {
        val eventBus = EventBus()

        fun retrofit() : Retrofit = Retrofit.Builder()
            .client(OkHttpClient().newBuilder().build())
            .baseUrl("https://www.googleapis.com/drive/v2/")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        val driveAPI : DriveAPI = retrofit().create(DriveAPI::class.java)
    }
}