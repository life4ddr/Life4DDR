package com.perrigogames.life4trials.data

import java.io.File

interface ServiceListener {
    fun loggedIn()
    fun fileDownloaded(file: File)
    fun cancelled()
    fun handleError(exception: Exception)
}