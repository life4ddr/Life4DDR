package com.perrigogames.life4.api

import com.perrigogames.life4.data.MessageOfTheDay
import com.perrigogames.life4.ktor.GithubDataAPI
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.koin.core.inject

class MotdLocalRemoteData(
    reader: LocalDataReader,
    listener: FetchListener<MessageOfTheDay>? = null
): KtorLocalRemoteData<MessageOfTheDay>(reader, listener) {

    private val githubKtor: GithubDataAPI by inject()

    override suspend fun getRemoteResponse() = githubKtor.getMotd()

    override fun createLocalDataFromText(text: String) = json.decodeFromString(MessageOfTheDay.serializer(), text)

    override fun createTextToData(data: MessageOfTheDay) = json.encodeToString(data)
}