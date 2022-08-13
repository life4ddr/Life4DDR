package com.perrigogames.life4.android.activity.base

import android.os.Bundle
import android.text.Spanned
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.perrigogames.life4.android.util.spannedText
import com.perrigogames.life4.db.aggregateDiffStyleString
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.model.IgnoreListManager
import com.perrigogames.life4.model.SongDataManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseTextListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LazyColumn {
                item {
                    AndroidView(factory = { context ->
                        val text = with(StringBuilder()) {
                            buildText(this)
                            toString()
                        }
                        TextView(context).apply {
                            setText(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))
                        }
                    })
                }
            }
        }
    }

    abstract fun buildText(builder: StringBuilder)
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
        songDataManager.chartsGroupedBySong.forEach { (song, charts) ->
            val difficulties = charts
                .filter { it.playStyle == PlayStyle.SINGLE }
                .joinToString(separator = " / ") {
                    it.difficultyNumber.toString()
                }
            builder.append("${song.title}  /  ${song.artist}<br>${song.skillId}<br>$difficulties<br>")
        }
    }
}
