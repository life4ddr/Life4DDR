package com.perrigogames.life4.api

import com.perrigogames.life4.ktor.GithubDataAPI
import com.perrigogames.life4.util.indexOfOrEnd
import org.koin.core.inject

class SongListRemoteData(reader: LocalDataReader, listener: FetchListener<String>? = null):
    KtorLocalRemoteData<String>(reader, listener) {

    private val githubKtor: GithubDataAPI by inject()

    override fun createLocalDataFromText(text: String) = text
    override fun createTextToData(data: String) = data
    override fun getDataVersion(data: String) = data.substring(0, data.indexOfOrEnd('\n')).trim().toInt()
    override suspend fun getRemoteResponse(): String = githubKtor.getSongList()
}
