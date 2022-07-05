package com.perrigogames.life4.android.view.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle

@Composable
fun ManualScoreInput(
    lastDifficultyClass: DifficultyClass? = null
) {
    var songName by remember { mutableStateOf("") }
    var score by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = songName,
            onValueChange = { songName = it },
            label = { Text("Song Name") },
            modifier = Modifier.fillMaxWidth(),
        )
        CompositeDropdown(PlayStyle.values().map { it.name })
        CompositeDropdown(
            values = DifficultyClass.values().map { it.name },
            initialSelected = lastDifficultyClass?.ordinal,
        )
        Divider()
        OutlinedTextField(
            value = score,
            onValueChange = { score = it },
            label = { Text("Score") },
            modifier = Modifier.fillMaxWidth(),
        )
        CompositeDropdown(ClearType.values().map { it.name })
    }
}

@Composable
fun CompositeDropdown(
    values: List<String>,
    initialSelected: Int? = null,
) {
    var index by remember { mutableStateOf(initialSelected ?: 0) }
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            values[index],
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = true })
                .padding(16.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(),
        ) {
            PlayStyle.values().forEachIndexed { idx, style ->
                DropdownMenuItem(
                    onClick = {
                        index = idx
                        expanded = false
                    }
                ) {
                    Text(style.name)
                }
            }
        }
    }
}

@Preview
@Composable
fun ManualScoreInputPreview() {
    AppCompatTheme {
        ManualScoreInput()
    }
}