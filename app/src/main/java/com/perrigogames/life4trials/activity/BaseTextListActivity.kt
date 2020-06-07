package com.perrigogames.life4trials.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4.db.SongDatabaseHelper
import com.perrigogames.life4.db.aggregateDiffStyleString
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.model.IgnoreListManager
import com.perrigogames.life4trials.R
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

    private val ignoreListManager: IgnoreListManager by inject()

    override fun buildText(builder: StringBuilder) {
        ignoreListManager.getCurrentlyIgnoredSongs().forEach {
            builder.append("(${it.id}) ${it.version} - ${it.title}\n")
        }
        ignoreListManager.getCurrentlyIgnoredCharts().forEach { entry ->
            entry.value.forEach { chart ->
                builder.append("(${entry.key.id}) ${entry.key.version} - ${entry.key.title} (${chart.aggregateDiffStyleString})\n")
            }
        }
    }
}

class SongRecordsListCheckActivity: BaseTextListActivity(), KoinComponent {

    private val songDb: SongDatabaseHelper by inject()

    override fun buildText(builder: StringBuilder) {
        songDb.allSongs().forEach { song ->
            val difficulties = songDb.selectChartsForSong(song.id, PlayStyle.SINGLE)
                .joinToString { "${it.difficultyClass.toString().substring(0, 2)} ${it.difficultyNumber}" }
            builder.append("${song.title} - $difficulties\n")
        }
    }
}
