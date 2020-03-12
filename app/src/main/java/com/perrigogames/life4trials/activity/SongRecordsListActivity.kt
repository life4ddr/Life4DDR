package com.perrigogames.life4trials.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.manager.SongDataManager
import kotlinx.android.synthetic.main.activity_block_list_check.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class SongRecordsListActivity : AppCompatActivity(), KoinComponent {

    private val songDataManager: SongDataManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_list_check) // debug screen, reuse old layout idgafos

        text_songs.text = with(StringBuilder()) {
            songDataManager.getCurrentlyIgnoredSongs().forEach {
                append("(${it.id}) ${it.version} - ${it.title}\n")
            }
            songDataManager.getCurrentlyIgnoredCharts().forEach {
                val target = it.song.target
                append("(${it.id}) ${target.version} - ${target.title} (${it.difficultyClass})\n")
            }
            toString()
        }
    }
}
