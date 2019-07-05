package com.perrigogames.life4trials.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.util.openWebUrlFromRes
import com.perrigogames.life4trials.view.SongView

import kotlinx.android.synthetic.main.activity_single_song_tournament.*
import kotlinx.android.synthetic.main.content_single_song_tournament.*

/**
 * An Activity designed to show off details for a tournament of which there
 * is one song (like a "song of the week") and provide an option to capture
 * and submit it.
 */
class SingleSongTournamentActivity : AppCompatActivity() {

    val tournamentManager get() = life4app.tournamentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_song_tournament)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        (view_song_details as SongView).let {
            it.song = tournamentManager.getCurrentSongOfWeek()
            it.shouldShowCamera = false
        }
        button_submit.setOnClickListener { openWebUrlFromRes(R.string.url_song_of_week) }
    }
}
