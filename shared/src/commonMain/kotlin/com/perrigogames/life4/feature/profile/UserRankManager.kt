package com.perrigogames.life4.feature.profile

import co.touchlab.kermit.Logger
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.nullableNext
import com.perrigogames.life4.feature.settings.UserRankSettings
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.model.BaseModel
import kotlinx.coroutines.flow.*
import org.koin.core.component.inject

/**
 * Manager class that deals with the current user's rank information.
 */
class UserRankManager : BaseModel() {
    private val ladderSettings: UserRankSettings by inject()
    private val logger: Logger by injectLogger("UserRankManager")

    val rank: StateFlow<LadderRank?> =
        ladderSettings.rank
            .onEach { logger.v { "RANK: $it" } }
            .stateIn(mainScope, started = SharingStarted.Lazily, initialValue = null)

    val targetRank: StateFlow<LadderRank?> =
        ladderSettings.targetRank
            .onEach { logger.v { "TARGET RANK: $it" } }
            .stateIn(mainScope, started = SharingStarted.Lazily, initialValue = null)

    fun setUserRank(rank: LadderRank?) {
        ladderSettings.setRank(rank)
        ladderSettings.setTargetRank(rank.nullableNext)
    }

    fun setUserTargetRank(rank: LadderRank?) {
        ladderSettings.setTargetRank(rank)
    }
}
