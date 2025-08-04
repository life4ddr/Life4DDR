package com.perrigogames.life4.feature.deeplink

import co.touchlab.kermit.Logger
import com.perrigogames.life4.feature.deeplink.IDeeplinkManager.Companion.DEEPLINK_PREFIX
import com.perrigogames.life4.feature.deeplink.IDeeplinkManager.Companion.SANBAI_AUTH_RETURN_PATH
import com.perrigogames.life4.feature.sanbai.ISanbaiManager
import com.perrigogames.life4.feature.sanbai.SanbaiManager
import com.perrigogames.life4.feature.songresults.SongResultsManager
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.ktor.SanbaiAPI
import com.perrigogames.life4.ktor.toChartResult
import com.perrigogames.life4.model.BaseModel
import kotlinx.coroutines.launch
import org.koin.core.component.inject

interface IDeeplinkManager {
    fun processDeeplink(deeplink: String)

    companion object {
        const val DEEPLINK_PREFIX = "life4://"
        const val SANBAI_AUTH_RETURN_PATH = "sanbai_auth"
        const val SANBAI_AUTH_RETURN_PATH_FULL = "$DEEPLINK_PREFIX$SANBAI_AUTH_RETURN_PATH"
    }
}

class DeeplinkManager : BaseModel(), IDeeplinkManager {

    private val sanbaiManager: ISanbaiManager by inject()
    private val songResultsManager: SongResultsManager by inject()
    private val logger: Logger by injectLogger("DeeplinkManager")

    override fun processDeeplink(deeplink: String) {
        val sections = deeplink
            .removePrefix(DEEPLINK_PREFIX)
            .split("/", "?")
        val queryParams = sections.lastOrNull()
            ?.split("&")
            ?.map { it.split("=") }
            ?.associate { it[0] to it.getOrNull(1) }
            ?: emptyMap()
        when (sections[0]) {
            SANBAI_AUTH_RETURN_PATH -> {
                val authCode = queryParams["code"]
//                val playerId = queryParams["player_id"]
                authCode?.let {
                    ktorScope.launch {
                        if (sanbaiManager.completeLogin(it)) {
                            sanbaiManager.fetchScores()
                        }
                    }
                }
            }
            else -> {
                logger.w("Unknown deeplink: ${sections[0]}")
            }
        }
    }
}