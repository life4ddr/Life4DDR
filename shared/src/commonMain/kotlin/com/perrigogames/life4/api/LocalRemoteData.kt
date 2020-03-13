package com.perrigogames.life4.api

import com.perrigogames.life4.data.MajorVersioned
import com.perrigogames.life4.model.BaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Interface for retrieving data from the application's resources.
 */
interface LocalUncachedDataReader {

    /**
     * Loads a raw version of the data from the system's resources.
     * This is not optional and serves as the default data set.
     */
    fun loadRawString(): String
}

/**
 * Interface for retrieving and committing data to/from a cached source,
 * usually elsewhere in local storage.
 */
interface LocalDataReader: LocalUncachedDataReader {

    /**
     * Loads the cached version of the data from internal storage, if it exists.
     */
    fun loadCachedString(): String?

    /**
     * Saves a set of data to the cache to be retrieved later.
     * @return whether the save was successful
     */
    fun saveCachedString(data: String): Boolean

    /**
     * Deletes the cached data, returning priority to the app's internal data.
     * @return whether the deletion was successful
     */
    fun deleteCachedString(): Boolean
}

/**
 * Interface for retrieving data from a remote location with callbacks
 */
interface RemoteData<T: Any>: FetchListener<T> {

    /**
     * Performs the fetching of the data.  This function is expected to call either [onFetchUpdated]
     * or [onFetchFailed] exactly once.
     */
    fun fetch()
}

interface FetchListener<T> {

    /**
     * Invoked when the remote data is received and the result of [checkResponse] is true.
     */
    fun onFetchUpdated(data: T) {}

    /**
     * Invoked when the remote data is not received or if [checkResponse] indicated an error.
     */
    fun onFetchFailed() {}
}

abstract class KtorRemoteData<T: Any>: BaseModel(), RemoteData<T>, KoinComponent {

    override fun fetch() {
        ktorScope.launch {
            try {
                val data = getRemoteResponse()
                withContext(Dispatchers.Main) {
                    onFetchUpdated(data)
                }
            } catch (e:Exception){
                onFetchFailed()
            }
        }
    }

    /**
     * Function invoked when the external data service should be fetched. This function
     * is always called from an internal coroutine.
     */
    abstract suspend fun getRemoteResponse(): T

    /**
     * Checks whether the received response is acceptable. In its most basic form, it simply
     * checks whether the response was successful or not.
     */
    open fun checkResponse(response: T): Boolean = true
}

/**
 * A wrapper around a data type that is both fetched from an external source and stored locally.
 * It contains logic to determine the most up-to-date version of any data and use the most recent.
 */
abstract class KtorLocalRemoteData<T: Any>(protected val localReader: LocalDataReader,
                                           protected var fetchListener: FetchListener<T>? = null): KtorRemoteData<T>() {

    val json: Json by inject()

    lateinit var data: T

    /** Provided a string, constructs and returns an appropriate [T]. */
    abstract fun createLocalDataFromText(text: String): T

    /** Provided a [T], returns an appropriate string representation for later use. */
    abstract fun createTextToData(data: T): String

        /** @return a version number for a given [T] so this structure can determine whether it
     * needs to be updated. */
    open fun getDataVersion(data: T = this.data): Int = Int.MIN_VALUE

    override fun checkResponse(response: T) = super.checkResponse(response) && shouldUpdate(response)

    override fun onFetchUpdated(data: T) {
        super.onFetchUpdated(data)
        onNewDataLoaded(data)
        fetchListener?.onFetchUpdated(data)
    }

    override fun onFetchFailed() {
        super.onFetchFailed()
        fetchListener?.onFetchFailed()
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
abstract class KtorMajorVersionedRemoteData<T: MajorVersioned>(localReader: LocalDataReader,
                                                               val majorVersion: Int,
                                                               fetchListener: FetchListener<T>? = null):
    KtorLocalRemoteData<T>(localReader, fetchListener) {

    //FIXME EventBus
//    protected val eventBus: EventBus by inject()

    /** Since the template is restricted to [MajorVersioned], use that version number. */
    override fun getDataVersion(data: T) = data.version

    override fun checkResponse(response: T) = super.checkResponse(response) && when {
        shouldUpdateApp(response) -> {
//            eventBus.postSticky(DataRequiresAppUpdateEvent())
            false
        }
        else -> true
    }

    override fun onNewDataLoaded(newData: T) {
        if (shouldUpdateApp(newData)) {
//            eventBus.postSticky(DataRequiresAppUpdateEvent())
        } else {
            super.onNewDataLoaded(newData)
        }
    }

    fun shouldUpdateApp() = shouldUpdateApp(this.data)

    private fun shouldUpdateApp(data: T) = data.majorVersion > majorVersion
}
