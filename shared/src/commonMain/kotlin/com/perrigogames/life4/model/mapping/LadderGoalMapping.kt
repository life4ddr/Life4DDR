package com.perrigogames.life4.model.mapping

import com.perrigogames.life4.PlatformStrings
import com.perrigogames.life4.data.BaseRankGoal
import com.perrigogames.life4.data.ladder.UILadderGoal
import com.perrigogames.life4.db.GoalState
import com.perrigogames.life4.enums.GoalStatus
import com.perrigogames.life4.model.GoalStateManager
import com.perrigogames.life4.model.LadderProgressManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LadderGoalMapper : KoinComponent {

    private val goalStateManager: GoalStateManager by inject()
    private val ladderProgressManager: LadderProgressManager by inject()
    private val platformStrings: PlatformStrings by inject()

    fun toViewData(
        base: BaseRankGoal,
        goalState: GoalState = goalStateManager.getOrCreateGoalState(base),
    ) = UILadderGoal(
        id = base.id.toLong(),
        goalText = base.goalString(platformStrings),
        completed = goalState.status == GoalStatus.COMPLETE,
        hidden = goalState.status == GoalStatus.IGNORED,
        canHide = !base.isMandatory
    )
}