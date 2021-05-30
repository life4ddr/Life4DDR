package com.perrigogames.life4.api

import com.perrigogames.life4.data.MessageOfTheDay
import com.perrigogames.life4.ktor.GithubDataAPI
import org.koin.core.inject

class MotdRemoteData(
    private val listener: FetchListener<MessageOfTheDay>,
): KtorRemoteData<MessageOfTheDay>() {

    private val githubKtor: GithubDataAPI by inject()

    override suspend fun getRemoteResponse() = githubKtor.getMotd()

    override fun onFetchUpdated(data: MessageOfTheDay) = listener.onFetchUpdated(data)
    override fun onFetchFailed() = listener.onFetchFailed()
}