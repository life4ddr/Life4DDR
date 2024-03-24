package com.perrigogames.life4.feature.songlist

import co.touchlab.kermit.Logger
import com.perrigogames.life4.api.IgnoreListRemoteData
import com.perrigogames.life4.api.base.LocalDataReader
import com.perrigogames.life4.api.base.unwrapLoaded
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.IGNORES_FILE_NAME
import com.perrigogames.life4.model.BaseModel
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.serialization.json.Json
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class IgnoreListManager: BaseModel() {

    private val json: Json by inject()
    private val logger: Logger by injectLogger("IgnoreList")
    private val settings: Settings by inject()
    private val dataReader: LocalDataReader by inject(named(IGNORES_FILE_NAME))
    private val songDataManager: SongDataManager by inject()

    private val ignoreListsData = IgnoreListRemoteData(dataReader).apply { start() }
//        override fun onDataLoaded(data: IgnoreListData) {
//            data.evaluateIgnoreLists()
//        }

    val ignoreListsFlow = ignoreListsData.dataState
        .unwrapLoaded()
        .filterNotNull()

    val dataVersionString get() = ignoreListsData.versionState.value.versionString
}
