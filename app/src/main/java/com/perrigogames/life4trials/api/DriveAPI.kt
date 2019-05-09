package com.perrigogames.life4trials.api

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface DriveAPI {
    @GET("/files")
    fun getFiles(): Deferred<Response<Any>>
}