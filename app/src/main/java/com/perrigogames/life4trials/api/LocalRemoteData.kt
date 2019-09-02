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

    open fun getDataVersion(data: T): Int? = null

    override fun checkResponse(response: Response<T>) = super.checkResponse(response) && shouldUpdate(response.body()!!)

    override fun onFetchUpdated(data: T) {
        super.onFetchUpdated(data)
        this.data = data
        context.saveToFile(cachedFileName, DataUtil.gson.toJson(data))
    }

    private fun shouldUpdate(other: T) = getDataVersion(data) == null || getDataVersion(other)!! > getDataVersion(data)!!

    fun start() {
        val dataString = context.readFromFile(cachedFileName) ?: context.loadRawString(rawResId)
        data = createLocalDataFromText(dataString)
        fetch()
    }
}