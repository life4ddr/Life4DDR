package com.perrigogames.life4.model

interface EventBusNotifier {

    fun post(event: Any)
    fun postSticky(event: Any)
}
