package com.perrigogames.life4.model

import com.perrigogames.life4.LadderDialogs
import com.perrigogames.life4.api.LadderRemoteData
import com.perrigogames.life4.api.base.CompositeData
import com.perrigogames.life4.api.base.LocalDataReader
import com.perrigogames.life4.data.BaseRankGoal
import com.perrigogames.life4.data.LadderRankData
import com.perrigogames.life4.data.LadderVersion
import com.perrigogames.life4.db.GoalDatabaseHelper
import com.perrigogames.life4.db.GoalState
import com.perrigogames.life4.enums.GoalStatus
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.RANKS_FILE_NAME
import com.perrigogames.life4.model.settings.LadderSettingsManager
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.core.component.inject
import org.koin.core.qualifier.named

@OptIn(ExperimentalSerializationApi::class)
class LadderManager: BaseModel() {

    private val ignoreListManager: IgnoreListManager by inject()
    private val goalDBHelper: GoalDatabaseHelper by inject()
    private val ladderProgressManager: LadderProgressManager by inject()
    private val ladderDialogs: LadderDialogs by inject()
    private val ladderSettings: LadderSettingsManager by inject()
    private val dataReader: LocalDataReader by inject(named(RANKS_FILE_NAME))

    //
    // Ladder Data
    //
    private val ladderDataRemote = LadderRemoteData(dataReader, object : CompositeData.NewDataListener<LadderRankData> {
        override fun onDataVersionChanged(data: LadderRankData) {
            ladderDialogs.showLadderUpdateToast()
            // FIXME eventBus.post(LadderRanksReplacedEvent())
        }

        override fun onMajorVersionBlock() {
            // FIXME eventBus.postSticky(DataRequiresAppUpdateEvent())
        }
    }).apply { start() }

    val dataVersionString get() = ladderDataRemote.versionString

    val ladderData: LadderRankData get() = ladderDataRemote.data
    val currentRequirements: LadderVersion
        get() = ignoreListManager.selectedIgnoreList.baseVersion.let { version ->
            ladderData.gameVersions[version] ?: error("Rank requirements not found for version $version")
        }

    //
    // Local User Rank
    //
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
        ladderSettings.setTargetRank(null)
    }

    fun setUserTargetRank(rank: LadderRank?) {
        ladderSettings.setTargetRank(rank)
    }

    //
    // Rank Navigation
    //
    fun findRankEntry(rank: LadderRank?) = currentRequirements.rankRequirements.firstOrNull { it.rank == rank }

    fun previousEntry(rank: LadderRank?) = previousEntry(currentRequirements.rankRequirements.indexOfFirst { it.rank == rank })

    fun previousEntry(index: Int) = currentRequirements.rankRequirements.getOrNull(index - 1)

    fun nextEntry(rank: LadderRank?) = nextEntry(currentRequirements.rankRequirements.indexOfFirst { it.rank == rank })

    fun nextEntry(index: Int) = currentRequirements.rankRequirements.getOrNull(index + 1)

    //
    // Goal State
    //
    fun getGoalState(goal: BaseRankGoal): GoalState? =
        goalDBHelper.stateForId(goal.id.toLong())

    fun getOrCreateGoalState(goal: BaseRankGoal): GoalState =
        getGoalState(goal) ?: GoalState(goal.id.toLong(), GoalStatus.INCOMPLETE, Clock.System.now().toString())

    fun getGoalStateList(goals: List<BaseRankGoal>): List<GoalState> =
        goalDBHelper.statesForIdList(goals.map { it.id.toLong() }).executeAsList()

    fun setGoalState(id: Long, status: GoalStatus) {
        goalDBHelper.insertGoalState(id, status)
    }

    fun clearGoalStates() {
        ladderDialogs.onClearGoalStates {
            mainScope.launch {
                goalDBHelper.deleteAll()
            }
            ladderProgressManager.clearAllResults()
            // FIXME eventBus.post(LadderRankUpdatedEvent())
        }
    }
}
