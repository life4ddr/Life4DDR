package com.perrigogames.life4trials.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.manager.SongDataManager
import com.perrigogames.life4trials.repo.SongRepo
import kotlinx.android.synthetic.main.activity_block_list_check.*
import org.koin.core.KoinComponent
import org.koin.core.inject

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

class BlockListCheckActivity: BaseTextListActivity(), KoinComponent {

    private val songDataManager: SongDataManager by inject()

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

class SongRecordsListCheckActivity: BaseTextListActivity(), KoinComponent {

    private val songRepo: SongRepo by inject()

    override fun buildText(builder: StringBuilder) {
        songRepo.getSongs().forEach { song ->
            val difficulties = song.charts
                .filter { it.playStyle == PlayStyle.SINGLE }
                .joinToString { "${it.difficultyClass.toString().substring(0, 2)} ${it.difficultyNumber}" }
            builder.append("${song.title} - $difficulties\n")
        }
    }
}
