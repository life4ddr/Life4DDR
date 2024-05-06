package com.perrigogames.life4.api.base

import com.perrigogames.life4.model.BaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface FetchListener<T> {
    /**
     * Invoked when the remote data is received and the result of [checkResponse] is true.
     */
    fun onFetchUpdated(newData: T) {}

    /**
     * Invoked when the remote data is not received or if [checkResponse] indicated an error.
     */
    fun onFetchFailed(e: Throwable) {}
}

abstract class RemoteData<T : Any> : BaseModel(), DelayedDataSource<T> {
    override fun fetch(listener: FetchListener<T>) {
        ktorScope.launch {
            try {
                val data = getRemoteResponse()
                withContext(Dispatchers.Main) {
                    if (checkResponse(data)) {
                        listener.onFetchUpdated(data)
                    } else {
                        listener.onFetchFailed(Error("Response validation failed"))
                    }
                }
            } catch (e: Exception) {
                println(e)
                withContext(Dispatchers.Main) { listener.onFetchFailed(e) }
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
