package com.perrigogames.life4.android.activity.base

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.databinding.ActivityBlockListCheckBinding
import com.perrigogames.life4.android.util.spannedText
import com.perrigogames.life4.db.aggregateDiffStyleString
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.model.IgnoreListManager
import com.perrigogames.life4.model.SongDataManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseTextListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBlockListCheckBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        binding = ActivityBlockListCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.textSongs.text = with(StringBuilder()) {
            buildText(this)
            toString().spannedText
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    abstract fun buildText(builder: StringBuilder)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}

class BlockListCheckActivity: BaseTextListActivity(), KoinComponent {

    private val ignoreListManager: IgnoreListManager by inject()

    override fun buildText(builder: StringBuilder) {
        ignoreListManager.currentlyIgnoredCharts.forEach { entry ->
            with(entry) { builder.append("($id) $version - $title ($aggregateDiffStyleString)<br>") }
        }
    }
}

class SongRecordsListCheckActivity: BaseTextListActivity(), KoinComponent {

    private val songDataManager: SongDataManager by inject()

    override fun buildText(builder: StringBuilder) {
        songDataManager.groupedCharts.forEach { (song, charts) ->
            val difficulties = charts
                .filter { it.playStyle == PlayStyle.SINGLE }
                .joinToString(separator = " / ") {
                    it.difficultyNumber.toString()
                }
            builder.append("${song.title}  /  ${song.artist}<br>${song.skillId}<br>$difficulties<br>")
        }
    }
}
