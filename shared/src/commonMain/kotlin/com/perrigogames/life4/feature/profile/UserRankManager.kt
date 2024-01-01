package com.perrigogames.life4.feature.profile

import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.nullableNext
import com.perrigogames.life4.model.BaseModel
import com.perrigogames.life4.model.settings.UserRankSettings
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject

/**
 * Manager class that deals with the current user's rank information.
 */
class UserRankManager : BaseModel() {

    private val ladderSettings: UserRankSettings by inject()

    private val _rank = MutableStateFlow<LadderRank?>(null).cMutableStateFlow()
    val rank: StateFlow<LadderRank?> = _rank
    val currentRank: LadderRank? get() = rank.value

    private val _targetRank = MutableStateFlow<LadderRank?>(null).cMutableStateFlow()
    val targetRank: StateFlow<LadderRank?> = _targetRank

    init {
        mainScope.launch {
            ladderSettings.rank.collect { _rank.emit(it) }
            ladderSettings.targetRank.collect { _targetRank.emit(it) }
        }
    }

    fun setUserRank(rank: LadderRank?) {
        ladderSettings.setRank(rank)
        ladderSettings.setTargetRank(rank.nullableNext)
    }

    fun setUserTargetRank(rank: LadderRank?) {
        ladderSettings.setTargetRank(rank)
    }
}