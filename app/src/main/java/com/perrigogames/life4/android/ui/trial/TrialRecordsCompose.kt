package com.perrigogames.life4.android.ui.trial

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.perrigogames.life4.db.TrialSession
import com.perrigogames.life4.db.TrialSong

@Composable
fun TrialRecordsList() {
    LazyColumn() {

    }
}

@Composable
fun TrialRecordsListItem(session: TrialSession, songs: List<TrialSong>) {
    Column {
        Row {
            Image()
            Column {
                Text()
                Text()
            }
        }
        LinearProgressIndicator()
        Column {
            songs.forEach { trialSong ->
                TrialSongItem(
                    trialSong = trialSong,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun TrialSongItem(
    trialSong: TrialSong,
    modifier: Modifier = Modifier,
) {
    Row {
        Text()
        Surface(
            color = Color.Red,
            modifier = Modifier.width(6.dp)
        ) {
            Text()
        }
        )
    }
}