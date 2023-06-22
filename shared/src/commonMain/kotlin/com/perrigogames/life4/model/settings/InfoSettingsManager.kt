package com.perrigogames.life4.model.settings

import com.perrigogames.life4.SettingsKeys
import com.perrigogames.life4.data.SocialNetwork
import com.perrigogames.life4.enums.LadderRank
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
class InfoSettingsManager : SettingsManager() {

    val userName: Flow<String> = settings.getStringFlow(SettingsKeys.KEY_INFO_NAME, "")
    val rivalCode: Flow<String> = settings.getStringFlow(SettingsKeys.KEY_INFO_RIVAL_CODE, "")
    val userRank: Flow<LadderRank?> = settings.getLongOrNullFlow(SettingsKeys.KEY_INFO_RANK)
        .map { LadderRank.parse(it) }

    val socialNetworks: Flow<Map<SocialNetwork, String>> =
        settings.getStringFlow(SettingsKeys.KEY_INFO_SOCIAL_NETWORKS, "")
            .map { settingsString ->
                settingsString.split(SOCIAL_LINE_DELIM)
                    .map { it.split(SOCIAL_ENTRY_DELIM) }
                    .associate { SocialNetwork.parse(it[0]) to it[1] }
            }

    fun setUserBasics(
        name: String,
        rivalCode: String,
        socialNetworks: Map<SocialNetwork, String>
    ) {
        setUserName(name)
        setRivalCode(rivalCode)
        setSocialNetworks(socialNetworks)
    }

    fun setUserName(name: String) = mainScope.launch {
        settings.putString(SettingsKeys.KEY_INFO_NAME, name)
    }

    fun setRivalCode(code: String) = mainScope.launch {
        settings.putString(SettingsKeys.KEY_INFO_RIVAL_CODE, code)
    }

    fun setRank(rank: LadderRank?) = mainScope.launch {
        if (rank != null) {
            settings.putLong(SettingsKeys.KEY_INFO_RANK, rank.stableId)
        } else {
            settings.remove(SettingsKeys.KEY_INFO_RANK)
        }
    }

    fun setSocialNetworks(networks: Map<SocialNetwork, String>) {
        val networksString = networks.toList()
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