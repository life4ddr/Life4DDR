package com.perrigogames.life4trials.manager

import android.content.Context
import android.widget.Toast
import com.perrigogames.life4.data.LadderRank
import com.perrigogames.life4.model.BaseModel
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.api.ApiPlayer
import com.perrigogames.life4trials.api.RetrofitLife4API
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.koin.core.inject
import retrofit2.Response

/**
 * A manager class that handles player data. This includes the local
 * user's data as well as leaderboards that list out ranges of players.
 */
class PlayerManager: BaseModel() {

    private val context: Context by inject()
    private val life4Api: RetrofitLife4API by inject()
    private val eventBus: EventBus by inject()

    private var importJob: Job? = null
    private var ladderJob: Job? = null

    var ladderPlayers: MutableList<ApiPlayer> = mutableListOf()
        private set

    fun importPlayerInfo(playerName: String) {
        importJob?.cancel()
        importJob = CoroutineScope(Dispatchers.IO).launch {
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
                    val player = ApiPlayer(1, playerName, "DIAMOND I", "", "@CodingCabbit", "", "5142-3911", true)
                    eventBus.post(PlayerImportedEvent(player))
                } else {
                    eventBus.post(PlayerImportedEvent())
                }
                importJob = null
            }
        }
    }

    fun fetchLadderLeaderboards() {
        if (ladderPlayers.isNotEmpty()) {
            eventBus.post(PlayerLadderUpdatedEvent(ladderPlayers))
        } else {
            ladderJob?.cancel()
            ladderJob = CoroutineScope(Dispatchers.IO).launch {
                val response = life4Api.getAllPlayers()
                withContext(Dispatchers.Main) {
                    if (response.check()) {
                        ladderPlayers.addAll(response.body()!!
                            .filterNot { it.rank == null }
                            .sortedWith(compareBy({ LadderRank.values().size - it.rank!!.ordinal }, { it.name.toLowerCase() })))
                        eventBus.post(PlayerLadderUpdatedEvent(ladderPlayers))
                    }
                    ladderJob = null
                }
            }
        }
    }

    private fun Response<List<ApiPlayer>>.check(): Boolean = when {
        !isSuccessful -> {
            Toast.makeText(context, errorBody()!!.string(), Toast.LENGTH_SHORT).show()
            eventBus.post(PlayerLadderUpdatedEvent())
            false
        }
        body()!!.isEmpty() -> {
            Toast.makeText(context, R.string.status_error, Toast.LENGTH_SHORT).show()
            eventBus.post(PlayerLadderUpdatedEvent())
            false
        } //FIXME
        else -> true
    }

    class PlayerImportedEvent(val apiPlayer: ApiPlayer? = null)
    class PlayerLadderUpdatedEvent(val apiPlayers: List<ApiPlayer>? = null)
}
