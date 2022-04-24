package com.perrigogames.life4.model

import com.perrigogames.life4.PlayerImportedEvent
import com.perrigogames.life4.data.ApiPlayer
import com.perrigogames.life4.isDebug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject

/**
 * A manager class that handles player data. This includes the local
 * user's data as well as leaderboards that list out ranges of players.
 */
class PlayerManager: BaseModel() {

    private val eventBus: EventBusNotifier by inject()

    fun importPlayerInfo(playerName: String) {
        mainScope.launch {
            //FIXME the API isn't currently operational
            if (isDebug) {
                delay(750) // simulate fetching a user on debug
            }
            withContext(Dispatchers.Main) {
                if (isDebug && playerName == "KONNOR") {
                    val player = ApiPlayer(1, playerName, "DIAMOND III", "",
                        "@strogazer", "", "5142-3911", true)
                    eventBus.post(PlayerImportedEvent(player))
                } else {
                    eventBus.post(PlayerImportedEvent())
                }
            }
        }
    }
}
