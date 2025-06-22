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
interface UserRankManager {
    val rank: StateFlow<LadderRank?>

    val targetRank: StateFlow<LadderRank?>

    fun setUserRank(rank: LadderRank?)

    fun setUserTargetRank(rank: LadderRank?)
}

class DefaultUserRankManager : BaseModel(), UserRankManager {

    private val ladderSettings: UserRankSettings by inject()
    private val logger: Logger by injectLogger("UserRankManager")

    override val rank: StateFlow<LadderRank?> = ladderSettings.rank
        .onEach { logger.v { "RANK: $it" } }
        .stateIn(mainScope, started = SharingStarted.Lazily, initialValue = null)

    override val targetRank: StateFlow<LadderRank?> = ladderSettings.targetRank
        .onEach { logger.v { "TARGET RANK: $it" } }
        .stateIn(mainScope, started = SharingStarted.Lazily, initialValue = null)

    override fun setUserRank(rank: LadderRank?) {
        ladderSettings.setRank(rank)
        ladderSettings.setTargetRank(rank.nullableNext)
    }

    override fun setUserTargetRank(rank: LadderRank?) {
        ladderSettings.setTargetRank(rank)
    }
}