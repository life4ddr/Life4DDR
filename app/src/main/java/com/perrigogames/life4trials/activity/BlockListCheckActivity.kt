package com.perrigogames.life4trials.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.life4app
import kotlinx.android.synthetic.main.activity_block_list_check.*

class BlockListCheckActivity : AppCompatActivity() {

    private val songDataManager get() = life4app.songDataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_list_check)

        text_songs.text = with(StringBuilder()) {
            songDataManager.getSongsById(songDataManager.selectedIgnoreSongIds).forEach {
                append("${it.id} - ${it.title}\n")
            }
            songDataManager.getChartsById(songDataManager.selectedIgnoreChartIds).forEach {
                append("${it.id} - ${it.song.target.title} ${it.difficultyClass}\n")
            }
            toString()
        }
    }
}