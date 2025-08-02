package com.perrigogames.life4.android.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.feature.settings.pages.songlock.SongLockPageProvider
import com.perrigogames.life4.feature.settings.pages.songlock.UISongLockPage
import com.perrigogames.life4.feature.settings.pages.songlock.UISongLockSection

@Composable
fun SongLockScreen(
    provider: SongLockPageProvider = SongLockPageProvider()
) {
    val state by provider.data.collectAsState()
    SongLockScreenContent(state)
}

@Composable
private fun SongLockScreenContent(
    data: UISongLockPage
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = data.title.toString(context),
            style = MaterialTheme.typography.titleLarge
        )
        SizedSpacer(8.dp)
        LazyColumn {
            items(data.sections) { section ->
                UISongLockSection(section)
                SizedSpacer(8.dp)
            }
        }
    }
}

@Composable
private fun UISongLockSection(
    data: UISongLockSection
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(
            text = data.title.toString(context),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .clickable { expanded = !expanded }
                .fillMaxWidth()
                .padding(16.dp)
        )
        if (expanded) {
            Column(
                modifier = Modifier.clickable { expanded = !expanded }
                    .padding(start = 16.dp)
            ) {
                data.charts.forEach { chart ->
                    Text(
                        text = chart.toString(context)
                    )
                }
            }
        }
    }
}