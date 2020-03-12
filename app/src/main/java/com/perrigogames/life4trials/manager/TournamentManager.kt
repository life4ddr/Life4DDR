package com.perrigogames.life4trials.manager

import com.perrigogames.life4.data.DifficultyClass
import com.perrigogames.life4.data.Song

/**
 * A Manager class designed to handle tournament data, like song of the week.
 */
class TournamentManager: BaseManager() {

    //FIXME obviously wants API support
    fun getCurrentSongOfWeek() = Song(
        "Astrogazer", 18, DifficultyClass.CHALLENGE, 0,
        "https://zenius-i-vanisher.com/simfiles/DanceDanceRevolution%20A%20%28AC%29%20%28BETA%29/Astrogazer/Astrogazer-jacket.png"
    )
}
