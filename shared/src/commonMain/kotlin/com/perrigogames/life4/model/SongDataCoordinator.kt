package com.perrigogames.life4.model

import com.perrigogames.life4.LadderDialogs
import com.perrigogames.life4.SongResultsUpdatedEvent
import org.koin.core.component.inject

/**
 * A helper structure designed to make sure song data and ladder progress initialize
 * in the right order.  Also helps facilitate complex interactions between these two
 * systems.
 */
class SongDataCoordinator : BaseModel() {

    private val songDataManager: SongDataManager by inject()
    private val ladderProgressManager: LadderProgressManager by inject()
    private val ladderDialogs: LadderDialogs by inject()
    private val eventBus: EventBusNotifier by inject()

    init {
        songDataManager.start {
            ladderProgressManager.refresh()
        }
    }

    fun clearSongResults() {
        ladderDialogs.onClearSongResults {
            ladderProgressManager.clearAllResults()
            eventBus.post(SongResultsUpdatedEvent())
        }
    }

    fun refreshSongDatabase() {
        ladderDialogs.onRefreshSongDatabase {
            ladderProgressManager.clearAllResults()
            songDataManager.refreshSongDatabase(delete = true)
            eventBus.post(SongResultsUpdatedEvent())
        }
    }
}