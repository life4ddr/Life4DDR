package com.perrigogames.life4trials.manager

import com.perrigogames.life4.PlayerImportedEvent
import com.perrigogames.life4.data.ApiPlayer
import com.perrigogames.life4.model.BaseModel
import com.perrigogames.life4trials.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.koin.core.inject

/**
 * A manager class that handles player data. This includes the local
 * user's data as well as leaderboards that list out ranges of players.
 */
class PlayerManager: BaseModel() {

    private val eventBus: EventBus by inject()

    var ladderPlayers: MutableList<ApiPlayer> = mutableListOf()
        private set

    fun importPlayerInfo(playerName: String) {
        mainScope.launch {
            //FIXME the API isn't currently operational
//            val response = life4Api.getPlayer(playerName)
//            withContext(Dispatchers.Main) {
//                if (response?.check()) {
//                    Life4Application.eventBus.post(PlayerImportedEvent(response.body()!![0]))
//                }
//                importJob = null
//            }
            if (BuildConfig.DEBUG) {
                delay(750) // simulate fetching a user on debug
            }
            withContext(Dispatchers.Main) {
                if (BuildConfig.DEBUG && playerName == "KONNOR") {
                    val player = ApiPlayer(1, playerName, "DIAMOND III", "",
                        "@strogazer", "", "5142-3911", true)
                    eventBus.post(PlayerImportedEvent(player))
                } else {
                    eventBus.post(PlayerImportedEvent())
                }
            }
        }
    }

    fun fetchLadderLeaderboards() {
        if (ladderPlayers.isNotEmpty()) {
//            eventBus.post(PlayerLadderUpdatedEvent(ladderPlayers))
        } else {
//            ladderJob?.cancel()
//            ladderJob = CoroutineScope(Dispatchers.IO).launch {
//                val response = life4Api.getAllPlayers()
//                withContext(Dispatchers.Main) {
//                    if (response.check()) {
//                        ladderPlayers.addAll(response.body()!!
//                            .filterNot { it.rank == null }
//                            .sortedWith(compareBy({ LadderRank.values().size - it.rank!!.ordinal }, { it.name.toLowerCase() })))
//                        eventBus.post(PlayerLadderUpdatedEvent(ladderPlayers))
//                    }
//                    ladderJob = null
//                }
//            }
        }
    }

//    private fun Response<List<ApiPlayer>>.check(): Boolean = when {
//        !isSuccessful -> {
//            Toast.makeText(context, errorBody()!!.string(), Toast.LENGTH_SHORT).show()
//            eventBus.post(PlayerLadderUpdatedEvent())
//            false
//        }
//        body()!!.isEmpty() -> {
//            Toast.makeText(context, R.string.status_error, Toast.LENGTH_SHORT).show()
//            eventBus.post(PlayerLadderUpdatedEvent())
//            false
//        } //FIXME
//        else -> true
//    }
}
