package com.perrigogames.life4trials.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.PlayStyle
import com.perrigogames.life4trials.life4app
import kotlinx.android.synthetic.main.activity_block_list_check.*

abstract class BaseTextListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_list_check)

        text_songs.text = with(StringBuilder()) {
            buildText(this)
            toString()
        }
    }

    abstract fun buildText(builder: StringBuilder)
}

class BlockListCheckActivity: BaseTextListActivity() {

    private val songDataManager get() = life4app.songDataManager

    override fun buildText(builder: StringBuilder) {
        songDataManager.getCurrentlyIgnoredSongs().forEach {
            builder.append("(${it.id}) ${it.version} - ${it.title}\n")
        }
        songDataManager.getCurrentlyIgnoredCharts().forEach {
            val target = it.song.target
            builder.append("(${it.id}) ${target.version} - ${target.title} (${it.difficultyClass})\n")
        }
    }
}

class SongRecordsListCheckActivity: BaseTextListActivity() {

    private val songRepo get() = life4app.songRepo

    override fun buildText(builder: StringBuilder) {
        songRepo.getSongs().forEach { song ->
            val difficulties = song.charts
                .filter { it.playStyle == PlayStyle.SINGLE }
                .joinToString { "${it.difficultyClass.toString().substring(0, 2)} ${it.difficultyNumber}" }
            builder.append("${song.title} - $difficulties\n")
        }
    }
}