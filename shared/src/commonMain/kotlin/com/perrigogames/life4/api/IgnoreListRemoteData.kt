package com.perrigogames.life4.api

import com.perrigogames.life4.data.IgnoreListData
import com.perrigogames.life4.ktor.GithubDataAPI
import org.koin.core.inject

class IgnoreListRemoteData(reader: LocalDataReader): KtorMajorVersionedRemoteData<IgnoreListData>(reader, 1) {

    private val githubKtor: GithubDataAPI by inject()

    override suspend fun getRemoteResponse() = githubKtor.getIgnoreLists()
    override fun createLocalDataFromText(text: String) = json.parse(IgnoreListData.serializer(), text)
    override fun createTextToData(data: IgnoreListData) = json.stringify(IgnoreListData.serializer(), data)

    override fun onNewDataLoaded(newData: IgnoreListData) {
        super.onNewDataLoaded(newData)
        newData.evaluateIgnoreLists()
    }
}
