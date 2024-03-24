package com.perrigogames.life4.model

import com.perrigogames.life4.LadderDialogs
import com.perrigogames.life4.feature.songlist.SongDataManager
import com.perrigogames.life4.feature.songresults.SongResultsManager
import org.koin.core.component.inject

/**
 * A helper structure designed to make sure song data and ladder progress initialize
 * in the right order.  Also helps facilitate complex interactions between these two
 * systems.
 */
class SongDataCoordinator : BaseModel() {

    private val songDataManager: SongDataManager by inject()
    private val songResultsManager: SongResultsManager by inject()
    private val ladderDialogs: LadderDialogs by inject()

    init {
        songDataManager.start {
            songResultsManager.refresh()
        }
    }

    fun clearSongResults() {
        ladderDialogs.onClearSongResults {
            songResultsManager.clearAllResults()
        }
    }

    fun refreshSongDatabase() {
        ladderDialogs.onRefreshSongDatabase {
            songResultsManager.clearAllResults()
            songDataManager.refreshSongDatabase(delete = true)
        }
    }
}