package com.perrigogames.life4.feature.profile

import com.perrigogames.life4.data.SocialNetwork
import com.perrigogames.life4.feature.settings.UserInfoSettings
import com.perrigogames.life4.model.BaseModel
import kotlinx.coroutines.flow.combine
import org.koin.core.component.inject

class UserInfoManager : BaseModel() {

    private val userInfoSettings: UserInfoSettings by inject()

    val userInfoFlow = combine(
        userInfoSettings.userName,
        userInfoSettings.rivalCode,
        userInfoSettings.socialNetworks
    ) { userName, rivalCode, socialNetworks ->
        UIUserInfo(
            userName = userName,
            rivalCode = rivalCode,
            socialNetworks = socialNetworks
        )
    }
}

data class UIUserInfo(
    val userName: String,
    val rivalCode: String,
    val socialNetworks: Map<SocialNetwork, String>
)