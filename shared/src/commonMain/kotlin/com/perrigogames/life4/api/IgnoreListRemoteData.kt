package com.perrigogames.life4.api

import com.perrigogames.life4.data.IgnoreListData
import com.perrigogames.life4.data.IgnoreListData.Companion.IGNORE_LIST_MAJOR_VERSION
import com.perrigogames.life4.ktor.GithubDataAPI
import org.koin.core.inject

class IgnoreListRemoteData(reader: LocalDataReader):
    KtorMajorVersionedRemoteData<IgnoreListData>(reader, IGNORE_LIST_MAJOR_VERSION) {

    private val githubKtor: GithubDataAPI by inject()

    override suspend fun getRemoteResponse() = githubKtor.getIgnoreLists()
    override fun createLocalDataFromText(text: String) = json.decodeFromString(IgnoreListData.serializer(), text)
    override fun createTextToData(data: IgnoreListData) = json.encodeToString(IgnoreListData.serializer(), data)

    override fun onNewDataLoaded(newData: IgnoreListData) {
        super.onNewDataLoaded(newData)
        newData.evaluateIgnoreLists()
    }
}
