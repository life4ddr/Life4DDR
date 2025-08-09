package com.perrigogames.life4.feature.sanbai

import com.perrigogames.life4.MR
import com.perrigogames.life4.feature.banners.BannerLocation
import com.perrigogames.life4.feature.banners.IBannerManager
import com.perrigogames.life4.feature.banners.UIBanner
import com.perrigogames.life4.feature.banners.UIBannerTemplates
import com.perrigogames.life4.feature.songlist.SongDataManager
import com.perrigogames.life4.feature.songresults.SongResultsManager
import com.perrigogames.life4.ktor.SanbaiAPI
import com.perrigogames.life4.ktor.toChartResult
import com.perrigogames.life4.model.BaseModel
import dev.icerock.moko.resources.desc.color.asColorDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.component.inject

interface ISanbaiManager {

    fun requiresAuthorization(): Boolean
    suspend fun completeLogin(authCode: String): Boolean
    suspend fun fetchScores(): Boolean
}

class SanbaiManager : BaseModel(), ISanbaiManager {

    private val sanbaiAPI: SanbaiAPI by inject()
    private val sanbaiAPISettings: ISanbaiAPISettings by inject()
    private val songResultsManager: SongResultsManager by inject()
    private val bannersManager: IBannerManager by inject()

    override fun requiresAuthorization(): Boolean {
        return sanbaiAPISettings.refreshExpires < Clock.System.now()
    }

    override suspend fun completeLogin(authCode: String): Boolean {
        bannersManager.setBanner(BANNER_LOADING, BannerLocation.PROFILE, BannerLocation.SCORES)
        try {
            sanbaiAPI.getSessionToken(authCode)
        } catch (e: Exception) {
            bannersManager.setBanner(BANNER_ERROR, BannerLocation.PROFILE, BannerLocation.SCORES, durationSeconds = 3)
            return false
        }
        return true
    }

    override suspend fun fetchScores(): Boolean {
        if (requiresAuthorization()) {
            return false
        }
        bannersManager.setBanner(BANNER_LOADING, BannerLocation.PROFILE, BannerLocation.SCORES)
        try {
            sanbaiAPI.getScores()?.let { scores ->
                songResultsManager.addScores(scores.map { it.toChartResult() })
            }
        } catch (e: Exception) {
            bannersManager.setBanner(BANNER_ERROR, BannerLocation.PROFILE, BannerLocation.SCORES, durationSeconds = 3)
            return false
        }
        bannersManager.setBanner(BANNER_SUCCESS, BannerLocation.PROFILE, BannerLocation.SCORES, durationSeconds = 3)
        return true
    }

    companion object {
        val BANNER_LOADING = UIBanner(
            text = MR.strings.sanbai_syncing_scores.desc()
        )
        val BANNER_SUCCESS = UIBannerTemplates.success(MR.strings.sanbai_syncing_success.desc())
        val BANNER_ERROR = UIBannerTemplates.error(MR.strings.sanbai_syncing_error.desc())
    }
}