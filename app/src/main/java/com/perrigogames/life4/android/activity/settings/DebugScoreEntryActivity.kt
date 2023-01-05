package com.perrigogames.life4.android.activity.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.perrigogames.life4.GameConstants
import com.perrigogames.life4.android.activity.settings.ui.theme.LIFE4DDRTheme
import com.perrigogames.life4.android.compose.Paddings
import java.lang.Integer.max
import java.lang.Long.max
import java.lang.Long.min

class DebugScoreEntryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LIFE4DDRTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Controls()
                }
            }
        }
    }
}

@Composable
fun Controls() {
    var levelExpanded by remember { mutableStateOf(false) }
    var levelSelected by remember { mutableStateOf(0) }
    var songCount by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0L) }
    var allExceptChecked by remember { mutableStateOf(false) }

    @Composable
    fun ScoreButton(
        buttonScore: Long,
        buttonTitle: String = buttonScore.toString(),
        modifier: Modifier = Modifier,
    ) {
        Button(
            onClick = { score = buttonScore },
            modifier = modifier,
        ) {
            Text(buttonTitle)
        }
    }

    Column {
        Text("Chart level: ")
        Row {
            Text(
                text = levelSelected.toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { levelExpanded = true })
                    .background(Color.Gray)
            )
            DropdownMenu(
                expanded = levelExpanded,
                onDismissRequest = {
                    levelExpanded = false
                }
            ) {
                (1..GameConstants.HIGHEST_DIFFICULTY).forEach { level ->
                    DropdownMenuItem(onClick = {
                        levelSelected = level
                        levelExpanded = false
                    }) {
                        Text(level.toString())
                    }
                }
            }
        }

        Text("Score", modifier = Modifier.padding(top = Paddings.MEDIUM))
        Row {
            TextField(
                value = score.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { input ->
                    score = max(0, min(GameConstants.MAX_SCORE.toLong(), input.toLong()))
                }
            )
        }
        Row {
            ScoreButton(0L, modifier = Modifier.weight(1f, true))
            ScoreButton(500000L, "500k", modifier = Modifier.weight(1f, true))
            ScoreButton(800000L, "800k", modifier = Modifier.weight(1f, true))
            ScoreButton(900000L, "900k", modifier = Modifier.weight(1f, true))
        }
        Row {
            ScoreButton(990000L, "990k", modifier = Modifier.weight(1f, true))
            ScoreButton(999500L, "PFC", modifier = Modifier.weight(1f, true))
            ScoreButton(1000000L, "MFC", modifier = Modifier.weight(1f, true))
        }

        Text("Song Count", modifier = Modifier.padding(top = Paddings.MEDIUM))
        Row {
            Button(onClick = { songCount = max(0, songCount - 1) }) {
                Text("-")
            }
            TextField(
                value = songCount.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { input ->
                    songCount = max(0, input.toInt())
                }
            )
            Button(onClick = { songCount += 1 }) {
                Text("+")
            }
        }

        Row {
            Checkbox(
                checked = allExceptChecked,
                onCheckedChange = { allExceptChecked = it },
                modifier = Modifier.align(Alignment.CenterVertically),
            )
            Text(
                text = "All except",
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }

        Button(
            onClick = {
                val specifier = SongSpecifier(
                    count = songCount,
                    allExcept = allExceptChecked,
                )
                submitScore(levelSelected, score, specifier)
            },
            modifier = Modifier.padding(top = Paddings.MEDIUM)
        ) {
            Text("Submit")
        }
    }
}

private fun submitScore(
    level: Int,
    score: Long,
    specifier: SongSpecifier,
) {
    // TODO implement a function to search for and add a score
}

private data class SongSpecifier(
    val count: Int?,
    val allExcept: Boolean = false,
)

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    LIFE4DDRTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Controls()
        }
    }
}