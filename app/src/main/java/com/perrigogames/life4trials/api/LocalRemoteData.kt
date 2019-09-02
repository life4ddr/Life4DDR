package com.perrigogames.life4trials.api

import android.content.Context
import android.widget.Toast
import androidx.annotation.RawRes
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.loadRawString
import com.perrigogames.life4trials.util.readFromFile
import com.perrigogames.life4trials.util.saveToFile
import kotlinx.coroutines.*
import retrofit2.Response
import java.net.UnknownHostException

abstract class RemoteData<T: Any>(private val context: Context) {

    var fetchJob: Job? = null

    fun fetch() {
        fetchJob?.cancel()
        fetchJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = getRemoteResponse()
                withContext(Dispatchers.Main) {
                    if (checkResponse(response)) {
                        onFetchUpdated(response.body()!!)
                    } else {
                        onFetchFailed()
                    }
                    fetchJob = null
                }
            } catch (e: UnknownHostException) {}
        }
    }

    abstract suspend fun getRemoteResponse(): Response<T>

    open fun onFetchUpdated(data: T) {}

    open fun onFetchFailed() {}

    open fun checkResponse(response: Response<T>): Boolean = when {
        !response.isSuccessful -> {
            Toast.makeText(context, response.errorBody()!!.string(), Toast.LENGTH_SHORT).show()
            false
        }
        else -> true
    }
}

abstract class LocalRemoteData<T: Any>(private val context: Context,
                                       @RawRes private val rawResId: Int,
                                       private val cachedFileName: String): RemoteData<T>(context) {

    lateinit var data: T

    abstract fun createLocalDataFromText(text: String): T

    open fun createTextToData(data: T): String = DataUtil.gson.toJson(data)

    open fun getDataVersion(data: T): Int = Int.MIN_VALUE

    override fun checkResponse(response: Response<T>) = super.checkResponse(response) && shouldUpdate(response.body()!!)

    override fun onFetchUpdated(data: T) {
        super.onFetchUpdated(data)
        this.data = data
        context.saveToFile(cachedFileName, createTextToData(data))
    }

    private fun shouldUpdate(other: T) = getDataVersion(other) > getDataVersion(data)

    private fun shouldDeleteCache(cache: T) = getDataVersion(cache) < getDataVersion(data)

    fun start() {
        data = createLocalDataFromText(context.loadRawString(rawResId))
        context.readFromFile(cachedFileName)?.let { createLocalDataFromText(it) }?.let { cached ->
            if (shouldDeleteCache(cached)) {
                context.deleteFile(cachedFileName)
                onFetchUpdated(data)
            } else if (shouldUpdate(cached)) {
                data = cached
            }
        }
        fetch()
    }
}