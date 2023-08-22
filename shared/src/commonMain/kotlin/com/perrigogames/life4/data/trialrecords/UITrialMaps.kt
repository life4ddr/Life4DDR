package com.perrigogames.life4.data.trialrecords

import com.perrigogames.life4.db.TrialSession
import com.perrigogames.life4.db.TrialSong
import com.perrigogames.life4.model.SongDataManager
import com.perrigogames.life4.model.TrialManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UITrialMaps : KoinComponent {

    private val songManager: SongDataManager by inject()
    private val trialManager: TrialManager by inject()

    /**
     * @throws Error if the associated Trial or Songs cannot be found
     */
    fun trialRecordUIModel(
        session: TrialSession,
        songs: List<TrialSong>,
    ): UITrialRecord {
        val trial = trialManager.findTrial(session.trialId)
            ?: throw Error("Trial ${session.trialId} not found")
        return UITrialRecord(
            trialTitleText = trial.name,
            trialSubtitleText = when {
                trial.isRetired -> "(Retired)"
                trial.isEvent -> "(Event)"
                else -> null
            },
            exScoreText = "FIXME",
            exProgressPercent = 0f, // FIXME
            trialSongs = songs.map { trialSong ->
                val s = trial.songs[trialSong.position.toInt()]
//                val song = songManager.findSong(trialSong.id) ?: throw Error("Song ${} not found")
                UITrialSong(
                    songTitleText = "I'm in terrible pain",
                    scoreText = "FIXME",
                    difficultyClass = s.difficultyClass,
                )
            },
            achieved = session.goalObtained,
            rank = session.goalRank,
        )
    }
}