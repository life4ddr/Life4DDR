package com.perrigogames.life4trials.manager

import android.content.Context
import android.widget.Toast
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.api.ApiPlayer
import com.perrigogames.life4trials.data.LadderRank
import com.perrigogames.life4trials.life4app
import kotlinx.coroutines.*
import retrofit2.Response

/**
 * A manager class that handles player data. This includes the local
 * user's data as well as leaderboards that list out ranges of players.
 */
class PlayerManager(private val context: Context) {

    private var importJob: Job? = null
    private var ladderJob: Job? = null

    var ladderPlayers: MutableList<ApiPlayer> = mutableListOf()
        private set

    private val life4Api = context.life4app.life4Api

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
            delay(750)
            withContext(Dispatchers.Main) {
                if (BuildConfig.DEBUG && playerName == "KONNOR") {
                    val player = ApiPlayer(1, playerName, "GOLD III", "", "@CodingCabbit", "", "1234-5678", true)
                    Life4Application.eventBus.post(PlayerImportedEvent(player))
                } else {
                    Life4Application.eventBus.post(PlayerImportedEvent())
                }
                importJob = null
            }
        }
    }

    fun fetchLadderLeaderboards() {
        if (ladderPlayers.isNotEmpty()) {
            Life4Application.eventBus.post(PlayerLadderUpdatedEvent(ladderPlayers))
        } else {
            ladderJob?.cancel()
            ladderJob = CoroutineScope(Dispatchers.IO).launch {
                val response = life4Api.getAllPlayers()
                withContext(Dispatchers.Main) {
                    if (response.check()) {
                        ladderPlayers.addAll(response.body()!!
                            .filterNot { it.rank == null }
                            .sortedWith(compareBy({ LadderRank.values().size - it.rank!!.ordinal }, { it.name?.toLowerCase() })))
                        Life4Application.eventBus.post(PlayerLadderUpdatedEvent(ladderPlayers))
                    }
                    ladderJob = null
                }
            }
        }
    }

    private fun Response<List<ApiPlayer>>.check(): Boolean = when {
        !isSuccessful -> {
            Toast.makeText(context, errorBody()!!.string(), Toast.LENGTH_SHORT).show()
            Life4Application.eventBus.post(PlayerLadderUpdatedEvent())
            false
        }
        body()!!.isEmpty() -> {
            Toast.makeText(context, R.string.status_error, Toast.LENGTH_SHORT).show()
            Life4Application.eventBus.post(PlayerLadderUpdatedEvent())
            false
        } //FIXME
        else -> true
    }

    class PlayerImportedEvent(val apiPlayer: ApiPlayer? = null)
    class PlayerLadderUpdatedEvent(val apiPlayers: List<ApiPlayer>? = null)
}