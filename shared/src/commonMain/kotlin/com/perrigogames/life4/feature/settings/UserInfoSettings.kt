package com.perrigogames.life4.feature.settings

import co.touchlab.kermit.Logger
import com.perrigogames.life4.GameConstants
import com.perrigogames.life4.SettingsKeys
import com.perrigogames.life4.data.SocialNetwork
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.injectLogger
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalSettingsApi::class)
class UserInfoSettings : SettingsManager() {
    private val logger: Logger by injectLogger("UserInfoSettings")

    val userName: StateFlow<String> =
        settings.getStringFlow(SettingsKeys.KEY_INFO_NAME, "")
            .stateIn(mainScope, SharingStarted.Eagerly, "")

    val rivalCode: StateFlow<String> =
        settings.getStringFlow(SettingsKeys.KEY_INFO_RIVAL_CODE, "")
            .stateIn(mainScope, SharingStarted.Eagerly, "")

    val rivalCodeDisplay: Flow<String?> =
        rivalCode.map {
            if (it.length == GameConstants.RIVAL_CODE_LENGTH) {
                "${it.substring(0..3)}-${it.substring(4..7)}"
            } else {
                null
            }
        }

    val userRank: StateFlow<LadderRank?> =
        settings.getLongOrNullFlow(SettingsKeys.KEY_INFO_RANK)
            .map { LadderRank.parse(it) }
            .stateIn(mainScope, SharingStarted.Eagerly, null)

    val socialNetworks: StateFlow<Map<SocialNetwork, String>> =
        settings.getStringFlow(SettingsKeys.KEY_INFO_SOCIAL_NETWORKS, "")
            .map { settingsString ->
                settingsString.split(SOCIAL_LINE_DELIM)
                    .map { it.split(SOCIAL_ENTRY_DELIM) }
                    .filter { it.size == 2 }
                    .associate { SocialNetwork.parse(it[0]) to it[1] }
            }
            .stateIn(mainScope, SharingStarted.Eagerly, emptyMap())

    fun setUserBasics(
        name: String,
        rivalCode: String,
        socialNetworks: Map<SocialNetwork, String>,
    ) {
        setUserName(name)
        setRivalCode(rivalCode)
        setSocialNetworks(socialNetworks)
    }

    fun setUserName(name: String) =
        mainScope.launch {
            settings.putString(SettingsKeys.KEY_INFO_NAME, name)
        }

    fun setRivalCode(code: String) =
        mainScope.launch {
            settings.putString(SettingsKeys.KEY_INFO_RIVAL_CODE, code)
        }

    fun setRank(rank: LadderRank?) =
        mainScope.launch {
            if (rank != null) {
                settings.putLong(SettingsKeys.KEY_INFO_RANK, rank.stableId)
            } else {
                settings.remove(SettingsKeys.KEY_INFO_RANK)
            }
        }

    fun setSocialNetworks(networks: Map<SocialNetwork, String>) {
        val networksString =
            networks.toList()
                .joinToString(SOCIAL_LINE_DELIM.toString()) { (k, v) ->
                    k.toString() + SOCIAL_ENTRY_DELIM + v
                }

        mainScope.launch {
            settings.putString(SettingsKeys.KEY_INFO_SOCIAL_NETWORKS, networksString)
        }
    }

    companion object {
        const val SOCIAL_LINE_DELIM = '/'
        const val SOCIAL_ENTRY_DELIM = '"'
    }
}
