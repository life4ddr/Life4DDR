package com.perrigogames.life4.android.feature.trial

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.perrigogames.life4.MR
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.drawableRes
import com.perrigogames.life4.android.ui.Life4Divider
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.data.trialrecords.UITrialMocks
import com.perrigogames.life4.data.trialrecords.UITrialRecord
import com.perrigogames.life4.data.trialrecords.UITrialRecordSong
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.colorRes
import com.perrigogames.life4.feature.trialrecords.TrialRecordsViewModel
import dev.icerock.moko.resources.compose.colorResource

@Composable
fun TrialRecordsList(viewModel: TrialRecordsViewModel) {
    val records by viewModel.records.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(records) { record ->
            TrialRecordItem(
                record = record,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TrialRecordItem(
    record: UITrialRecord,
    expanded: Boolean = false,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(expanded) }
    val arrowRotationDegrees by remember {
        derivedStateOf { if (expanded) 0.25f else 0f }
    }

    Column(
        modifier = modifier
            .clickable { expanded = !expanded }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(record.rank.drawableRes),
                contentDescription = null, // FIXME
                modifier = Modifier.size(48.dp)
            )
            SizedSpacer(8.dp)
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = record.trialTitleText,
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    if (record.trialSubtitleText != null) {
                        SizedSpacer(4.dp)
                        Text(
                            text = record.trialSubtitleText!!,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(com.perrigogames.life4.R.string.ex_colon),
                        style = MaterialTheme.typography.labelMedium,
                    )
                    SizedSpacer(2.dp)
                    Text(record.exScoreText)
                }
            }
            Icon(
                imageVector = Icons.Default.MoreVert,
                modifier = Modifier.rotate(arrowRotationDegrees),
                contentDescription = if (expanded) "expanded" else "collapsed"
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 4.dp),
            ) {
                Life4Divider()
                SizedSpacer(4.dp)
                LazyColumn {
                    items(record.trialSongs) { song ->
                        TrialRecordSongItem(
                            song = song,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
        LinearProgressIndicator(
            color = colorResource(MR.colors.colorAccent),
            trackColor = MaterialTheme.colorScheme.surface,
            progress = record.exProgressPercent,
            modifier = Modifier
                .height(4.dp)
                .fillMaxWidth(),
        )
    }
}

@Composable
fun TrialRecordSongItem(
    song: UITrialRecordSong,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = song.songTitleText,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(0.75f),
        )
        Box(
            modifier = Modifier
                .width(6.dp)
                .height(24.dp)
                .background(colorResource(song.difficultyClass.colorRes)),
        )
        SizedSpacer(4.dp)
        Text(
            text = song.scoreText,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(0.25f),
        )
    }
}

@Composable
@Preview(widthDp = 480)
fun TrialRecordItemPreview() {
    LIFE4Theme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            TrialRecordItem(
                record = UITrialMocks.createUITrialRecord()
            )
        }
    }
}

@Composable
@Preview(widthDp = 480)
fun TrialRecordItemExpandedPreview() {
    LIFE4Theme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            TrialRecordItem(
                record = UITrialMocks.createUITrialRecord(),
                expanded = true,
            )
        }
    }
}

@Composable
@Preview(widthDp = 480)
fun TrialSongItemPreview() {
    LIFE4Theme {
        Surface(color = MaterialTheme.colorScheme.background) {
            LazyColumn {
                items(DifficultyClass.values()) { difficulty ->
                    TrialRecordSongItem(
                        song = UITrialMocks.createUITrialRecordSong(difficultyClass = difficulty),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}