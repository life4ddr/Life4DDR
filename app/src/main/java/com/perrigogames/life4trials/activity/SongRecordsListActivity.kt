package com.perrigogames.life4trials.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4.model.IgnoreListManager
import com.perrigogames.life4trials.R
import kotlinx.android.synthetic.main.activity_block_list_check.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class SongRecordsListActivity : AppCompatActivity(), KoinComponent {

    private val ignoreListManager: IgnoreListManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_list_check) // debug screen, reuse old layout idgafos

        text_songs.text = with(StringBuilder()) {
            ignoreListManager.getCurrentlyIgnoredSongs().forEach {
                append("(${it.id}) ${it.version} - ${it.title}\n")
            }
            ignoreListManager.getCurrentlyIgnoredCharts().forEach { entry ->
                entry.value.forEach { chart ->
                    append("(${entry.key.id}) ${entry.key.version} - ${entry.key.title} (${chart.difficultyClass})\n")
                }
            }
            toString()
        }
    }
}
