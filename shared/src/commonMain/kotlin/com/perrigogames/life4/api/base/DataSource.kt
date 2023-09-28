package com.perrigogames.life4.api.base

interface InstantDataSource<T: Any> {
    val data: T?
}

interface DelayedDataSource<T: Any> {
    fun fetch(listener: FetchListener<T>)
}

interface StringToData<T: Any> {

    /** Provided a string, constructs and returns an appropriate [T]. */
    fun create(s: String): T
}

interface DataToString<T: Any> {

    /** Provided a [T], returns an appropriate string representation for later use. */
    fun create(data: T): String
}

interface Converter<T: Any>: StringToData<T>, DataToString<T>
