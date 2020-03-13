package com.perrigogames.life4trials.api

import android.content.Context
import android.widget.Toast
import com.perrigogames.life4.api.LocalDataReader
import com.perrigogames.life4.api.RemoteData
import com.perrigogames.life4.data.MajorVersioned
import com.perrigogames.life4trials.event.DataRequiresAppUpdateEvent
import com.perrigogames.life4trials.util.DataUtil
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * A wrapper around a data type that provides support for loading the data
 * externally using Retrofit.
 */
abstract class RetrofitRemoteData<T: Any>: RemoteData<T>, KoinComponent {

    protected val context: Context by inject()

    var fetchJob: Job? = null

    override fun fetch() {
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
abstract class LocalRemoteData<T: Any>(private val localReader: LocalDataReader): RetrofitRemoteData<T>() {

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
        data = createLocalDataFromText(localReader.loadRawString())
        onNewDataLoaded(data)
        localReader.loadCachedString()?.let { createLocalDataFromText(it) }?.let { onNewDataLoaded(it) }
        fetch()
    }

    open fun onNewDataLoaded(newData: T) {
        if (shouldDeleteCache(newData)) {
            localReader.deleteCachedString()
            onFetchUpdated(newData)
        } else if (shouldUpdate(newData)) {
            data = newData
            localReader.saveCachedString(createTextToData(data))
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
abstract class MajorVersionedRemoteData<T: MajorVersioned>(localReader: LocalDataReader, val majorVersion: Int):
    LocalRemoteData<T>(localReader) {

    protected val eventBus: EventBus by inject()

    /** Since the template is restricted to [MajorVersioned], use that version number. */
    override fun getDataVersion(data: T) = data.version

    override fun checkResponse(response: Response<T>) = super.checkResponse(response) && when {
        shouldUpdateApp(response.body()!!) -> {
            eventBus.postSticky(DataRequiresAppUpdateEvent())
            false
        }
        else -> true
    }

    override fun onNewDataLoaded(newData: T) {
        if (shouldUpdateApp(newData)) {
            eventBus.postSticky(DataRequiresAppUpdateEvent())
        } else {
            super.onNewDataLoaded(newData)
        }
    }

    fun shouldUpdateApp() = shouldUpdateApp(this.data)

    private fun shouldUpdateApp(data: T) = data.majorVersion > majorVersion
}
