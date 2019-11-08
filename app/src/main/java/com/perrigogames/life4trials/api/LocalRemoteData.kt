package com.perrigogames.life4trials.api

import android.content.Context
import android.widget.Toast
import androidx.annotation.RawRes
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.data.MajorVersioned
import com.perrigogames.life4trials.event.DataRequiresAppUpdateEvent
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.loadRawString
import com.perrigogames.life4trials.util.readFromFile
import com.perrigogames.life4trials.util.saveToFile
import kotlinx.coroutines.*
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * A wrapper around a data type that provides support for loading the data
 * externally using Retrofit.
 */
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
            } catch (e: UnknownHostException) {
                onFetchFailed()
                fetchJob = null
            } catch (e: SocketTimeoutException) {
                onFetchFailed()
                fetchJob = null
            }
        }
    }

    /**
     * Function invoked when the external data service should be fetched. This function
     * is always called from an internal coroutine.
     */
    abstract suspend fun getRemoteResponse(): Response<T>

    /**
     * Invoked when the remote data is received and the result of [checkResponse] is true.
     */
    open fun onFetchUpdated(data: T) {}

    /**
     * Invoked when the remote data is not received or if [checkResponse] indicated an error.
     */
    open fun onFetchFailed() {}

    /**
     * Checks whether the received response is acceptable. In its most basic form, it simply
     * checks whether the response was successful or not.
     */
    open fun checkResponse(response: Response<T>): Boolean = when {
        !response.isSuccessful -> {
            Toast.makeText(context, response.errorBody()!!.string(), Toast.LENGTH_SHORT).show()
            false
        }
        else -> true
    }
}

/**
 * A wrapper around a data type that is both fetched from an external source and stored locally.
 * It contains logic to determine the most up-to-date version of any data and use the most recent.
 */
abstract class LocalRemoteData<T: Any>(protected val context: Context,
                                       @RawRes private val rawResId: Int,
                                       private val cachedFileName: String): RemoteData<T>(context) {

    lateinit var data: T

    /** Provided a string, constructs and returns an appropriate [T]. */
    abstract fun createLocalDataFromText(text: String): T

    /** Provided a [T], returns an appropriate string representation for later use. */
    open fun createTextToData(data: T): String = DataUtil.gson.toJson(data)

    /** @return a version number for a given [T] so this structure can determine whether it
     * needs to be updated. */
    open fun getDataVersion(data: T = this.data): Int = Int.MIN_VALUE

    override fun checkResponse(response: Response<T>) = super.checkResponse(response) && shouldUpdate(response.body()!!)

    override fun onFetchUpdated(data: T) {
        super.onFetchUpdated(data)
        onNewDataLoaded(data)
    }

    private fun shouldUpdate(other: T) = getDataVersion(other) > getDataVersion(data)

    private fun shouldDeleteCache(cache: T) = getDataVersion(cache) < getDataVersion(data)

    fun start() {
        data = createLocalDataFromText(context.loadRawString(rawResId))
        onNewDataLoaded(data)
        context.readFromFile(cachedFileName)?.let { createLocalDataFromText(it) }?.let { onNewDataLoaded(it) }
        fetch()
    }

    open fun onNewDataLoaded(newData: T) {
        if (shouldDeleteCache(newData)) {
            context.deleteFile(cachedFileName)
            onFetchUpdated(newData)
        } else if (shouldUpdate(newData)) {
            data = newData
            context.saveToFile(cachedFileName, createTextToData(data))
        }
    }
}

/**
 * A wrapper around a data type that is both fetched from an external source and stored locally.
 * It contains logic to determine the most up-to-date version of any data and use the most recent.
 *
 * It also checks a major version of the data against the major version supported by the interpreter
 * to prevent unreadable data from crashing older versions of the app that won't parse properly.
 */
abstract class MajorVersionedRemoteData<T: MajorVersioned>(context: Context,
                                                           @RawRes rawResId: Int,
                                                           cachedFileName: String,
                                                           val majorVersion: Int):
    LocalRemoteData<T>(context, rawResId, cachedFileName) {

    /** Since the template is restricted to [MajorVersioned], use that version number. */
    override fun getDataVersion(data: T) = data.version

    override fun checkResponse(response: Response<T>) = super.checkResponse(response) && when {
        shouldUpdateApp(response.body()!!) -> {
            Life4Application.eventBus.postSticky(DataRequiresAppUpdateEvent())
            false
        }
        else -> true
    }

    override fun onNewDataLoaded(newData: T) {
        if (shouldUpdateApp(newData)) {
            Life4Application.eventBus.postSticky(DataRequiresAppUpdateEvent())
        } else {
            super.onNewDataLoaded(newData)
        }
    }

    fun shouldUpdateApp() = shouldUpdateApp(this.data)

    private fun shouldUpdateApp(data: T) = data.majorVersion > majorVersion
}